package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class InvType extends ConfigType {

    public static final int GROUP = 5;

    public int size = 0;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1:
                    size = in.g2();
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
