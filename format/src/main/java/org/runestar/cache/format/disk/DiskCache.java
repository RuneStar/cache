package org.runestar.cache.format.disk;

import org.runestar.cache.format.MutableCache;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class DiskCache implements MutableCache, Closeable {

    private static final String DAT_FILE_NAME = "main_file_cache.dat2";

    private static final String IDX_FILE_NAME = "main_file_cache.idx";

    private final Path directory;

    private final DatFile datFile;

    private final IdxFile[] idxFiles = new IdxFile[MASTER_ARCHIVE + 1];

    private DiskCache(Path directory) throws IOException {
        this.directory = directory;
        datFile = DatFile.open(directory.resolve(DAT_FILE_NAME));
    }

    private IdxFile getIdxFile(int archive) throws IOException {
        var f = idxFiles[archive];
        if (f == null) {
            idxFiles[archive] = f = IdxFile.open(directory.resolve(IDX_FILE_NAME + archive));
        }
        return f;
    }

    @Override public synchronized CompletableFuture<Integer> getArchiveCount() {
        try {
            return CompletableFuture.completedFuture(getIdxFile(MASTER_ARCHIVE).size());
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override public synchronized CompletableFuture<ByteBuffer> getGroupCompressed(int archive, int group) {
        try {
            var e = getIdxFile(archive).read(group);
            if (e == null) return CompletableFuture.completedFuture(null);
            return CompletableFuture.completedFuture(datFile.read(archive, group, e.length, e.sector));
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override public synchronized CompletableFuture<Void> setGroupCompressed(int archive, int group, ByteBuffer buf) {
        try {
            getIdxFile(archive).write(group, buf.remaining(), datFile.append(archive, group, buf));
            return CompletableFuture.completedFuture(null);
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
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
