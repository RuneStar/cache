package org.runestar.cache.content.graphics;

import org.runestar.cache.content.CacheType;
import org.runestar.cache.content.io.Input;

public final class GraphicsDefaults extends CacheType {

    public static final int ARCHIVE = 17;

    public int compass = -1;

    public int mapedge = -1;

    public int mapscene = -1;

    public int headiconspk = -1;

    public int headiconsprayer = -1;

    public int headiconshint = -1;

    public int mapmarker = -1;

    public int cross = -1;

    public int mapdots = -1;

    public int scrollbar = -1;

    public int modicons = -1;

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
                    compass = in.gSmart2or4();
                    mapedge = in.gSmart2or4();
                    mapscene = in.gSmart2or4();
                    headiconspk = in.gSmart2or4();
                    headiconsprayer = in.gSmart2or4();
                    headiconshint = in.gSmart2or4();
                    mapmarker = in.gSmart2or4();
                    cross = in.gSmart2or4();
                    mapdots = in.gSmart2or4();
                    scrollbar = in.gSmart2or4();
                    modicons = in.gSmart2or4();
            }
        }
    }
}
