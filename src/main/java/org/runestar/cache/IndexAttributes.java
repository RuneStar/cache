package org.runestar.cache;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class IndexAttributes {

    public final int version;

    public final ArchiveAttributes[] archives;

    public final int[] archiveIds;

    public IndexAttributes(int version, ArchiveAttributes[] archives, int[] archiveIds) {
        this.version = version;
        this.archives = archives;
        this.archiveIds = archiveIds;
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
        var maxArchiveId = archiveIds[archiveIds.length - 1];
        var archiveNameHashes = hasNames ? IO.getIntSlice(buf, archiveCount) : null;
        var archiveCrs = IO.getIntSlice(buf, archiveCount);
        var archiveVersion = IO.getIntSlice(buf, archiveCount);
        var fileCounts = IO.getShortSlice(buf, archiveCount);

        var fileIds = new int[archiveCount][];
        for (var a = 0; a < archiveCount; a++) {
            var fi = 0;
            var fc = Short.toUnsignedInt(fileCounts.get(a));
            fileIds[a] = new int[fc];
            for (var f = 0; f < fc; f++) {
                fi = Short.toUnsignedInt(buf.getShort());
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

        var archives = new ArchiveAttributes[maxArchiveId + 1];
        for (var a = 0; a < archiveCount; a++) {
            int[] fnhs = null;
            if (hasNames) {
                var fids = fileIds[a];
                var maxFileId = fids[fids.length - 1];
                fnhs = new int[maxFileId + 1];
                var fc = Short.toUnsignedInt(fileCounts.get(a));
                for (var f = 0; f < fc; f++) {
                    fnhs[fids[f]] = fileNameHashes[a].get(f);
                }
            }
            var anh = hasNames ? archiveNameHashes.get(a) : 0;
            archives[archiveIds[a]] = new ArchiveAttributes(anh, archiveCrs.get(a), archiveVersion.get(a), fnhs, fileIds[a]);
        }
        return new IndexAttributes(version, archives, archiveIds);
    }

    public static final class ArchiveAttributes {

        public final int nameHash;

        public final int crc;

        public final int version;

        public final int[] fileNameHashes;

        public final int[] fileIds;

        public ArchiveAttributes(int nameHash, int crc, int version, int[] fileNameHashes, int[] fileIds) {
            this.nameHash = nameHash;
            this.crc = crc;
            this.version = version;
            this.fileNameHashes = fileNameHashes;
            this.fileIds = fileIds;
        }
    }
}
