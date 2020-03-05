package org.runestar.cache.tools;

import org.runestar.cache.format.Cache;
import org.runestar.cache.format.disk.DiskCache;
import org.runestar.cache.format.net.NetCache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

public class UpdateCache {

    public static void main(String[] args) throws IOException {
        var start = Instant.now();

        try (var net = NetCache.connect(new InetSocketAddress("oldschool7.runescape.com", NetCache.DEFAULT_PORT), 188);
             var disk = DiskCache.open(Path.of(".cache"))) {
            Cache.update(net, disk).join();
        }

        System.out.println(Duration.between(start, Instant.now()));
    }
}
