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
            var index = cache.index(12);
            for (var archive : index.archives()) {
                var data = archive.data();
                Files.write(dir.resolve("" + archive.id()), IO.getArray(data, data.remaining()));
            }
        }
    }
}
