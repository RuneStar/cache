package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class VarcType extends ConfigType {

    public static final int GROUP = 19;

    public boolean persist = false;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 2:
                    persist = true;
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
