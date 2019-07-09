package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class FloorUnderlayType extends ConfigType {

    public static final int GROUP = 1;

    public int rgb = 0;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1:
                    rgb = in.g3();
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
