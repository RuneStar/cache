package org.runestar.cache.tools;

import org.runestar.cache.content.ObjType;
import org.runestar.cache.content.NPCType;
import org.runestar.cache.content.LocType;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.SortedMap;
import java.util.TreeMap;

public final class GenerateClientIdClasses {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("gen"));
        try (var disk = DiskCache.open(Path.of(".cache"))) {
            var cache = MemCache.of(disk);
            var archive = cache.archive(2);

            var objNames = new TreeMap<Integer, String>();
            var objGroup = archive.group(6);
            for (var file : objGroup.files()) {
                var loc = new LocType();
                loc.decode(file.data());
                var name = escape(loc.name);
                if (name == null) continue;
                objNames.put(file.id(), name);
            }
            writeFile("ObjectId", objNames);

            var npcNames = new TreeMap<Integer, String>();
            var npcGroup = archive.group(9);
            for (var file : npcGroup.files()) {
                var npc = new NPCType();
                npc.decode(file.data());
                var name = escape(npc.name);
                if (name == null) continue;
                npcNames.put(file.id(), name);
            }
            writeFile("NpcId", npcNames);

            var itemNames = new TreeMap<Integer, String>();
            var itemGroup = archive.group(10);
            for (var file : itemGroup.files()) {
                var obj = new ObjType();
                obj.decode(file.data());
                var name = escape(obj.name);
                if (name == null) continue;
                itemNames.put(file.id(), name);
            }
            writeFile("ItemId", itemNames);
        }
    }

    private static void writeFile(String className, SortedMap<Integer, String> names) throws IOException {
        var file = Path.of("gen", className + ".java");
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
