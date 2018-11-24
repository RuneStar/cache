package org.runestar.cache.tools;

import org.runestar.cache.format.IO;
import org.runestar.cache.format.fs.FileStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class DumpScriptBinaries {

    public static void main(String[] args) throws IOException {
        var dir = Path.of("input");
        Files.createDirectories(dir);

        try (var fs = FileStore.open(Paths.get(".cache"))) {
            var futures = new ArrayList<CompletableFuture<?>>();
            var ia = fs.getIndexAttributes(12).join();
            for (var a : ia.archives) {
                var f = fs.getArchiveDecompressed(12, a.id)
                        .thenApply(bb -> {
                            try {
                                return Files.write(dir.resolve("" + a.id), IO.content(bb));
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw null;
                            }
                        });
                futures.add(f);
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }
}
