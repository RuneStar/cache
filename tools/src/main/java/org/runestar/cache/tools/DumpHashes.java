package org.runestar.cache.tools;

import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class DumpHashes {

    public static void main(String[] args) throws IOException {
        var lines = new ArrayList<String>();
        try (var disk = DiskCache.open(Path.of(".cache"))) {
            var archiveCount = disk.getArchiveCount().join();
            for (var i = 0; i < archiveCount; i++) {
                var index = disk.getIndex(i).join();
                for (var g : index.groups) {
                    if (g.nameHash != 0) {
                        lines.add("" + i + '\t' + g.id + "\t-1\t" + g.nameHash);
                        for (var f : g.files) {
                            if (f.nameHash != 0) {
                                lines.add("" + i + '\t' + g.id + '\t' + f.id + '\t' + f.nameHash);
                            }
                        }
                    }
                }
            }
        }

        Files.write(Path.of("name-hashes.tsv"), lines);
    }
}
