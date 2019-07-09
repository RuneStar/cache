package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class SpotType extends ConfigType {

    public static final int GROUP = 13;

    public int model = 0;

    public int anim = -1;

    public int resizeh = 128;

    public int resizev = 128;

    public int orientation = 0;

    public int ambient = 0;

    public int contrast = 0;

    public short[] recol_s = null;

    public short[] recol_d = null;

    public short[] retex_s = null;

    public short[] retex_d = null;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1:
                    model = in.g2();
                    break;
                case 2:
                    anim = in.g2();
                    break;
                case 4:
                    resizeh = in.g2();
                    break;
                case 5:
                    resizev = in.g2();
                    break;
                case 6:
                    orientation = in.g2();
                    break;
                case 7:
                    ambient = in.g1();
                    break;
                case 8:
                    contrast = in.g1();
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
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
