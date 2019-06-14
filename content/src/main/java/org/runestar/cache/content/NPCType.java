package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.runestar.cache.content.Buf.*;

public final class NPCType extends ConfigType {

    public int transformVarbit = -1;

    public int _af = 0;

    public int _an = 0;

    public int transformVarp = -1;

    public int resizev = 128;

    public int resizeh = 128;

    public int[] _n = null;

    public int[] models = null;

    public short[] retex_s = null;

    public short[] retex_d = null;

    public short[] recol_s = null;

    public short[] recol_d = null;

    public boolean _ab = false;

    public boolean _ag = true;

    public boolean isInteractable = true;

    public boolean _o = false;

    public boolean drawMapDot = true;

    public int _aw = 0;

    public int headIconPrayer = -1;

    public int size = 1;

    public int walkSeq = -1;

    public int idleSeq = -1;

    public int turnSeq = -1;

    public int turnRightSeq = -1;

    public int turnLeftSeq = -1;

    public int combatLevel = -1;

    public int walkLeftSeq = -1;

    public int walkRightSeq = -1;

    public int[] transforms = null;

    public String name = "null";

    public String[] op = new String[5];

    public Map<Integer, Object> params = null;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1: {
                    var n = getUnsignedByte(buffer);
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
                    idleSeq = getUnsignedShort(buffer);
                    break;
                case 14:
                    walkSeq = getUnsignedShort(buffer);
                    break;
                case 15:
                    turnLeftSeq = getUnsignedShort(buffer);
                    break;
                case 16:
                    turnRightSeq = getUnsignedShort(buffer);
                    break;
                case 17:
                    walkSeq = getUnsignedShort(buffer);
                    turnSeq = getUnsignedShort(buffer);
                    walkLeftSeq = getUnsignedShort(buffer);
                    walkRightSeq = getUnsignedShort(buffer);
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34: {
                    var s = getString(buffer);
                    if (!s.equals("Hidden")) op[opcode - 30] = s;
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
                    _n = new int[m];
                    for (int i = 0; i < m; i++) {
                        _n[i] = getUnsignedShort(buffer);
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
                    _af = buffer.get();
                    break;
                case 101:
                    _an = buffer.get();
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
                    _ab = true;
                    break;
                case 106:
                case 118: {
                    transformVarbit = getUnsignedShort(buffer);
                    if (0xFFFF == transformVarbit) {
                        transformVarbit = -1;
                    }

                    transformVarp = getUnsignedShort(buffer);
                    if (transformVarp == 0xFFFF) {
                        transformVarp = -1;
                    }

                    int var4 = -1;
                    if (118 == opcode) {
                        var4 = getUnsignedShort(buffer);
                        if (0xFFFF == var4) {
                            var4 = -1;
                        }
                    }

                    int var5 = getUnsignedByte(buffer);
                    transforms = new int[var5 + 2];

                    for(int var6 = 0; var6 <= var5; ++var6) {
                        transforms[var6] = getUnsignedShort(buffer);
                        if (transforms[var6] == 0xFFFF) {
                            transforms[var6] = -1;
                        }
                    }

                    transforms[var5 + 1] = var4;
                    break;
                }
                case 249:
                    params = decodeParams(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
