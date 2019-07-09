package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class HeadbarType extends ConfigType {

    public static final int GROUP = 33;

    public int spritefront = -1;

    public int spriteback = -1;

    public int width = 30;

    public int int2 = 255;

    public int int5 = 70;

    public int int1 = 255;

    public int int3 = -1;

    public int widthPadding = 0;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1:
                    in.skip(2);
                    break;
                case 2:
                    int1 = in.g1();
                    break;
                case 3:
                    int2 = in.g1();
                    break;
                case 4:
                    int3 = 0;
                    break;
                case 5:
                    int5 = in.g2();
                    break;
                case 6:
                    in.skip(1);
                    break;
                case 7:
                    spritefront = in.gSmart2or4();
                    break;
                case 8:
                    spriteback = in.gSmart2or4();
                    break;
                case 11:
                    int3 = in.g2();
                    break;
                case 14:
                    width = in.g1();
                    break;
                case 15:
                    widthPadding = in.g1();
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
