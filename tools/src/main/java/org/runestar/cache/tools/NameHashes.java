package org.runestar.cache.tools;

import org.runestar.cache.format.MemCache;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class NameHashes {

    private NameHashes() {}

    public static void main(String[] args) throws IOException {
        try (var disk = DiskCache.open(Path.of(".cache"))) {
            write(MemCache.of(disk), Path.of("name-hashes.tsv"));
        }
    }

    public static void write(MemCache cache, Path file) throws IOException {
        var sb = new StringBuilder();
        for (var a : cache.archives()) {
            for (var g : a.groups()) {
                if (g.nameHash() == 0) continue;
                sb.append(a.id()).append('\t').append(g.id()).append('\t').append(-1).append('\t').append(g.nameHash()).append('\n');
                for (var f : g.files()) {
                    if (f.nameHash() == 0) continue;
                    sb.append(a.id()).append('\t').append(g.id()).append('\t').append(f.id()).append('\t').append(f.nameHash()).append('\n');
                }
            }
        }
        Files.writeString(file, sb);
    }
}
