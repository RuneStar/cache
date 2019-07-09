package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class ParamType extends ConfigType {

    public static final int GROUP = 11;

    public boolean autodisable = true;

    public byte type = 0;

    public int defaultint = 0;

    public String defaultstr = null;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1:
                    type = in.g1s();
                    break;
                case 2:
                    defaultint = in.g4s();
                    break;
                case 4:
                    autodisable = false;
                    break;
                case 5:
                    defaultstr = in.gjstr();
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
