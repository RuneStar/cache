package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class HitmarkType extends ConfigType {

    public static final int GROUP = 32;

    public int sprite2 = -1;

    public int sprite3 = -1;

    public int font = -1;

    public int sprite4 = -1;

    public int sprite = -1;

    public String format = "";

    public int _c = -1;

    public int _h = -1;

    public int _i = 70;

    public int textcolor = 0xFFFFFF;

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
                    textcolor = in.g3();
                    break;
                case 3:
                    sprite2 = in.gSmart2or4();
                    break;
                case 4:
                    sprite3 = in.gSmart2or4();
                    break;
                case 5:
                    sprite = in.gSmart2or4();
                    break;
                case 6:
                    sprite4 = in.gSmart2or4();
                    break;
                case 7:
                    _t = in.g2s();
                    break;
                case 8:
                    format = in.gjstr2();
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
