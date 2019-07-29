package org.runestar.cache.tools;

import org.runestar.cache.content.config.ConfigType;
import org.runestar.cache.content.config.ParamType;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.SortedMap;
import java.util.TreeMap;

public class DumpCs2Names {

    public static void main(String[] args) throws IOException {
        var paramTypes = new TreeMap<Integer, Integer>();
        Names names;
        try (var disk = DiskCache.open(Path.of(".cache"))) {
            names = new Names(disk);
            for (var file : MemCache.of(disk).archive(ConfigType.ARCHIVE).group(ParamType.GROUP).files()) {
                var param = new ParamType();
                param.decode(file.data());
                paramTypes.put(file.id(), (int) param.type);
            }
        }

        Files.createDirectories(Path.of(".cs2"));
        write("param-types.tsv", paramTypes);
        write("obj-names.tsv", names.objs);
        write("loc-names.tsv", names.locs);
        write("model-names.tsv", names.models);
        write("struct-names.tsv", names.structs);
        write("npc-names.tsv", names.npcs);
        write("seq-names.tsv", names.seqs);
    }

    private static void write(String fileName, SortedMap<Integer, ?> names) throws IOException {
        try (var w = Files.newBufferedWriter(Path.of(".cs2", fileName))) {
            for (var e : names.entrySet()) {
                w.write(e.getKey().toString());
                w.write('\t');
                w.write(e.getValue().toString());
                w.write('\n');
            }
        }
    }
}
