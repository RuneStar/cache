package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

import java.util.Map;

public final class LocType extends ConfigType {

    public static final int GROUP = 6;

    public boolean isRotated = false;

    public boolean isSolid = false;

    public boolean sharelight = false;

    public Map<Integer, Object> params = null;

    public int resizeZ = 128;

    public int ambient = 0;

    public int resizeY = 128;

    public int resizeX = 128;

    public int contrast = 0;

    public int offsetX = 0;

    public int offsetZ = 0;

    public int offsetY = 0;

    public int hillChange = -1;

    public int[] models = null;

    public int[] modelTypes = null;

    public short[] recol_s = null;

    public short[] recol_d = null;

    public short[] retex_d = null;

    public short[] retex_s = null;

    public boolean lowDetailVisible = false;

    public boolean clipped = true;

    public boolean boolean1 = true;

    public boolean occlude = false;

    public int interactType = 2;

    public int int3 = -1;

    public int mapSceneId = -1;

    public int int5 = 0;

    public int ambientSoundId = -1;

    public int int6 = 0;

    public int mapIconId = -1;

    public int int4 = 0;

    public int length = 1;

    public int width = 1;

    public int interactable = -1;

    public int int2 = 16;

    public int anim = -1;

    public int[] multi = null;

    public int multivarbit = -1;

    public int multivar = -1;

    public int[] _av = null;

    public String name = "null";

    public final String[] op = new String[5];

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1: {
                    int n = in.g1();
                    if (n > 0) {
                        modelTypes = new int[n];
                        models = new int[n];
                        for(int var5 = 0; var5 < n; var5++) {
                            models[var5] = in.g2();
                            modelTypes[var5] = in.g1();
                        }
                    }
                    break;
                }
                case 2:
                    name = in.gjstr();
                    break;
                case 5: {
                    int n = in.g1();
                    if (n > 0) {
                        modelTypes = null;
                        models = new int[n];
                        for(int var5 = 0; var5 < n; var5++) {
                            models[var5] = in.g2();
                        }
                    }
                    break;
                }
                case 14:
                    width = in.g1();
                    break;
                case 15:
                    length = in.g1();
                    break;
                case 17:
                    interactType = 0;
                    boolean1 = false;
                    break;
                case 18:
                    boolean1 = false;
                    break;
                case 19:
                    interactable = in.g1();
                    break;
                case 21:
                    hillChange = 0;
                    break;
                case 22:
                    sharelight = true;
                    break;
                case 23:
                    occlude = true;
                    break;
                case 24:
                    anim = in.g2m();
                    break;
                case 27:
                    interactType = 1;
                    break;
                case 28:
                    int2 = in.g1();
                    break;
                case 29:
                    ambient = in.g1s();
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
                case 39:
                    contrast = in.g1s() * 25;
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
                case 62:
                    isRotated = true;
                    break;
                case 64:
                    clipped = false;
                    break;
                case 65:
                    resizeX = in.g2();
                    break;
                case 66:
                    resizeY = in.g2();
                    break;
                case 67:
                    resizeZ = in.g2();
                    break;
                case 68:
                    mapSceneId = in.g2();
                    break;
                case 69:
                    in.skip(1);
                    break;
                case 70:
                    offsetX = in.g2();
                    break;
                case 71:
                    offsetY = in.g2();
                    break;
                case 72:
                    offsetZ = in.g2();
                    break;
                case 73:
                    lowDetailVisible = true;
                    break;
                case 74:
                    isSolid = true;
                    break;
                case 75:
                    int3 = in.g1();
                    break;
                case 78:
                    ambientSoundId = in.g2();
                    int4 = in.g1();
                    break;
                case 79: {
                    int5 = in.g2();
                    int6 = in.g2();
                    int4 = in.g1();
                    int n = in.g1();
                    _av = new int[n];
                    for(int var5 = 0; var5 < n; var5++) {
                        _av[var5] = in.g2();
                    }
                    break;
                }
                case 81:
                    hillChange = in.g1() * 256;
                    break;
                case 82:
                    mapIconId = in.g2();
                    break;
                case 77:
                case 92: {
                    multivarbit = in.g2m();
                    multivar = in.g2m();
                    int last = -1;
                    if (code == 92) last = in.g2m();
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

    @Override protected void postDecode() {
        if (-1 == interactable) {
            interactable = 0;
            if (models != null && (null == modelTypes || modelTypes[0] == 10)) {
                interactable = 1;
            }

            for(int var2 = 0; var2 < 5; ++var2) {
                if (op[var2] != null) {
                    interactable = 1;
                }
            }
        }

        if (int3 == -1) {
            int3 = 0 != interactType ? 1 : 0;
        }

        if (isSolid) {
            interactType = 0;
            boolean1 = false;
        }
    }
}
