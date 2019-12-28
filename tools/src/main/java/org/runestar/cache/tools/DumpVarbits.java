package org.runestar.cache.tools;

import org.runestar.cache.content.config.VarBitType;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class DumpVarbits {
    public static void main(String[] args) throws IOException {

        var lines = new ArrayList<String>();
        lines.add("id\tstartBit\tendBit\tbaseVar");

        try (var disk = DiskCache.open(Path.of(".cache"))) {
            MemCache cache = MemCache.of(disk);
            for (var file : cache.archive(VarBitType.ARCHIVE).group(VarBitType.GROUP).files()) {
                var varbit = new VarBitType();
                varbit.decode(file.data());
                lines.add("" + file.id() + '\t' + varbit.startBit + '\t' + varbit.endBit + '\t' + varbit.baseVar);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Files.write(Path.of("varbits.tsv"), lines);
    }
}
