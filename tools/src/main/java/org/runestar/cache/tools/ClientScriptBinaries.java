package org.runestar.cache.tools;

import org.runestar.cache.format.MemCache;
import org.runestar.cache.format.util.IO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ClientScriptBinaries {

    private ClientScriptBinaries() {}

    public static void write(MemCache cache, Path dir) throws IOException {
        for (var group : cache.archive(12).groups()) {
            Files.write(dir.resolve("" + group.id()), IO.getArray(group.data()));
        }
    }
}
