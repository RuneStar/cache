package org.runestar.cache.fs;

import org.runestar.cache.WritableStore;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public final class FileStore implements WritableStore {

    private static final String DAT_FILE_NAME = "main_file_cache.dat2";

    private static final String IDX_FILE_NAME = "main_file_cache.idx";

    private final Path directory;

    private final DatFile datFile;

    private final Map<Integer, IndexFile> indexFiles = new TreeMap<>();

    private FileStore(Path directory) throws IOException {
        Files.createDirectories(directory);
        this.directory = directory;
        datFile = DatFile.open(directory.resolve(DAT_FILE_NAME));
    }

    private IndexFile getIndexFile(int index) throws IOException {
        var f = indexFiles.get(index);
        if (f == null) {
            f = IndexFile.open(directory.resolve(IDX_FILE_NAME + index));
            indexFiles.put(index, f);
        }
        return f;
    }

    @Override
    public CompletableFuture<Integer> getIndexCount() throws IOException {
        return CompletableFuture.completedFuture(getIndexFile(255).size());
    }

    @Override
    public CompletableFuture<ByteBuffer> getArchive(int index, int archive) throws IOException {
        var idxe = getIndexFile(index).read(archive);
        if (idxe == null) return CompletableFuture.completedFuture(null);
        var buf = datFile.read(archive, idxe.length, idxe.sector);
        return CompletableFuture.completedFuture(buf);
    }

    @Override
    public void setArchive(int index, int archive, ByteBuffer buf) throws IOException {
        if (index == 0xFF && archive == 0xFF) throw new IllegalArgumentException();
        var length = buf.remaining();
        var sector = datFile.append(index, archive, buf);
        getIndexFile(index).write(archive, length, sector);
    }

    @Override
    public void close() throws IOException {
        datFile.close();
        for (var f : indexFiles.values()) {
            f.close();
        }
    }

    public static FileStore open(Path directory) throws IOException {
        return new FileStore(directory);
    }
}
