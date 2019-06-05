package org.runestar.cache.tools;

import org.runestar.cache.content.LocType;
import org.runestar.cache.content.ObjType;
import org.runestar.cache.content.ParamType;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.SortedMap;
import java.util.TreeMap;

public class DumpCs2Names {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("gen"));
        var objNames = new TreeMap<Integer, String>();
        var locNames = new TreeMap<Integer, String>();
        var paramTypes = new TreeMap<Integer, Integer>();
        try (var disk = DiskCache.open(Path.of(".cache"))) {
            var cache = MemCache.of(disk);
            for (var file : cache.archive(2).group(10).files()) {
                var obj = new ObjType();
                obj.decode(file.data());
                var name = escape(obj.name);
                if (name == null) continue;
                objNames.put(file.id(), name);
            }

            for (var file : cache.archive(2).group(6).files()) {
                var loc = new LocType();
                loc.decode(file.data());
                var name = escape(loc.name);
                if (name != null) {
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
        }
        write("param-types.tsv", paramTypes);
        write("obj-names.tsv", objNames);
        write("loc-names.tsv", locNames);
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
        if (name.isBlank()) return null;
        return name.toLowerCase()
                .replaceAll("([']|<.*?>)", "")
                .replaceAll("[- /)(.,!]", "_")
                .replaceAll("[%&+?]", "_")
                .replaceAll("(^_+|_+$)", "")
                .replaceAll("_{2,}", "_");
    }
}
