package org.runestar.cache.format.disk;

import org.runestar.cache.format.LocalCache;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DiskCache implements LocalCache {

    private static final String DAT_FILE_NAME = "main_file_cache.dat2";

    private static final String IDX_FILE_NAME = "main_file_cache.idx";

    public final Path directory;

    private final DatFile datFile;

    private final IdxFile[] idxFiles = new IdxFile[MASTER_ARCHIVE + 1];

    private DiskCache(Path directory) throws IOException {
        this.directory = directory;
        datFile = new DatFile(directory.resolve(DAT_FILE_NAME));
    }

    private IdxFile getIdxFile(int archive) throws IOException {
        var f = idxFiles[archive];
        if (f == null) {
            idxFiles[archive] = f = new IdxFile(directory.resolve(IDX_FILE_NAME + archive));
        }
        return f;
    }

    @Override public synchronized int getArchiveCount() {
        try {
            return getIdxFile(MASTER_ARCHIVE).size();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override public synchronized ByteBuffer getGroupCompressed(int archive, int group) {
        try {
            var e = getIdxFile(archive).read(group);
            return e == null ? null : datFile.read(archive, group, e.length, e.sector);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override public synchronized void setGroupCompressed(int archive, int group, ByteBuffer buf) {
        try {
            getIdxFile(archive).write(group, buf.remaining(), datFile.append(archive, group, buf));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override public synchronized void close() throws IOException {
        datFile.close();
        for (var f : idxFiles) {
            if (f != null) f.close();
        }
    }

    public static DiskCache open(Path directory) throws IOException {
        Files.createDirectories(directory);
        return new DiskCache(directory);
    }

    @Override public String toString() {
        return "DiskCache(" + directory + ')';
    }
}
