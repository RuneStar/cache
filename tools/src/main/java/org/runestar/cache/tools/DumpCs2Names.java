package org.runestar.cache.tools;

import org.runestar.cache.content.ObjType;
import org.runestar.cache.content.LocType;
import org.runestar.cache.content.ParamType;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class DumpCs2Names {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("gen"));
        var objLines = new ArrayList<String>();
        var locLines = new ArrayList<String>();
        var paramTypeLines = new ArrayList<String>();
        try (var disk = DiskCache.open(Path.of(".cache"))) {
            var cache = MemCache.of(disk);
            for (var file : cache.archive(2).group(10).files()) {
                var obj = new ObjType();
                obj.read(file.data());
                var name = escape(obj.name);
                if (name == null) continue;
                objLines.add("" + file.id() + "\t" + name + "_" + file.id());
            }
            for (var file : cache.archive(2).group(6).files()) {
                var loc = new LocType();
                loc.read(file.data());
                var name = escape(loc.name);
                if (name == null) continue;
                locLines.add("" + file.id() + "\t" + name + "_" + file.id());
            }
            for (var file : cache.archive(2).group(11).files()) {
                var param = new ParamType();
                param.read(file.data());
                paramTypeLines.add("" + file.id() + "\t" + (int) param.type);
            }
        }
        Files.write(Path.of("gen", "param-types.tsv"), paramTypeLines);
        Files.write(Path.of("gen", "obj-names.tsv"), objLines);
        Files.write(Path.of("gen", "loc-names.tsv"), locLines);
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
