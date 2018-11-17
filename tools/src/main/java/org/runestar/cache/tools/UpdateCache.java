package org.runestar.cache.tools;

import org.runestar.cache.format.fs.FileStore;
import org.runestar.cache.format.net.NetStore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

public class UpdateCache {

    public static void main(String[] args) throws IOException {
        var start = Instant.now();

        try (var net = NetStore.connect(new InetSocketAddress("oldschool7.runescape.com", 43594), 176);
             var fs = FileStore.open(Paths.get(".cache"))) {
            net.update(fs).join();
        }

        System.out.println(Duration.between(start, Instant.now()).toMillis());
    }
}
