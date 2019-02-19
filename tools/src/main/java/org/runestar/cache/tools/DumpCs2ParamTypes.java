package org.runestar.cache.tools;

import org.runestar.cache.content.ParamDefinition;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DumpCs2ParamTypes {

    public static void main(String[] args) throws IOException {
        var lines = new ArrayList<String>();
        try (var disk = DiskCache.open(Paths.get(".cache"))) {
            var cache = MemCache.of(disk);
            for (var file : cache.archive(2).group(11).files()) {
                var param = new ParamDefinition();
                param.read(file.data());
                lines.add("" + file.id() + "\t" + (int) param.type);
            }
        }
        Files.write(Path.of("param-types.tsv"), lines);
    }
}
