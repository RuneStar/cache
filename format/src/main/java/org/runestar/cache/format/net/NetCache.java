package org.runestar.cache.format.net;

import org.runestar.cache.format.Compressor;
import org.runestar.cache.format.IO;
import org.runestar.cache.format.MasterIndex;
import org.runestar.cache.format.ReadableCache;

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

public final class NetCache implements ReadableCache, Closeable {

    private static final int MAX_REQS = 19;

    private static final int WINDOW_SIZE = 512;

    private static final int HEADER_SIZE = 8;

    private static final byte WINDOW_DELIMITER = -1;

    private final Socket socket;

    private final BlockingQueue<Request> pendingWrites = new LinkedBlockingQueue<>();

    private final BlockingQueue<Request> pendingReads = new LinkedBlockingQueue<>(MAX_REQS);

    private Thread readThread;

    private Thread writeThread;

    private NetCache() throws SocketException {
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
            var archive = headerBuf.get();
            var group = headerBuf.getShort();
            var compressor = Compressor.of(headerBuf.get());
            var compressedSize = headerBuf.getInt();
            headerBuf.clear();
            if (archive != req.archive || group != req.group) throw new IOException();
            var resSize = HEADER_SIZE + compressedSize + compressor.headerSize;
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
    public CompletableFuture<ByteBuffer> getGroupCompressed(int archive, int group) {
        var req = new Request((byte) archive, (short) group);
        pendingWrites.add(req);
        return req.future;
    }

    @Override
    public CompletableFuture<MasterIndex> getMasterIndex() {
        return getGroup(MASTER_ARCHIVE, MASTER_ARCHIVE).thenApply(MasterIndex::read);
    }

    @Override
    public void close() {
        pendingWrites.add(Request.shutdownSentinel());
    }

    public static NetCache connect(
            SocketAddress address,
            int revision
    ) throws IOException {
        var cache = new NetCache();
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

        final byte archive;

        final short group;

        final CompletableFuture<ByteBuffer> future;

        Request(byte archive, short group) {
            this.archive = archive;
            this.group = group;
            future = new CompletableFuture<>();
        }

        private boolean isUrgent() {
            return archive == (byte) MASTER_ARCHIVE;
        }

        void writeTo(ByteBuffer buf) {
            buf.put((byte) (isUrgent() ? 1 : 0)).put(archive).putShort(group);
        }

        private Request() {
            archive = -1;
            group = -1;
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
