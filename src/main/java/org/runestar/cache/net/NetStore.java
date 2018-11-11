package org.runestar.cache.net;

import org.runestar.cache.Compressor;
import org.runestar.cache.IO;
import org.runestar.cache.ReadableStore;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.*;

public final class NetStore implements ReadableStore {

    private static final int MAX_REQS = 19;

    private static final int WINDOW_SIZE = 512;

    private static final int HEADER_SIZE = 8;

    private static final int WINDOW_DELIMITER = 0xFF;

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
        var headerArray = new byte[HEADER_SIZE];
        var headerBuf = ByteBuffer.wrap(headerArray);
        var is = new BufferedInputStream(socket.getInputStream());
        while (true) {
            var req = pendingReads.take();
            if (req.isShutdownSentinel()) return;
            IO.readBytes(is, headerArray);
            var index = headerBuf.get();
            var archive = headerBuf.getShort();
            var compressor = headerBuf.get();
            var compressedSize = headerBuf.getInt();
            headerBuf.clear();
            if (index != req.index || archive != req.archive) throw new IOException();
            var resSize = HEADER_SIZE + compressedSize + Compressor.of(compressor).headerLength;
            var resArray = Arrays.copyOf(headerArray, resSize);
            var pos = Math.min(WINDOW_SIZE, resSize);
            IO.readNBytes(is, resArray, HEADER_SIZE, pos - HEADER_SIZE);
            while (pos < resSize) {
                if (is.read() != WINDOW_DELIMITER) throw new IOException();
                var len = Math.min(WINDOW_SIZE - 1, resSize - pos);
                IO.readNBytes(is, resArray, pos, len);
                pos += len;
            }
            req.future.complete(ByteBuffer.wrap(resArray).position(3));
        }
    }

    private void write() throws InterruptedException, IOException {
        var writeArray = new byte[4];
        var writeBuf = ByteBuffer.wrap(writeArray);
        var os = socket.getOutputStream();
        while (true) {
            var req = pendingWrites.take();
            if (req.isShutdownSentinel()) {
                pendingReads.put(req);
                return;
            }
            writeBuf.put((byte) (req.index == -1 ? 1 : 0)).put(req.index).putShort(req.archive).clear();
            os.write(writeArray);
            pendingReads.put(req);
        }
    }

    @Override
    public CompletableFuture<ByteBuffer> getArchive(int index, int archive) {
        var req = new Request((byte) index, (short) archive);
        pendingWrites.add(req);
        return req.future;
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
