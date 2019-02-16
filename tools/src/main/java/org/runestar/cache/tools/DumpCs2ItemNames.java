package org.runestar.cache.tools;

import org.runestar.cache.content.ItemDefinition;
import org.runestar.cache.format.fs.FileStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DumpCs2ItemNames {

    public static void main(String[] args) throws IOException {
        var lines = new ArrayList<String>();
        try (var fs = FileStore.open(Paths.get(".cache"))) {
            var ia = fs.getIndexAttributes(2).join();
            var files = fs.getFiles(ia.archives.get(10), 2, 10).join();
            for (var e : files.entrySet()) {
                var fileId = e.getKey();
                var buf = e.getValue();
                var item = new ItemDefinition();
                item.read(buf);
                var name = escape(item.name);
                if (name == null) continue;
                lines.add("" + fileId + "\t" + name + "_" + fileId);
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
