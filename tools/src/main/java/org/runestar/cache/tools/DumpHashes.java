package org.runestar.cache.tools;

import org.runestar.cache.format.fs.FileStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DumpHashes {

    public static void main(String[] args) throws IOException {
        var lines = new ArrayList<String>();
        try (var fs = FileStore.open(Paths.get(".cache"))) {
            var indexCount = fs.getIndexCount().join();
            for (var i = 0; i < indexCount; i++) {
                var ia = fs.getIndexAttributes(i).join();
                for (var a : ia.archives) {
                    if (a.nameHash != 0) {
                        lines.add("" + i + '\t' + a.id + "\t-1\t" + a.nameHash);
                        for (var f : a.files) {
                            if (f.nameHash != 0) {
                                lines.add("" + i + '\t' + a.id + '\t' + f.id + '\t' + f.nameHash);
                            }
                        }
                    }
                }
            }
        }

        Files.write(Path.of("name-hashes.tsv"), lines);
    }
}
