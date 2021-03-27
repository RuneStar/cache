package org.runestar.cache.tools;

import org.runestar.cache.format.CacheUpdate;
import org.runestar.cache.format.disk.DiskCache;
import org.runestar.cache.format.net.NetCache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

public final class UpdateCache {

    private UpdateCache() {}

    public static void main(String[] args) throws IOException {
        var start = Instant.now();

        try (var net = NetCache.connect(new InetSocketAddress("oldschool7.runescape.com", NetCache.DEFAULT_PORT), 194);
             var disk = DiskCache.open(Path.of(".cache"))) {
            CacheUpdate.update(net, disk).join();
        }

        System.out.println(Duration.between(start, Instant.now()));
    }
}
