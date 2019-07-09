package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

import java.util.Map;

public final class ObjType extends ConfigType {

    public static final int GROUP = 10;

    public String name = "null";

    public int resizex = 128;

    public int resizey = 128;

    public int resizez = 128;

    public int xan2d = 0;

    public int yan2d = 0;

    public int zan2d = 0;

    public int cost = 0;

    public boolean stockmarket = false;

    public int stackable = 0;

    public int model = 0;

    public boolean members = false;

    public short[] recol_s = null;

    public short[] recol_d = null;

    public short[] retex_s = null;

    public short[] retex_d = null;

    public int zoom2d = 200_000;

    public int xof2d = 0;

    public int yof2d = 0;

    public int ambient = 0;

    public int contrast = 0;

    public int[] countco = null;

    public int[] countobj = null;

    public final String[] op = {null, null, "Take", null, null};

    public final String[] iop = {null, null, null, null, "Drop"};

    public int manwear = -1;

    public int manwear2 = -1;

    public int manwear3 = -1;

    public int manwearyoff = 0;

    public int manhead = -1;

    public int manhead2 = -1;

    public int womanwear = -1;

    public int womanwear2 = -1;

    public int womanwear3 = -1;

    public int womanwearyoff = 0;

    public int womanhead = -1;

    public int womanhead2 = -1;

    public int certlink = -1;

    public int certtemplate = -1;

    public int team = 0;

    public int shiftclickindex = -2;

    public int boughtlink = -1;

    public int boughttemplate = -1;

    public int placeholderlink = -1;

    public int placeholdertemplate = -1;

    public Map<Integer, Object> params = null;

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
                    name = in.gjstr();
                    break;
                case 4:
                    zoom2d = in.g2();
                    break;
                case 5:
                    xan2d = in.g2();
                    break;
                case 6:
                    yan2d = in.g2();
                    break;
                case 7:
                    xof2d = in.g2s();
                    break;
                case 8:
                    yof2d = in.g2s();
                    break;
                case 11:
                    stackable = 1;
                    break;
                case 12:
                    cost = in.g4s();
                    break;
                case 16:
                    members = true;
                    break;
                case 23:
                    manwear = in.g2();
                    manwearyoff = in.g1();
                    break;
                case 24:
                    manwear2 = in.g2();
                    break;
                case 25:
                    womanwear = in.g2();
                    womanwearyoff = in.g1();
                    break;
                case 26:
                    womanwear2 = in.g2();
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34: {
                    var s = in.gjstr();
                    if (!s.equalsIgnoreCase("Hidden")) op[code - 30] = s;
                    break;
                }
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                    iop[code - 35] = in.gjstr();
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
                case 42:
                    shiftclickindex = in.g1();
                    break;
                case 65:
                    stockmarket = true;
                    break;
                case 78:
                    manwear3 = in.g2();
                    break;
                case 79:
                    womanwear3 = in.g2();
                    break;
                case 90:
                    manhead = in.g2();
                    break;
                case 91:
                    womanhead = in.g2();
                    break;
                case 92:
                    manhead2 = in.g2();
                    break;
                case 93:
                    womanhead2 = in.g2();
                    break;
                case 95:
                    zan2d = in.g2();
                    break;
                case 97:
                    certlink = in.g2();
                    break;
                case 98:
                    certtemplate = in.g2();
                    break;
                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                    if (countobj == null) {
                        countobj = new int[10];
                        countco = new int[10];
                    }
                    countobj[code - 100] = in.g2();
                    countco[code - 100] = in.g2();
                    break;
                case 110:
                    resizex = in.g2();
                    break;
                case 111:
                    resizey = in.g2();
                    break;
                case 112:
                    resizez = in.g2();
                    break;
                case 113:
                    ambient = in.g1();
                    break;
                case 114:
                    contrast = in.g1() * 5;
                    break;
                case 115:
                    team = in.g1();
                    break;
                case 139:
                    boughtlink = in.g2();
                    break;
                case 140:
                    boughttemplate = in.g2();
                    break;
                case 148:
                    placeholderlink = in.g2();
                    break;
                case 149:
                    placeholdertemplate = in.g2();
                    break;
                case 249:
                    params = in.decodeParams();
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
