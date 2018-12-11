package org.runestar.cache.tools;

import org.runestar.cache.content.ParamDefinition;
import org.runestar.cache.format.Archive;
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
            var ia = fs.getIndexAttributes(2).join();
            for (var a : ia.archives) {
                if (a.id != 11) continue;
                var archiveData = fs.getArchiveDecompressed(2, 11).join();
                var fileData = Archive.split(archiveData, a.files.length);
                for (int j = 0; j < a.files.length; j++) {
                    var fileId = a.files[j].id;
                    var file = fileData[j];
                    var param = new ParamDefinition();
                    param.read(file);
                    lines.add("" + fileId + "\t" + (int) param.type);
                }
                break;
            }
        }
        Files.write(Path.of("param-types.tsv"), lines);
    }
}
