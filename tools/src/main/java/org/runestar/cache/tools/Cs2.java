package org.runestar.cache.tools;

import org.runestar.cache.format.MemCache;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.SortedMap;

public final class Cs2 {

    private Cs2() {}

    public static void main(String[] args) throws IOException {
        try (var disk = DiskCache.open(Path.of(".cache"))) {
            var cache = MemCache.of(disk);
            var dir = Path.of(".cs2");
            Files.createDirectories(dir);
            var inputDir = dir.resolve("input");
            Files.createDirectories(inputDir);

            ClientScriptBinaries.write(cache, inputDir);

            writeTsv(dir.resolve("param-types.tsv"), ParamTypes.get(cache));

            var ne = new NameExtractor(cache);
            writeTsv(dir.resolve("loc-names.tsv"), ne.locs);
            writeTsv(dir.resolve("model-names.tsv"), ne.models);
            writeTsv(dir.resolve("npc-names.tsv"), ne.npcs);
            writeTsv(dir.resolve("obj-names.tsv"), ne.objs);
            writeTsv(dir.resolve("seq-names.tsv"), ne.seqs);
            writeTsv(dir.resolve("stat-names.tsv"), ne.stats);
            writeTsv(dir.resolve("struct-names.tsv"), ne.structs);
        }
    }

    private static void writeTsv(Path file, SortedMap<?, ?> map) throws IOException {
        try (var w = Files.newBufferedWriter(file)) {
            for (var e : map.entrySet()) {
                w.write(e.getKey().toString());
                w.write('\t');
                w.write(e.getValue().toString());
                w.write('\n');
            }
        }
    }
}
