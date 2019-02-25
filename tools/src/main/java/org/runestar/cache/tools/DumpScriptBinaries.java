package org.runestar.cache.tools;

import org.runestar.cache.format.IO;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DumpScriptBinaries {

    public static void main(String[] args) throws IOException {
        var dir = Path.of("gen", "input");
        Files.createDirectories(dir);

        try (var disk = DiskCache.open(Paths.get(".cache"))) {
            var cache = MemCache.of(disk);
            for (var group : cache.archive(12).groups()) {
                var data = group.data();
                Files.write(dir.resolve("" + group.id()), IO.getArray(data, data.remaining()));
            }
        }
    }
}
