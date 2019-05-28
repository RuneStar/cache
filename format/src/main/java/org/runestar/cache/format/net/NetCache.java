package org.runestar.cache.format.net;

import org.runestar.cache.format.Compressor;
import org.runestar.cache.format.IO;
import org.runestar.cache.format.Cache;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

public final class NetCache implements Cache, Closeable {

    private static final int MAX_REQS = 19;

    private static final int WINDOW_SIZE = 512;

    private static final int HEADER_SIZE = 8;

    private static final byte WINDOW_DELIMITER = -1;

    private final BlockingQueue<Request> pendingWrites = new LinkedBlockingQueue<>();

    private final BlockingQueue<Request> pendingReads = new LinkedBlockingQueue<>(MAX_REQS);

    private Thread readThread;

    private Thread writeThread;

    private NetCache(
            Socket socket,
            ThreadFactory threadFactory
    ) {
        readThread = threadFactory.newThread(() -> {
            try {
                read(socket.getInputStream());
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
        writeThread = threadFactory.newThread(() -> {
            try {
                write(socket.getOutputStream());
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

    private void read(InputStream in) throws InterruptedException, IOException {
        var headerBuf = ByteBuffer.allocate(HEADER_SIZE);
        while (true) {
            var req = pendingReads.take();
            if (req.isShutdownSentinel()) return;
            IO.readBytes(in, headerBuf.array());
            var archive = headerBuf.get();
            var group = headerBuf.getShort();
            var compressor = Compressor.of(headerBuf.get());
            var compressedSize = headerBuf.getInt();
            headerBuf.clear();
            if (archive != req.archive || group != req.group) throw new IOException();
            var resSize = HEADER_SIZE + compressedSize + compressor.headerSize;
            var resArray = Arrays.copyOf(headerBuf.array(), resSize);
            if (resSize <= WINDOW_SIZE) {
                IO.readNBytes(in, resArray, HEADER_SIZE, resSize - HEADER_SIZE);
            } else {
                IO.readNBytes(in, resArray, HEADER_SIZE, WINDOW_SIZE - HEADER_SIZE + 1);
                var pos = WINDOW_SIZE;
                while (true) {
                    if (resArray[pos] != WINDOW_DELIMITER) throw new IOException();
                    if (resSize - pos >= WINDOW_SIZE) {
                        IO.readNBytes(in, resArray, pos, WINDOW_SIZE);
                        pos += WINDOW_SIZE - 1;
                    } else {
                        IO.readNBytes(in, resArray, pos, resSize - pos);
                        break;
                    }
                }
            }
            req.future.completeAsync(() -> ByteBuffer.wrap(resArray).position(3));
        }
    }

    private void write(OutputStream out) throws InterruptedException, IOException {
        var writeBuf = ByteBuffer.allocate(4);
        while (true) {
            var req = pendingWrites.take();
            if (req.isShutdownSentinel()) {
                pendingReads.put(req);
                return;
            }
            req.writeTo(writeBuf.clear());
            out.write(writeBuf.array());
            pendingReads.put(req);
        }
    }

    @Override public CompletableFuture<ByteBuffer> getGroupCompressed(int archive, int group) {
        var req = new Request((byte) archive, (short) group);
        pendingWrites.add(req);
        return req.future;
    }

    @Override public void close() {
        pendingWrites.add(Request.shutdownSentinel());
    }

    public static NetCache connect(
            SocketAddress address,
            int revision
    ) throws IOException {
        var socket = new Socket();
        try {
            connect(socket, address, revision);
        } catch (IOException e) {
            IO.closeQuietly(e, socket);
            throw e;
        }
        return new NetCache(socket, Executors.defaultThreadFactory());
    }

    private static void connect(Socket socket, SocketAddress address, int revision) throws IOException {
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(30000);
        socket.connect(address);
        socket.getOutputStream().write(ByteBuffer.allocate(5).put((byte) 15).putInt(revision).array());
        if (socket.getInputStream().read() != 0) throw new IOException();
        socket.getOutputStream().write(ByteBuffer.allocate(4).put((byte) 3).put((byte) 0).putShort((short) 0).array());
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
