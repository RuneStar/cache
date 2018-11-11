package org.runestar.cache.fs;

import org.runestar.cache.IO;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class DatFile implements Closeable {

    private static final int SECTOR_SIZE = 520;

    private final FileChannel channel;

    private final ByteBuffer buf = ByteBuffer.allocate(SECTOR_SIZE);

    private DatFile(FileChannel channel) {
        this.channel = channel;
    }

    ByteBuffer read(int index, int archive, int length, int sector) throws IOException {
        var dst = ByteBuffer.allocate(length);
        var chunk = 0;
        while (dst.hasRemaining()) {
            channel.read(buf, sector * SECTOR_SIZE);
            buf.clear();
            int sectorArchive = Short.toUnsignedInt(buf.getShort());
            if (archive != sectorArchive) throw new IOException();
            var sectorChunk = Short.toUnsignedInt(buf.getShort());
            if (chunk != sectorChunk) throw new IOException();
            sector = IO.getMedium(buf);
            var sectorIndex = Byte.toUnsignedInt(buf.get());
            if (index != sectorIndex) throw new IOException();
            buf.limit(buf.position() + Math.min(buf.remaining(), dst.remaining()));
            dst.put(buf);
            buf.clear();
            chunk++;
        }
        return dst.clear();
    }

    int append(int index, int archive, ByteBuffer data) throws IOException {
        var startSector = (int) (channel.size() / SECTOR_SIZE);
        var sector = startSector;
        var lim = data.limit();
        var chunk = 0;
        while (data.hasRemaining()) {
            buf.putShort((short) archive);
            buf.putShort((short) chunk);
            IO.putMedium(buf, sector + 1);
            buf.put((byte) index);
            var len = Math.min(buf.remaining(), data.remaining());
            data.limit(data.position() + len);
            buf.put(data);
            data.limit(lim);
            buf.clear();
            channel.write(buf, sector * SECTOR_SIZE);
            buf.clear();
            chunk++;
            sector++;
        }
        return startSector;
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    static DatFile open(Path file) throws IOException {
        return new DatFile(FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ));
    }
}
