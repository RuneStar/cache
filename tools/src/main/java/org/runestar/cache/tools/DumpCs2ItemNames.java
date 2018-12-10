package org.runestar.cache.tools;

import org.runestar.cache.content.ItemDefinition;
import org.runestar.cache.format.Archive;
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
            for (var a : ia.archives) {
                if (a.id != 10) continue;
                var archiveData = fs.getArchiveDecompressed(2, 10).join();
                var fileData = Archive.split(archiveData, a.files.length);
                for (int j = 0; j < a.files.length; j++) {
                    var fileId = a.files[j].id;
                    var file = fileData[j];
                    var item = new ItemDefinition();
                    item.read(file);
                    var name = escape(item.name);
                    if (name == null) continue;
                    lines.add("" + fileId + "\t" + name + "_" + fileId);
                }
                break;
            }
        }
        Files.write(Path.of("obj-names.tsv"), lines);
    }

    public static String escape(String name) {
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
