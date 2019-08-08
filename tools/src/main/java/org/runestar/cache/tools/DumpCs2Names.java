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
        NameExtractor extractor;
        try (var disk = DiskCache.open(Path.of(".cache"))) {
            extractor = new NameExtractor(disk);
            for (var file : MemCache.of(disk).archive(ConfigType.ARCHIVE).group(ParamType.GROUP).files()) {
                var param = new ParamType();
                param.decode(file.data());
                paramTypes.put(file.id(), (int) param.type);
            }
        }

        Files.createDirectories(Path.of(".cs2"));
        write("param-types.tsv", paramTypes);
        write("obj-names.tsv", extractor.objs);
        write("loc-names.tsv", extractor.locs);
        write("model-names.tsv", extractor.models);
        write("struct-names.tsv", extractor.structs);
        write("npc-names.tsv", extractor.npcs);
        write("seq-names.tsv", extractor.seqs);
        write("stat-names.tsv", extractor.stats);
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
