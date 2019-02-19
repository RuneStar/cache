package org.runestar.cache.tools;

import org.runestar.cache.content.ItemDefinition;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DumpCs2ItemNames {

    public static void main(String[] args) throws IOException {
        var lines = new ArrayList<String>();
        try (var disk = DiskCache.open(Paths.get(".cache"))) {
            var cache = MemCache.of(disk);
            for (var file : cache.archive(2).group(10).files()) {
                var item = new ItemDefinition();
                item.read(file.data());
                var name = escape(item.name);
                if (name == null) continue;
                lines.add("" + file.id() + "\t" + name + "_" + file.id());
            }
        }
        Files.write(Path.of("obj-names.tsv"), lines);
    }

    private static String escape(String name) {
        if (name.equalsIgnoreCase("null")) return null;
        if (name.isBlank()) return null;
        return name.toLowerCase()
                .replaceAll("([']|<.*?>)", "")
                .replaceAll("[- /)(.,!]", "_")
                .replaceAll("[%&+?]", "_")
                .replaceAll("(^_+|_+$)", "")
                .replaceAll("_{2,}", "_");
    }
}
