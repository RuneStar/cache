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
            var ia = fs.getIndexAttributes(12).join();
            for (var a : ia.archives) {
                var bb = fs.getArchiveDecompressed(12, a.id).join();
                Files.write(dir.resolve("" + a.id), IO.content(bb));
            }
        }
    }
}
