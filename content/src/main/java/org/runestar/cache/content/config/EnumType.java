package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class EnumType extends ConfigType {

    public static final int GROUP = 8;

    public byte inputtype = 0;

    public byte outputtype = 0;

    public int outputcount = 0;

    public int[] keys = null;

    public int defaultint = 0;

    public String defaultstr = null;

    public int[] intvals = null;

    public String[] strvals = null;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1:
                    inputtype = in.g1s();
                    break;
                case 2:
                    outputtype = in.g1s();
                    break;
                case 3:
                    defaultstr = in.gjstr();
                    break;
                case 4:
                    defaultint = in.g4s();
                    break;
                case 5:
                    outputcount = in.g2();
                    keys = new int[outputcount];
                    strvals = new String[outputcount];
                    for (int i = 0; i < outputcount; i++) {
                        keys[i] = in.g4s();
                        strvals[i] = in.gjstr();
                    }
                    break;
                case 6:
                    outputcount = in.g2();
                    keys = new int[outputcount];
                    intvals = new int[outputcount];
                    for (int i = 0; i < outputcount; i++) {
                        keys[i] = in.g4s();
                        intvals[i] = in.g4s();
                    }
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }

    public int getInt(int key) {
        for (var i = 0; i < keys.length; i++) {
            if (keys[i] == key) return intvals[i];
        }
        return defaultint;
    }

    public String getString(int key) {
        for (var i = 0; i < keys.length; i++) {
            if (keys[i] == key) return strvals[i];
        }
        return defaultstr;
    }
}
