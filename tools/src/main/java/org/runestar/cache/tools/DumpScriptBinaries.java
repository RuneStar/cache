package org.runestar.cache.tools;

import org.runestar.cache.format.IO;
import org.runestar.cache.format.fs.FileStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DumpScriptBinaries {

    public static void main(String[] args) throws IOException {
        var dir = Path.of("input");
        Files.createDirectories(dir);

        try (var fs = FileStore.open(Paths.get(".cache"))) {
            var cache = MemCache.of(fs);
            for (var a : cache.getArchiveIds(12)) {
                var buf = cache.getArchive(12, a);
                Files.write(dir.resolve("" + a), IO.getArray(buf, buf.remaining()));
            }
        }
    }
}
