package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class FloorOverlayType extends ConfigType {

    public static final int GROUP = 4;

    public boolean _o = true;

    public int rgb = 0;

    public int rgb2 = -1;

    public int texture = -1;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1:
                    rgb = in.g3();
                    break;
                case 2:
                    texture = in.g1();
                    break;
                case 5:
                    _o = false;
                    break;
                case 7:
                    rgb2 = in.g3();
                    break;
                case 8:
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
