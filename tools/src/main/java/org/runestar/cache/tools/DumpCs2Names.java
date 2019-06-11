package org.runestar.cache.tools;

import org.runestar.cache.content.EnumType;
import org.runestar.cache.content.LocType;
import org.runestar.cache.content.NPCType;
import org.runestar.cache.content.ObjType;
import org.runestar.cache.content.ParamType;
import org.runestar.cache.content.StructType;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class DumpCs2Names {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("gen"));

        var objNames = new TreeMap<Integer, String>();
        var locNames = new TreeMap<Integer, String>();
        var paramTypes = new TreeMap<Integer, Integer>();
        var modelNames = new TreeMap<Integer, String>();
        var structNames = new TreeMap<Integer, String>();
        var npcNames = new TreeMap<Integer, String>();

        try (var disk = DiskCache.open(Path.of(".cache"))) {
            var cache = MemCache.of(disk);

            var objToPrayer = new EnumType();
            objToPrayer.decode(cache.archive(2).group(8).file(496).data());
            var prayerToName = new EnumType();
            prayerToName.decode(cache.archive(2).group(8).file(860).data());
            var objModels = new HashMap<Integer, Integer>();
            for (var file : cache.archive(2).group(10).files()) {
                var obj = new ObjType();
                obj.decode(file.data());
                var name = escape(obj.name);
                if (name == null) continue;
                objNames.put(file.id(), name);
                objModels.putIfAbsent(obj.inventoryModel, file.id());
                if (obj.notedId != -1) {
                    objNames.put(obj.notedId, "cert_" + name);
                }
                if (obj.placeholderId != -1) {
                    objNames.put(obj.placeholderId, "placeholder_" + name);
                }
                if (obj.countCo != null) {
                    for (var i = 0; i < obj.countCo.length; i++) {
                        var count = obj.countCo[i];
                        if (count == 0) break;
                        var countId = obj.countObj[i];
                        objNames.putIfAbsent(countId, name + "_x" + count);
                    }
                }
            }
            for (var file : cache.archive(2).group(10).files()) {
                if (objNames.containsKey(file.id())) continue ;
                var obj = new ObjType();
                obj.decode(file.data());
                var spellName = obj.params == null ? null : (String) obj.params.get(601);
                if (spellName != null) {
                    objNames.put(file.id(), escape(spellName));
                    continue;
                }
                var prayer = objToPrayer.getInt(file.id());
                if (prayer != -1) {
                    var prayerName = prayerToName.getString(prayer);
                    objNames.put(file.id(), escape(prayerName));
                    continue;
                }
                var origId = objModels.get(obj.inventoryModel);
                if (origId != null) {
                    objNames.put(file.id(), "dummy_" + objNames.get(origId));
                }
            }

            for (var file : cache.archive(2).group(6).files()) {
                var loc = new LocType();
                loc.decode(file.data());
                var name = escape(loc.name);
                if (name != null) {
                    if (loc.models != null) {
                        for (var n : loc.models) {
                            if (n != 16238) {
                                modelNames.putIfAbsent(n, name);
                            }
                        }
                    }
                    locNames.put(file.id(), name);
                } else if (loc.transforms != null) {
                    for (var locId : loc.transforms) {
                        if (locId == -1) continue;
                        var loc2 = new LocType();
                        loc2.decode(cache.archive(2).group(6).file(locId).data());
                        var name2 = escape(loc2.name);
                        if (name2 != null) {
                            locNames.put(file.id(), name2);
                            break;
                        }
                    }
                }
            }

            for (var file : cache.archive(2).group(11).files()) {
                var param = new ParamType();
                param.decode(file.data());
                paramTypes.put(file.id(), (int) param.type);
            }

            var structNameKeys = new int[]{610,660,682,689,732};
            for (var file : cache.archive(2).group(34).files()) {
                var struct = new StructType();
                struct.decode(file.data());
                if (struct.params == null) continue;
                for (var key : structNameKeys) {
                    var value = struct.params.get(key);
                    if (value != null) {
                        structNames.put(file.id(), escape((String) value));
                        break;
                    }
                }
            }

            for (var file : cache.archive(2).group(9).files()) {
                var npc = new NPCType();
                npc.decode(file.data());
                var name = escape(npc.name);
                if (name == null) continue;
                npcNames.put(file.id(), name);
            }
        }
        write("param-types.tsv", paramTypes);
        write("obj-names.tsv", objNames);
        write("loc-names.tsv", locNames);
        write("model-names.tsv", modelNames);
        write("struct-names.tsv", structNames);
        write("npc-names.tsv", npcNames);
    }

    private static void write(String fileName, SortedMap<Integer, ?> names) throws IOException {
        try (var w = Files.newBufferedWriter(Path.of("gen", fileName))) {
            for (var e : names.entrySet()) {
                w.write(e.getKey().toString());
                w.write('\t');
                w.write(e.getValue().toString());
                w.write('\n');
            }
        }
    }

    private static String escape(String name) {
        if (name.equalsIgnoreCase("null")) return null;
        name = name.toLowerCase()
                .replaceAll("([']|<.*?>)", "")
                .replaceAll("[- /)(.,! ]", "_")
                .replaceAll("[%&+?]", "_")
                .replaceAll("(^_+|_+$)", "")
                .replaceAll("_{2,}", "_");
        if (name.isBlank()) return null;
        return name;
    }
}
