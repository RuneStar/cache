package org.runestar.cache.content;

import org.runestar.cache.content.io.Input;

public final class HitmarkType extends ConfigType {

    public static final int GROUP = 32;

    public int _a = -1;

    public int _j = -1;

    public int font = -1;

    public int _s = -1;

    public int _z = -1;

    public String _b = "";

    public int _c = -1;

    public int _h = -1;

    public int _i = 70;

    public int _n = 16777215;

    public int _r = 0;

    public int _t = 0;

    public int _y = 0;

    public int[] multi = null;

    public int multivarbit = -1;

    public int multivar = -1;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1:
                    font = in.gSmart2or4();
                    break;
                case 2:
                    _n = in.g3();
                    break;
                case 3:
                    _a = in.gSmart2or4();
                    break;
                case 4:
                    _j = in.gSmart2or4();
                    break;
                case 5:
                    _z = in.gSmart2or4();
                    break;
                case 6:
                    _s = in.gSmart2or4();
                    break;
                case 7:
                    _t = in.g2s();
                    break;
                case 8:
                    _b = in.gjstr2();
                    break;
                case 9:
                    _i = in.g2();
                    break;
                case 10:
                    _y = in.g2s();
                    break;
                case 11:
                    _h = 0;
                    break;
                case 12:
                    _c = in.g1();
                    break;
                case 13:
                    _r = in.g2s();
                    break;
                case 14:
                    _h = in.g2();
                    break;
                case 17:
                case 18:
                    multivarbit = in.g2m();
                    multivar = in.g2m();
                    int last = -1;
                    if (code == 18) last = in.g2m();
                    int n = in.g1();
                    multi = new int[n + 2];
                    for(int i = 0; i <= n; i++) {
                        multi[i] = in.g2m();
                    }
                    multi[n + 1] = last;
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
