package org.runestar.cache.content;

import org.runestar.cache.content.io.Input;

public final class MapElementType extends ConfigType {

    public static final int GROUP = 35;

    public byte[] _b = null;

    public int _a = Integer.MAX_VALUE;

    public int sprite2 = -1;

    public int _j = Integer.MIN_VALUE;

    public int _s = Integer.MIN_VALUE;

    public int _z = Integer.MAX_VALUE;

    public int[] _h = null;

    public int[] _i = null;

    public int category = -1;

    public int _e = 0;

    public int sprite1 = -1;

    public int textSize = 0;

    public int _y = 0;

    public String _l = null;

    public String string1 = null;

    public String[] strings = new String[5];

    public int _t = 0;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1:
                    sprite1 = in.gSmart2or4();
                    break;
                case 2:
                    sprite2 = in.gSmart2or4();
                    break;
                case 3:
                    _l = in.gjstr();
                    break;
                case 4:
                    _e = in.g3();
                    break;
                case 5:
                case 23:
                    in.skip(3);
                    break;
                case 6:
                    textSize = in.g1();
                    break;
                case 7:
                case 8:
                case 28:
                    in.skip(1);
                    break;
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                    strings[code - 10] = in.gjstr();
                    break;
                case 15: {
                    int n = in.g1();
                    _i = new int[n * 2];
                    for (int i = 0; i < n * 2; i++) {
                        _i[i] = in.g2s();
                    }
                    in.skip(4);
                    int m = in.g1();
                    _h = new int[m];
                    for (int i = 0; i < m; i++) {
                        _h[i] = in.g4s();
                    }
                    _b = new byte[n];
                    for (int i = 0; i < n; i++) {
                        _b[i] = in.g1s();
                    }
                    break;
                }
                case 17:
                    string1 = in.gjstr();
                    break;
                case 18:
                case 25:
                    in.gSmart2or4();
                    break;
                case 19:
                    category = in.g2();
                    break;
                case 21:
                case 22:
                case 24:
                    in.skip(4);
                    break;
                case 29:
                    _t = in.g1();
                    break;
                case 30:
                    _y = in.g1();
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }

    @Override protected void postDecode() {
        if (_i != null) {
            for(int i = 0; i < _i.length; i += 2) {
                if (_i[i] < _a) {
                    _a = _i[i];
                } else if (_i[i] > _j) {
                    _j = _i[i];
                }
                if (_i[i + 1] < _z) {
                    _z = _i[i + 1];
                } else if (_i[1 + i] > _s) {
                    _s = _i[i + 1];
                }
            }
        }
    }
}
