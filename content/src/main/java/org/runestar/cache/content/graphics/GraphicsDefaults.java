package org.runestar.cache.content.graphics;

import org.runestar.cache.content.CacheType;
import org.runestar.cache.content.io.Input;

public final class GraphicsDefaults extends CacheType {

    public static final int ARCHIVE = 16;

    public int modIcons = -1;

    public int mapDots = -1;

    public int _f = -1;

    public int mapMarkers = -1;

    public int crosses = -1;

    public int _m = -1;

    public int headIconsPrayer = -1;

    public int mapScenes = -1;

    public int headIconsHint = -1;

    public int headIconsPk = -1;

    public int scrollBars = -1;

    @Override public void decode(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1:
                    in.skip(3);
                    break;
                case 2:
                    _m = in.gSmart2or4();
                    _f = in.gSmart2or4();
                    mapScenes = in.gSmart2or4();
                    headIconsPk = in.gSmart2or4();
                    headIconsPrayer = in.gSmart2or4();
                    headIconsHint = in.gSmart2or4();
                    mapMarkers = in.gSmart2or4();
                    crosses = in.gSmart2or4();
                    mapDots = in.gSmart2or4();
                    scrollBars = in.gSmart2or4();
                    modIcons = in.gSmart2or4();
            }
        }
    }
}
