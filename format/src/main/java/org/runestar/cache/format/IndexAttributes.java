package org.runestar.cache.format;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
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
        var archiveIds = IO.getShortSlice(buf, archiveCount);
        var archiveNameHashes = hasNames ? IO.getIntSlice(buf, archiveCount) : null;
        var archiveCrcs = IO.getIntSlice(buf, archiveCount);
        var archiveVersions = IO.getIntSlice(buf, archiveCount);
        var fileCounts = IO.getShortSlice(buf, archiveCount);
        var fileIds = new ShortBuffer[archiveCount];
        for (var a = 0; a < archiveCount; a++) {
            fileIds[a] = IO.getShortSlice(buf, Short.toUnsignedInt(fileCounts.get(a)));
        }

        var archives = new ArchiveAttributes[archiveCount];
        var archiveId = 0;
        for (var a = 0; a < archiveCount; a++) {
            var fileCount = Short.toUnsignedInt(fileCounts.get(a));
            var files = new FileAttributes[fileCount];
            var fileId = 0;
            for (var f = 0; f < fileCount; f++) {
                var fileNameHash = hasNames ? buf.getInt() : 0;
                fileId += fileIds[a].get(f);
                files[f] = new FileAttributes(fileId, fileNameHash);
            }
            var archiveNameHash = hasNames ? archiveNameHashes.get(a) : 0;
            archiveId += archiveIds.get(a);
            archives[a] = new ArchiveAttributes(archiveId, archiveNameHash, archiveCrcs.get(a), archiveVersions.get(a), files);
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
