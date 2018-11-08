package org.runestar.cache;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public final class IndexAttributes {

    public final int version;

    public final ArchiveAttributes[] archives;

    public IndexAttributes(int version, ArchiveAttributes[] archives) {
        this.version = version;
        this.archives = archives;
    }

    @Override
    public String toString() {
        return "ArchiveAttributes(version=" + version + ", archives=" + Arrays.toString(archives) + ')';
    }

    public static IndexAttributes read(ByteBuffer buf) {
        var format = buf.get();
        if (format != 5 && format != 6) throw new IllegalArgumentException();
        var version = format >= 6 ? buf.getInt() : 0;
        var hasNames = buf.get() != 0;
        var archiveCount = Short.toUnsignedInt(buf.getShort());
        var archiveIds = new int[archiveCount];
        var ai = 0;
        for (var a = 0; a < archiveCount; a++) {
            ai += Short.toUnsignedInt(buf.getShort());
            archiveIds[a] = ai;
        }
        var archiveNameHashes = hasNames ? IO.getIntSlice(buf, archiveCount) : null;
        var archiveCrs = IO.getIntSlice(buf, archiveCount);
        var archiveVersions = IO.getIntSlice(buf, archiveCount);
        var fileCounts = IO.getShortSlice(buf, archiveCount);

        var fileIds = new int[archiveCount][];
        for (var a = 0; a < archiveCount; a++) {
            var fi = 0;
            var fc = Short.toUnsignedInt(fileCounts.get(a));
            fileIds[a] = new int[fc];
            for (var f = 0; f < fc; f++) {
                fi += Short.toUnsignedInt(buf.getShort());
                fileIds[a][f] = fi;
            }
        }

        IntBuffer[] fileNameHashes = null;
        if (hasNames) {
            fileNameHashes = new IntBuffer[archiveCount];
            for (var a = 0; a < archiveCount; a++) {
                fileNameHashes[a] = IO.getIntSlice(buf, Short.toUnsignedInt(fileCounts.get(a)));
            }
        }

        var archives = new ArchiveAttributes[archiveCount];
        for (var a = 0; a < archiveCount; a++) {
            var fc = Short.toUnsignedInt(fileCounts.get(a));
            var files = new FileAttributes[fc];
            for (var f = 0; f < fc; f++) {
                var fnh = hasNames ? fileNameHashes[a].get(f) : 0;
                files[f] = new FileAttributes(fileIds[a][f], fnh);
            }
            var anh = hasNames ? archiveNameHashes.get(a) : 0;
            archives[a] = new ArchiveAttributes(archiveIds[a], anh, archiveCrs.get(a), archiveVersions.get(a), files);
        }
        return new IndexAttributes(version, archives);
    }

    public static final class ArchiveAttributes {

        public final int id;

        public final int nameHash;

        public final int crc;

        public final int version;

        public final FileAttributes[] files;

        public ArchiveAttributes(int id, int nameHash, int crc, int version, FileAttributes[] files) {
            this.id = id;
            this.nameHash = nameHash;
            this.crc = crc;
            this.version = version;
            this.files = files;
        }

        @Override
        public String toString() {
            return "ArchiveAttributes(id=" + id +
                    ", nameHash=" + nameHash +
                    ", crc=" + crc +
                    ", version=" + version +
                    ", files=" + Arrays.toString(files) +
                    ')';
        }
    }

    public static final class FileAttributes {

        public final int id;

        public final int nameHash;

        public FileAttributes(int id, int nameHash) {
            this.id = id;
            this.nameHash = nameHash;
        }

        @Override
        public String toString() {
            return "FileAttributes(id=" + id + ", nameHash=" + nameHash + ')';
        }
    }
}
