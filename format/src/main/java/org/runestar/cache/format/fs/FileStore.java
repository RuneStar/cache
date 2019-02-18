package org.runestar.cache.format.fs;

import org.runestar.cache.format.WritableStore;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class FileStore implements WritableStore, Closeable {

    private static final String DAT_FILE_NAME = "main_file_cache.dat2";

    private static final String IDX_FILE_NAME = "main_file_cache.idx";

    private final Path directory;

    private final DatFile datFile;

    private final IndexFile[] indexFiles = new IndexFile[1 << Byte.SIZE];

    private FileStore(Path directory) throws IOException {
        Files.createDirectories(directory);
        this.directory = directory;
        datFile = DatFile.open(directory.resolve(DAT_FILE_NAME));
    }

    private IndexFile getIndexFile(int index) throws IOException {
        var f = indexFiles[index];
        if (f == null) {
            indexFiles[index] = f = IndexFile.open(directory.resolve(IDX_FILE_NAME + index));
        }
        return f;
    }

    @Override
    public synchronized CompletableFuture<Integer> getIndexCount() {
        try {
            return CompletableFuture.completedFuture(getIndexFile(META_INDEX).size());
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public synchronized CompletableFuture<ByteBuffer> getArchiveCompressed(int index, int archive) {
        try {
            var idxe = getIndexFile(index).read(archive);
            if (idxe == null) return CompletableFuture.completedFuture(null);
            var buf = datFile.read(index, archive, idxe.length, idxe.sector);
            return CompletableFuture.completedFuture(buf);
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public synchronized CompletableFuture<Void> setArchiveCompressed(int index, int archive, ByteBuffer buf) {
        if (index == META_INDEX && archive == META_INDEX) throw new IllegalArgumentException();
        var length = buf.remaining();
        try {
            var sector = datFile.append(index, archive, buf);
            getIndexFile(index).write(archive, length, sector);
            return CompletableFuture.completedFuture(null);
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        datFile.close();
        for (var f : indexFiles) {
            if (f != null) f.close();
        }
    }

    public static FileStore open(Path directory) throws IOException {
        return new FileStore(directory);
    }
}
