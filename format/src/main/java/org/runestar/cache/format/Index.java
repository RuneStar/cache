package org.runestar.cache.format;

import org.runestar.cache.format.util.IO;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Objects;

public final class Index {

    public final int version;

    public final Group[] groups;

    public Index(int version, Group[] groups) {
        this.version = version;
        this.groups = Objects.requireNonNull(groups);
    }

    @Override public String toString() {
        return "Index(version=" + version + ", groups=" + Arrays.toString(groups) + ')';
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Index)) return false;
        Index other = (Index) obj;
        return version == other.version && Arrays.equals(groups, other.groups);
    }

    @Override public int hashCode() {
        return version ^ Arrays.hashCode(groups);
    }

    public static Index decode(ByteBuffer buf) {
        var protocol = buf.get();
        if (protocol != 5 && protocol != 6) throw new IllegalArgumentException();
        var version = protocol >= 6 ? buf.getInt() : 0;
        var hasNames = buf.get() != 0;
        var groupCount = Short.toUnsignedInt(buf.getShort());
        var groupIds = IO.getShortSlice(buf, groupCount);
        var groupNameHashes = hasNames ? IO.getIntSlice(buf, groupCount) : null;
        var groupCrcs = IO.getIntSlice(buf, groupCount);
        var groupVersions = IO.getIntSlice(buf, groupCount);
        var fileCounts = IO.getShortSlice(buf, groupCount);
        var fileIds = new ShortBuffer[groupCount];
        for (var a = 0; a < groupCount; a++) {
            fileIds[a] = IO.getShortSlice(buf, Short.toUnsignedInt(fileCounts.get(a)));
        }

        var groups = new Group[groupCount];
        var groupId = 0;
        for (var a = 0; a < groupCount; a++) {
            var fileCount = Short.toUnsignedInt(fileCounts.get(a));
            var files = new File[fileCount];
            var fileId = 0;
            for (var f = 0; f < fileCount; f++) {
                var fileNameHash = hasNames ? buf.getInt() : 0;
                fileId += fileIds[a].get(f);
                files[f] = new File(fileId, fileNameHash);
            }
            var groupNameHash = hasNames ? groupNameHashes.get(a) : 0;
            groupId += groupIds.get(a);
            groups[a] = new Group(groupId, groupNameHash, groupCrcs.get(a), groupVersions.get(a), files);
        }
        return new Index(version, groups);
    }

    public ByteBuffer encode() {
        var hasNames = hasNames();
        int len = 1 + 4 + 1 + 2 + groups.length * (2 + (hasNames ? 4 : 0) + 4 + 4 + 2);
        for (var g : groups) {
            len += g.files.length * (2 + (hasNames ? 4 : 0));
        }
        var buf = ByteBuffer.allocate(len);
        buf.put((byte) 6);
        buf.putInt(version);
        buf.put((byte) (hasNames ? 1 : 0));
        buf.putShort((short) groups.length);
        int lastGroup = 0;
        for (var g : groups) {
            buf.putShort((short) (g.id - lastGroup));
            lastGroup = g.id;
        }
        if (hasNames) {
            for (var g : groups) {
                buf.putInt(g.nameHash);
            }
        }
        for (var g : groups) {
            buf.putInt(g.crc32);
        }
        for (var g : groups) {
            buf.putInt(g.version);
        }
        for (var g : groups) {
            buf.putShort((short) g.files.length);
        }
        for (var g : groups) {
            int lastFile = 0;
            for (var f : g.files) {
                buf.putShort((short) (f.id - lastFile));
                lastFile = f.id;
            }
        }
        if (hasNames) {
            for (var g : groups) {
                for (var f : g.files) {
                    buf.putInt(f.nameHash);
                }
            }
        }
        return buf.flip();
    }

    private boolean hasNames() {
        for (var g : groups) {
            if (g.nameHash != 0) return true;
            for (var f : g.files) {
                if (f.nameHash != 0) return true;
            }
        }
        return false;
    }

    public static final class Group {

        public final int id;

        public final int nameHash;

        public final int crc32;

        public final int version;

        public final File[] files;

        public Group(int id, int nameHash, int crc32, int version, File[] files) {
            this.id = id;
            this.nameHash = nameHash;
            this.crc32 = crc32;
            this.version = version;
            this.files = Objects.requireNonNull(files);
        }

        @Override public String toString() {
            return "Group(id=" + id + ", nameHash=" + nameHash + ", crc32=" + crc32 + ", version=" + version + ", files=" + Arrays.toString(files) + ')';
        }

        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Group)) return false;
            Group other = (Group) obj;
            return id == other.id
                    && nameHash == other.nameHash
                    && crc32 == other.crc32
                    && version == other.version
                    && Arrays.equals(files, other.files);
        }

        @Override public int hashCode() {
            return crc32;
        }

        public static ByteBuffer[] split(ByteBuffer group, int fileCount) {
            Objects.requireNonNull(group);
            if (fileCount == 1) return new ByteBuffer[]{group};
            var fs = new ByteBuffer[fileCount];
            int chunkCount = Byte.toUnsignedInt(group.get(group.limit() - 1));
            var chunks = new ByteBuffer[fileCount][chunkCount];
            var chunkSizes = group.duplicate().position(group.limit() - 1 - chunkCount * fileCount * Integer.BYTES);
            for (int c = 0; c < chunkCount; c++) {
                int chunkSize = 0;
                for (int f = 0; f < fileCount; f++) {
                    chunks[f][c] = IO.getSlice(group, chunkSize += chunkSizes.getInt());
                }
            }
            group.position(group.limit());
            for (int f = 0; f < fileCount; f++) {
                fs[f] = IO.join(chunks[f]);
            }
            return fs;
        }

        public static ByteBuffer merge(ByteBuffer[] files) {
            if (files.length == 1) return files[0];
            int len = 1;
            for (var f : files) {
                len += Integer.BYTES + f.remaining();
            }
            var dst = ByteBuffer.allocate(len);
            var sizes = dst.duplicate().position(dst.limit() - 1 - files.length * Integer.BYTES);
            int lastFileSize = 0;
            for (var f : files) {
                sizes.putInt(f.remaining() - lastFileSize);
                lastFileSize = f.remaining();
                dst.put(f.duplicate());
            }
            dst.put(dst.limit() - 1, (byte) 1);
            return dst.rewind();
        }
    }

    public static final class File {

        public final int id;

        public final int nameHash;

        public File(int id, int nameHash) {
            this.id = id;
            this.nameHash = nameHash;
        }

        @Override public String toString() {
            return "File(id=" + id + ", nameHash=" + nameHash + ')';
        }

        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof File)) return false;
            File other = (File) obj;
            return id == other.id && nameHash == other.nameHash;
        }

        @Override public int hashCode() {
            return id ^ nameHash;
        }
    }
}
