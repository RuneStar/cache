package org.runestar.cache.format.fs;

import org.runestar.cache.format.IO;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

final class IndexFile implements Closeable {

    private static final int ENTRY_LENGTH = 6;

    private final FileChannel channel;

    private final ByteBuffer buf = ByteBuffer.allocate(ENTRY_LENGTH);

    private IndexFile(FileChannel channel) {
        this.channel = channel;
    }

    int size() throws IOException {
        return (int) (channel.size() / ENTRY_LENGTH);
    }

    Entry read(int archive) throws IOException {
        var pos = archive * ENTRY_LENGTH;
        var fileLength = channel.size();
        if (pos + ENTRY_LENGTH > fileLength) return null;
        channel.read(buf, pos);
        buf.clear();
        var entry = new Entry(IO.getMedium(buf), IO.getMedium(buf));
        buf.clear();
        if (entry.length == 0) return null;
        return entry;
    }

    void write(int archive, int length, int sector) throws IOException {
        IO.putMedium(buf, length);
        IO.putMedium(buf, sector);
        buf.clear();
        channel.write(buf, archive * ENTRY_LENGTH);
        buf.clear();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    static IndexFile open(Path file) throws IOException {
        return new IndexFile(FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ));
    }

    static final class Entry {

        final int length;

        final int sector;

        Entry(int length, int sector) {
            this.length = length;
            this.sector = sector;
        }
    }
}
