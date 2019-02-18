package org.runestar.cache.format.net;

import org.runestar.cache.format.Compressor;
import org.runestar.cache.format.IO;
import org.runestar.cache.format.IndexVersion;
import org.runestar.cache.format.ReadableStore;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public final class NetStore implements ReadableStore, Closeable {

    private static final int MAX_REQS = 19;

    private static final int WINDOW_SIZE = 512;

    private static final int HEADER_SIZE = 8;

    private static final byte WINDOW_DELIMITER = -1;

    private final Socket socket;

    private final BlockingQueue<Request> pendingWrites = new LinkedBlockingQueue<>();

    private final BlockingQueue<Request> pendingReads = new LinkedBlockingQueue<>(MAX_REQS);

    private Thread readThread;

    private Thread writeThread;

    private NetStore() throws SocketException {
        socket = new Socket();
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(30000);
    }

    private void connect0(SocketAddress address, int revision) throws IOException {
        socket.connect(address);
        socket.getOutputStream().write(ByteBuffer.allocate(5).put((byte) 15).putInt(revision).array());
        if (socket.getInputStream().read() != 0) throw new IOException();
        socket.getOutputStream().write(ByteBuffer.allocate(4).put((byte) 3).put((byte) 0).putShort((short) 0).array());
    }

    private void startHandlers() {
        var factory = Executors.defaultThreadFactory();
        readThread = factory.newThread(() -> {
            try {
                read();
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                writeThread.interrupt();
                IO.closeQuietly(e, socket);
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        writeThread = factory.newThread(() -> {
            try {
                write();
            } catch (IOException e) {
                readThread.interrupt();
                IO.closeQuietly(e, socket);
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        readThread.start();
        writeThread.start();
    }

    private void read() throws InterruptedException, IOException {
        var headerBuf = ByteBuffer.allocate(HEADER_SIZE);
        var is = socket.getInputStream();
        while (true) {
            var req = pendingReads.take();
            if (req.isShutdownSentinel()) return;
            IO.readBytes(is, headerBuf.array());
            var index = headerBuf.get();
            var archive = headerBuf.getShort();
            var compressor = Compressor.of(headerBuf.get());
            var compressedSize = headerBuf.getInt();
            headerBuf.clear();
            if (index != req.index || archive != req.archive) throw new IOException();
            var resSize = HEADER_SIZE + compressedSize + compressor.headerSize();
            var resArray = Arrays.copyOf(headerBuf.array(), resSize);
            if (resSize <= WINDOW_SIZE) {
                IO.readNBytes(is, resArray, HEADER_SIZE, resSize - HEADER_SIZE);
            } else {
                IO.readNBytes(is, resArray, HEADER_SIZE, WINDOW_SIZE - HEADER_SIZE + 1);
                var pos = WINDOW_SIZE;
                while (true) {
                    if (resArray[pos] != WINDOW_DELIMITER) throw new IOException();
                    if (resSize - pos >= WINDOW_SIZE) {
                        IO.readNBytes(is, resArray, pos, WINDOW_SIZE);
                        pos += WINDOW_SIZE - 1;
                    } else {
                        IO.readNBytes(is, resArray, pos, resSize - pos);
                        break;
                    }
                }
            }
            req.future.completeAsync(() -> ByteBuffer.wrap(resArray).position(3));
        }
    }

    private void write() throws InterruptedException, IOException {
        var writeBuf = ByteBuffer.allocate(4);
        var os = socket.getOutputStream();
        while (true) {
            var req = pendingWrites.take();
            if (req.isShutdownSentinel()) {
                pendingReads.put(req);
                return;
            }
            req.writeTo(writeBuf.clear());
            os.write(writeBuf.array());
            pendingReads.put(req);
        }
    }

    @Override
    public CompletableFuture<ByteBuffer> getArchiveCompressed(int index, int archive) {
        var req = new Request((byte) index, (short) archive);
        pendingWrites.add(req);
        return req.future;
    }

    @Override
    public CompletableFuture<IndexVersion[]> getIndexVersions() {
        return getArchive(META_INDEX, META_INDEX).thenApply(IndexVersion::readAll);
    }

    @Override
    public void close() {
        pendingWrites.add(Request.shutdownSentinel());
    }

    public static NetStore connect(
            SocketAddress address,
            int revision
    ) throws IOException {
        var cache = new NetStore();
        try {
            cache.connect0(address, revision);
        } catch (IOException e) {
            IO.closeQuietly(e, cache.socket);
            throw e;
        }
        cache.startHandlers();
        return cache;
    }

    private final static class Request {

        final byte index;

        final short archive;

        final CompletableFuture<ByteBuffer> future;

        Request(byte index, short archive) {
            this.index = index;
            this.archive = archive;
            future = new CompletableFuture<>();
        }

        private boolean isHighPriority() {
            return index == (byte) META_INDEX;
        }

        void writeTo(ByteBuffer buf) {
            buf.put((byte) (isHighPriority() ? 1 : 0)).put(index).putShort(archive);
        }

        private Request() {
            index = -1;
            archive = -1;
            future = null;
        }

        boolean isShutdownSentinel() {
            return future == null;
        }

        static Request shutdownSentinel() {
            return new Request();
        }
    }
}
