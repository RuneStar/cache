package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.runestar.cache.content.Buf.*;

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

    public String[] op = new String[5];

    public Map<Integer, Object> params = null;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int code = getUnsignedByte(buffer);
            switch (code) {
                case 0:
                    return;
                case 1: {
                    int n = getUnsignedByte(buffer);
                    models = new int[n];
                    for (int i = 0; i < n; i++) {
                        models[i] = getUnsignedShort(buffer);
                    }
                    break;
                }
                case 2:
                    name = getString(buffer);
                    break;
                case 12:
                    size = getUnsignedByte(buffer);
                    break;
                case 13:
                    readyanim = getUnsignedShort(buffer);
                    break;
                case 14:
                    walkanim = getUnsignedShort(buffer);
                    break;
                case 15:
                    turnleftanim = getUnsignedShort(buffer);
                    break;
                case 16:
                    turnrightanim = getUnsignedShort(buffer);
                    break;
                case 17:
                    walkanim = getUnsignedShort(buffer);
                    walkbackanim = getUnsignedShort(buffer);
                    walkleftanim = getUnsignedShort(buffer);
                    walkrightanim = getUnsignedShort(buffer);
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34: {
                    var s = getString(buffer);
                    if (!s.equalsIgnoreCase("Hidden")) op[code - 30] = s;
                    break;
                }
                case 40: {
                    int n = getUnsignedByte(buffer);
                    recol_s = new short[n];
                    recol_d = new short[n];
                    for (int i = 0; i < n; i++) {
                        recol_s[i] = buffer.getShort();
                        recol_d[i] = buffer.getShort();
                    }
                    break;
                }
                case 41: {
                    int n = getUnsignedByte(buffer);
                    retex_s = new short[n];
                    retex_d = new short[n];
                    for (int i = 0; i < n; i++) {
                        retex_s[i] = buffer.getShort();
                        retex_d[i] = buffer.getShort();
                    }
                    break;
                }
                case 60: {
                    var m = Byte.toUnsignedInt(buffer.get());
                    head = new int[m];
                    for (int i = 0; i < m; i++) {
                        head[i] = getUnsignedShort(buffer);
                    }
                    break;
                }
                case 93:
                    drawMapDot = false;
                    break;
                case 95:
                    combatLevel = getUnsignedShort(buffer);
                    break;
                case 97:
                    resizeh = getUnsignedShort(buffer);
                    break;
                case 98:
                    resizev = getUnsignedShort(buffer);
                    break;
                case 99:
                    _o = true;
                    break;
                case 100:
                    ambient = buffer.get();
                    break;
                case 101:
                    contrast = buffer.get();
                    break;
                case 102:
                    headIconPrayer = getUnsignedShort(buffer);
                    break;
                case 103:
                    _aw = getUnsignedShort(buffer);
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
                    multivarbit = getUnsignedShortM1(buffer);
                    multivar = getUnsignedShortM1(buffer);
                    int last = -1;
                    if (code == 118) last = getUnsignedShortM1(buffer);
                    int n = getUnsignedByte(buffer);
                    multi = new int[n + 2];
                    for(int i = 0; i <= n; i++) {
                        multi[i] = getUnsignedShortM1(buffer);
                    }
                    multi[n + 1] = last;
                    break;
                }
                case 249:
                    params = decodeParams(buffer);
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
