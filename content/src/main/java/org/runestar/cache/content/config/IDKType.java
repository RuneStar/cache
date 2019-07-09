package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class IDKType extends ConfigType {

    public static final int GROUP = 3;

    public int bodyPart = -1;

    public final int[] head = {-1, -1, -1, -1, -1};

    public int[] models = null;

    public short[] retex_s = null;

    public short[] recol_s = null;

    public short[] recol_d = null;

    public short[] retex_d = null;

    public boolean _k = false;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1:
                    bodyPart = in.g1();
                    break;
                case 2: {
                    int n = in.g1();
                    models = new int[n];
                    for (int i = 0; i < n; i++) {
                        models[i] = in.g2();
                    }
                    break;
                }
                case 3:
                    _k = true;
                    break;
                case 40: {
                    int n = in.g1();
                    recol_s = new short[n];
                    recol_d = new short[n];
                    for (int i = 0; i < n; i++) {
                        recol_s[i] = in.g2s();
                        recol_d[i] = in.g2s();
                    }
                    break;
                }
                case 41: {
                    int n = in.g1();
                    retex_s = new short[n];
                    retex_d = new short[n];
                    for (int i = 0; i < n; i++) {
                        retex_s[i] = in.g2s();
                        retex_d[i] = in.g2s();
                    }
                    break;
                }
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                    head[code - 60] = in.g2();
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
