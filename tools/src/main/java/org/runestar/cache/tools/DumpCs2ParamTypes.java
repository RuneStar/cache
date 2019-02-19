package org.runestar.cache.tools;

import org.runestar.cache.content.ParamDefinition;
import org.runestar.cache.format.fs.FileStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DumpCs2ParamTypes {

    public static void main(String[] args) throws IOException {
        var lines = new ArrayList<String>();
        try (var fs = FileStore.open(Paths.get(".cache"))) {
            var cache = MemCache.of(fs);
            for (var fileId : cache.getFileIds(2, 11)) {
                var file = cache.getFile(2, 11, fileId);
                var param = new ParamDefinition();
                param.read(file);
                lines.add("" + fileId + "\t" + (int) param.type);
            }
        }
        Files.write(Path.of("param-types.tsv"), lines);
    }
}
