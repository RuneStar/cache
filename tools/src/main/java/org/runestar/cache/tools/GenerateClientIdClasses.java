package org.runestar.cache.tools;

import org.runestar.cache.content.ItemDefinition;
import org.runestar.cache.content.NpcDefinition;
import org.runestar.cache.content.ObjectDefinition;
import org.runestar.cache.format.Archive;
import org.runestar.cache.format.fs.FileStore;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedMap;
import java.util.TreeMap;

public final class GenerateClientIdClasses {

    public static void main(String[] args) throws IOException {
        try (var fs = FileStore.open(Paths.get(".cache"))) {
            var ia = fs.getIndexAttributes(2).join();
            for (var a : ia.archives) {
                ByteBuffer archiveData;
                ByteBuffer[] fileData;
                TreeMap<Integer, String> names;
                switch (a.id) {
                    case 6:
                        archiveData = fs.getArchiveDecompressed(2, 6).join();
                        fileData = Archive.split(archiveData, a.files.length);
                        names = new TreeMap<>();
                        for (int j = 0; j < a.files.length; j++) {
                            var fileId = a.files[j].id;
                            var file = fileData[j];
                            var obj = new ObjectDefinition();
                            obj.read(file);
                            var name = escape(obj.name);
                            if (name == null) continue;
                            names.put(fileId, name);
                        }
                        writeFile("ObjectId", names);
                        break;
                    case 9:
                        archiveData = fs.getArchiveDecompressed(2, 9).join();
                        fileData = Archive.split(archiveData, a.files.length);
                        names = new TreeMap<>();
                        for (int j = 0; j < a.files.length; j++) {
                            var fileId = a.files[j].id;
                            var file = fileData[j];
                            var npc = new NpcDefinition();
                            npc.read(file);
                            var name = escape(npc.name);
                            if (name == null) continue;
                            names.put(fileId, name);
                        }
                        writeFile("NpcId", names);
                        break;
                    case 10:
                        archiveData = fs.getArchiveDecompressed(2, 10).join();
                        fileData = Archive.split(archiveData, a.files.length);
                        names = new TreeMap<>();
                        for (int j = 0; j < a.files.length; j++) {
                            var fileId = a.files[j].id;
                            var file = fileData[j];
                            var item = new ItemDefinition();
                            item.read(file);
                            var name = escape(item.name);
                            if (name == null) continue;
                            names.put(fileId, name);
                        }
                        writeFile("ItemId", names);
                        break;

                }
            }
        }
    }

    private static void writeFile(String className, SortedMap<Integer, String> names) throws IOException {
        var file = Path.of(className + ".java");
        try (var writer = Files.newBufferedWriter(file)) {
            writer.write("package org.runestar.client.game.api;\n\n");
            writer.write("public final class " + className + " {\n\n");
            writer.write("\tprivate " + className + "() {}\n\n");
            writer.write("\tpublic static final int\n");
            int i = 0;
            for (var e : names.entrySet()) {
                writer.write("\t\t\t" + e.getValue() + "_" + e.getKey() + "=" + e.getKey());
                if (i == names.size() - 1) {
                    writer.write(';');
                } else {
                    writer.write(',');
                }
                writer.write('\n');
                i++;
            }
            writer.write('}');
        }
    }

    private static String escape(String name) {
        if (name.equalsIgnoreCase("null")) return null;
        if (name.isBlank()) return null;
        name = name.toUpperCase()
                .replaceAll("([']|<.*?>)", "")
                .replaceAll("[- /)(.,!]", "_")
                .replaceAll("[%&+?]", "_")
                .replaceAll("(^_+|_+$)", "")
                .replaceAll("_{2,}", "_");
        if (name.isBlank()) return null;
        if (Character.isDigit(name.charAt(0))) {
            name = '_' + name;
        }
        return name;
    }
}
