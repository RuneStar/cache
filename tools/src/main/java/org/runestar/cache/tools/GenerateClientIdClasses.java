package org.runestar.cache.tools;

import org.runestar.cache.content.ItemDefinition;
import org.runestar.cache.content.NpcDefinition;
import org.runestar.cache.content.ObjectDefinition;
import org.runestar.cache.format.fs.FileStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedMap;
import java.util.TreeMap;

public final class GenerateClientIdClasses {

    public static void main(String[] args) throws IOException {
        try (var fs = FileStore.open(Paths.get(".cache"))) {
            var ia = fs.getIndexAttributes(2).join();

            var objFiles = fs.getFiles(ia.archives.get(6), 2, 6).join();
            var objNames = new TreeMap<Integer, String>();
            for (var e : objFiles.entrySet()) {
                var fileId = e.getKey();
                var buf = e.getValue();
                var obj = new ObjectDefinition();
                obj.read(buf);
                var name = escape(obj.name);
                if (name == null) continue;
                objNames.put(fileId, name);
            }
            writeFile("ObjectId", objNames);

            var npcFiles = fs.getFiles(ia.archives.get(9), 2, 9).join();
            var npcNames = new TreeMap<Integer, String>();
            for (var e : npcFiles.entrySet()) {
                var fileId = e.getKey();
                var buf = e.getValue();
                var npc = new NpcDefinition();
                npc.read(buf);
                var name = escape(npc.name);
                if (name == null) continue;
                npcNames.put(fileId, name);
            }
            writeFile("NpcId", npcNames);

            var itemFiles = fs.getFiles(ia.archives.get(10), 2, 10).join();
            var itemNames = new TreeMap<Integer, String>();
            for (var e : itemFiles.entrySet()) {
                var fileId = e.getKey();
                var buf = e.getValue();
                var item = new ItemDefinition();
                item.read(buf);
                var name = escape(item.name);
                if (name == null) continue;
                itemNames.put(fileId, name);
            }
            writeFile("ItemId", itemNames);
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
