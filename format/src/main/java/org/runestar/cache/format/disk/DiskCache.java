package org.runestar.cache.format.disk;

import org.runestar.cache.format.WritableCache;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class DiskCache implements WritableCache, Closeable {

    private static final String DAT_FILE_NAME = "main_file_cache.dat2";

    private static final String IDX_FILE_NAME = "main_file_cache.idx";

    private final Path directory;

    private final DatFile datFile;

    private final IdxFile[] idxFiles = new IdxFile[MASTER_ARCHIVE + 1];

    private DiskCache(Path directory) throws IOException {
        Files.createDirectories(directory);
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

    @Override
    public synchronized CompletableFuture<Integer> getArchiveCount() {
        try {
            return CompletableFuture.completedFuture(getIdxFile(MASTER_ARCHIVE).size());
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public synchronized CompletableFuture<ByteBuffer> getGroupCompressed(int archive, int group) {
        try {
            var idxe = getIdxFile(archive).read(group);
            if (idxe == null) return CompletableFuture.completedFuture(null);
            var buf = datFile.read(archive, group, idxe.length, idxe.sector);
            return CompletableFuture.completedFuture(buf);
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public synchronized CompletableFuture<Void> setGroupCompressed(int archive, int group, ByteBuffer buf) {
        if (archive == MASTER_ARCHIVE && group == MASTER_ARCHIVE) throw new IllegalArgumentException();
        var length = buf.remaining();
        try {
            var sector = datFile.append(archive, group, buf);
            getIdxFile(archive).write(group, length, sector);
            return CompletableFuture.completedFuture(null);
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        datFile.close();
        for (var f : idxFiles) {
            if (f != null) f.close();
        }
    }

    public static DiskCache open(Path directory) throws IOException {
        return new DiskCache(directory);
    }
}
