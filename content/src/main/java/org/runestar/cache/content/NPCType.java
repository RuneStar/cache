package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

public final class NPCType extends ConfigType {

    public int transformVarbit = -1;

    public int ambient = 0;

    public int contrast = 0;

    public int transformVarp = -1;

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

    public int[] transforms = null;

    public String name = "null";

    public String[] op = new String[5];

    public Map<Integer, Object> params = null;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = buffer.getUnsignedByte();
            switch (opcode) {
                case 0:
                    return;
                case 1: {
                    int n = buffer.getUnsignedByte();
                    models = new int[n];
                    for (int i = 0; i < n; i++) {
                        models[i] = buffer.getUnsignedShort();
                    }
                    break;
                }
                case 2:
                    name = buffer.getString();
                    break;
                case 12:
                    size = buffer.getUnsignedByte();
                    break;
                case 13:
                    readyanim = buffer.getUnsignedShort();
                    break;
                case 14:
                    walkanim = buffer.getUnsignedShort();
                    break;
                case 15:
                    turnleftanim = buffer.getUnsignedShort();
                    break;
                case 16:
                    turnrightanim = buffer.getUnsignedShort();
                    break;
                case 17:
                    walkanim = buffer.getUnsignedShort();
                    walkbackanim = buffer.getUnsignedShort();
                    walkleftanim = buffer.getUnsignedShort();
                    walkrightanim = buffer.getUnsignedShort();
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34: {
                    var s = buffer.getString();
                    if (!s.equalsIgnoreCase("Hidden"))
                        op[opcode - 30] = s;
                    break;
                }
                case 40: {
                    int n = buffer.getUnsignedByte();
                    recol_s = new short[n];
                    recol_d = new short[n];
                    for (int i = 0; i < n; i++) {
                        recol_s[i] = buffer.getShort();
                        recol_d[i] = buffer.getShort();
                    }
                    break;
                }
                case 41: {
                    int n = buffer.getUnsignedByte();
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
                        head[i] = buffer.getUnsignedShort();
                    }
                    break;
                }
                case 93:
                    drawMapDot = false;
                    break;
                case 95:
                    combatLevel = buffer.getUnsignedShort();
                    break;
                case 97:
                    resizeh = buffer.getUnsignedShort();
                    break;
                case 98:
                    resizev = buffer.getUnsignedShort();
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
                    headIconPrayer = buffer.getUnsignedShort();
                    break;
                case 103:
                    _aw = buffer.getUnsignedShort();
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
                    transformVarbit = buffer.getUnsignedShortM1();
                    transformVarp = buffer.getUnsignedShortM1();
                    int lastTransform = -1;
                    if (opcode == 118) lastTransform = buffer.getUnsignedShortM1();
                    int n = buffer.getUnsignedByte();
                    transforms = new int[n + 2];
                    for(int i = 0; i <= n; i++) {
                        transforms[i] = buffer.getUnsignedShortM1();
                    }
                    transforms[n + 1] = lastTransform;
                    break;
                }
                case 249:
                    params = buffer.decodeParams();
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
