package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

public final class Index {

    public final int version;

    public final Group[] groups;

    public Index(int version, Group[] groups) {
        this.version = version;
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "Index(version=" + version + ", groups=" + Arrays.toString(groups) + ')';
    }

    public static Index read(ByteBuffer buf) {
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
            this.files = files;
        }

        @Override
        public String toString() {
            return "Group(id=" + id +
                    ", nameHash=" + nameHash +
                    ", crc=" + crc +
                    ", version=" + version +
                    ", files=" + Arrays.toString(files) +
                    ')';
        }

        public ByteBuffer[] split(ByteBuffer buf) {
            var fs = new ByteBuffer[files.length];
            if (files.length == 1) {
                fs[0] = buf;
            } else {
                if (buf.get(buf.limit() - 1) != 1) throw new IllegalStateException();
                var fileSizes = buf.duplicate().position(buf.limit() - 1 - files.length * Integer.BYTES);
                var fileSize = 0;
                for (var fi = 0; fi < files.length; fi++) {
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

        @Override
        public String toString() {
            return "File(id=" + id + ", nameHash=" + nameHash + ')';
        }
    }
}
