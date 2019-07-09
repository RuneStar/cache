package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

import java.util.Map;

public final class NPCType extends ConfigType {

    public static final int GROUP = 9;

    public int ambient = 0;

    public int contrast = 0;

    public int resizev = 128;

    public int resizeh = 128;

    public int[] head = null;

    public int[] models = null;

    public short[] retex_s = null;

    public short[] retex_d = null;

    public short[] recol_s = null;

    public short[] recol_d = null;

    public boolean follower = false;

    public boolean _ag = true;

    public boolean isInteractable = true;

    public boolean _o = false;

    public boolean drawMapDot = true;

    public int _aw = 0;

    public int headIconPrayer = -1;

    public int size = 1;

    public int walkanim = -1;

    public int readyanim = -1;

    public int walkbackanim = -1;

    public int turnrightanim = -1;

    public int turnleftanim = -1;

    public int combatLevel = -1;

    public int walkleftanim = -1;

    public int walkrightanim = -1;

    public int[] multi = null;

    public int multivarbit = -1;

    public int multivar = -1;

    public String name = "null";

    public final String[] op = new String[5];

    public Map<Integer, Object> params = null;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1: {
                    int n = in.g1();
                    models = new int[n];
                    for (int i = 0; i < n; i++) {
                        models[i] = in.g2();
                    }
                    break;
                }
                case 2:
                    name = in.gjstr();
                    break;
                case 12:
                    size = in.g1();
                    break;
                case 13:
                    readyanim = in.g2();
                    break;
                case 14:
                    walkanim = in.g2();
                    break;
                case 15:
                    turnleftanim = in.g2();
                    break;
                case 16:
                    turnrightanim = in.g2();
                    break;
                case 17:
                    walkanim = in.g2();
                    walkbackanim = in.g2();
                    walkleftanim = in.g2();
                    walkrightanim = in.g2();
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
                case 60: {
                    var m = in.g1();
                    head = new int[m];
                    for (int i = 0; i < m; i++) {
                        head[i] = in.g2();
                    }
                    break;
                }
                case 93:
                    drawMapDot = false;
                    break;
                case 95:
                    combatLevel = in.g2();
                    break;
                case 97:
                    resizeh = in.g2();
                    break;
                case 98:
                    resizev = in.g2();
                    break;
                case 99:
                    _o = true;
                    break;
                case 100:
                    ambient = in.g1s();
                    break;
                case 101:
                    contrast = in.g1s();
                    break;
                case 102:
                    headIconPrayer = in.g2();
                    break;
                case 103:
                    _aw = in.g2();
                    break;
                case 107:
                    isInteractable = false;
                    break;
                case 109:
                    _ag = false;
                    break;
                case 111:
                    follower = true;
                    break;
                case 106:
                case 118: {
                    multivarbit = in.g2m();
                    multivar = in.g2m();
                    int last = -1;
                    if (code == 118) last = in.g2m();
                    int n = in.g1();
                    multi = new int[n + 2];
                    for(int i = 0; i <= n; i++) {
                        multi[i] = in.g2m();
                    }
                    multi[n + 1] = last;
                    break;
                }
                case 249:
                    params = in.decodeParams();
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
