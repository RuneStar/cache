package org.runestar.cache.format;

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
        if (version != other.version) return false;
        return Arrays.equals(groups, other.groups);
    }

    @Override public int hashCode() {
        return Arrays.hashCode(groups) ^ version;
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

    public static final class Group {

        public final int id;

        public final int nameHash;

        public final int crc;

        public final int version;

        public final File[] files;

        public Group(int id, int nameHash, int crc, int version, File[] files) {
            this.id = id;
            this.nameHash = nameHash;
            this.crc = crc;
            this.version = version;
            this.files = Objects.requireNonNull(files);
        }

        @Override public String toString() {
            return "Group(id=" + id + ", nameHash=" + nameHash + ", crc=" + crc + ", version=" + version + ", files=" + Arrays.toString(files) + ')';
        }

        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Group)) return false;
            Group other = (Group) obj;
            if (id != other.id) return false;
            if (nameHash != other.nameHash) return false;
            if (crc != other.crc) return false;
            if (version != other.version) return false;
            return Arrays.equals(files, other.files);
        }

        @Override public int hashCode() {
            return id ^ nameHash ^ crc ^ version;
        }

        public static ByteBuffer[] split(ByteBuffer buf, int fileCount) {
            var fs = new ByteBuffer[fileCount];
            if (fileCount == 1) {
                fs[0] = buf;
            } else {
                if (buf.get(buf.limit() - 1) != 1) throw new IllegalStateException();
                var fileSizes = buf.duplicate().position(buf.limit() - 1 - fileCount * Integer.BYTES);
                var fileSize = 0;
                for (var fi = 0; fi < fileCount; fi++) {
                    fs[fi] = IO.getSlice(buf, fileSize += fileSizes.getInt());
                }
                buf.position(buf.limit());
            }
            return fs;
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
            if (id != other.id) return false;
            return nameHash == other.nameHash;
        }

        @Override public int hashCode() {
            return id ^ nameHash;
        }
    }
}
