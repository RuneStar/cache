package org.runestar.cache.format.disk;

import org.runestar.cache.format.util.IO;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

final class IdxFile implements Closeable {

    private static final int ENTRY_SIZE = 6;

    private final FileChannel channel;

    private final ByteBuffer buf = ByteBuffer.allocate(ENTRY_SIZE);

    IdxFile(Path file) throws IOException {
        channel = FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
    }

    int size() throws IOException {
        return (int) (channel.size() / ENTRY_SIZE);
    }

    Entry read(int group) throws IOException {
        int pos = group * ENTRY_SIZE;
        if (pos + ENTRY_SIZE > channel.size()) return null;
        channel.read(buf, pos);
        buf.clear();
        int length = IO.getMedium(buf);
        int sector = IO.getMedium(buf);
        buf.clear();
        if (length <= 0 && sector == 0) return null;
        return new Entry(length, sector);
    }

    void write(int group, int length, int sector) throws IOException {
        IO.putMedium(buf, length);
        IO.putMedium(buf, sector);
        channel.write(buf.clear(), group * ENTRY_SIZE);
        buf.clear();
    }

    @Override public void close() throws IOException {
        channel.close();
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
