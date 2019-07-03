package org.runestar.cache.tools;

import org.runestar.cache.content.AnimBase;
import org.runestar.cache.content.AnimFrame;
import org.runestar.cache.content.Buf;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Path;

public class Anims {

    public static void main(String[] args) throws IOException {
        try (var disk = DiskCache.open(Path.of(".cache"))) {
            var cache = MemCache.of(disk);

            for (var g : cache.archive(AnimFrame.ARCHIVE).groups()) {
                for (var f : g.files()) {
                    var frameData = f.data();
                    int baseId = Buf.getUnsignedShort(frameData);
                    var baseData = cache.archive(AnimBase.ARCHIVE).group(baseId).data();

                    var base = new AnimBase();
                    base.decode(baseData);

                    var frame = new AnimFrame(base);
                    frame.decode(frameData);
                }
            }
        }
    }
}