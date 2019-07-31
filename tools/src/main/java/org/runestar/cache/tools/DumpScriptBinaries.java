package org.runestar.cache.tools;

import org.runestar.cache.format.IO;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DumpScriptBinaries {

    public static void main(String[] args) throws IOException {
        var dir = Path.of(".cs2", "input");
        Files.createDirectories(dir);

        try (var disk = DiskCache.open(Path.of(".cache"))) {
            var cache = MemCache.of(disk);
            for (var group : cache.archive(12).groups()) {
                var data = group.data();
                Files.write(dir.resolve("" + group.id()), IO.getArray(data, data.remaining()));
            }
        }
    }
}
