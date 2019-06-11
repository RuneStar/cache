package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

public final class NPCType extends ConfigType {

    public int transformVarbit = -1;

    public int _af = 0;

    public int _an = 0;

    public int transformVarp = -1;

    public int resizev = 128;

    public int resizeh = 128;

    public int[] _n = null;

    public int[] archives = null;

    public short[] retextureFrom = null;

    public short[] retextureTo = null;

    public short[] recols = null;

    public short[] recold = null;

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

    public String[] ops = new String[5];

    public Map<Integer, Object> params = null;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = Buf.getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    var n = Buf.getUnsignedByte(buffer);
                    archives = new int[n];
                    for (int i = 0; i < n; i++) {
                        archives[i] = Buf.getUnsignedShort(buffer);
                    }
                    break;
                case 2:
                    name = Buf.getString(buffer);
                    break;
                case 12:
                    size = Buf.getUnsignedByte(buffer);
                    break;
                case 13:
                    idleSeq = Buf.getUnsignedShort(buffer);
                    break;
                case 14:
                    walkSeq = Buf.getUnsignedShort(buffer);
                    break;
                case 15:
                    turnLeftSeq = Buf.getUnsignedShort(buffer);
                    break;
                case 16:
                    turnRightSeq = Buf.getUnsignedShort(buffer);
                    break;
                case 17:
                    walkSeq = Buf.getUnsignedShort(buffer);
                    turnSeq = Buf.getUnsignedShort(buffer);
                    walkLeftSeq = Buf.getUnsignedShort(buffer);
                    walkRightSeq = Buf.getUnsignedShort(buffer);
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                    var action = Buf.getString(buffer);
                    if (!action.equals("Hidden")) {
                        ops[opcode - 30] = action;
                    }
                    break;
                case 40:
                    int colors = Buf.getUnsignedByte(buffer);
                    recols = new short[colors];
                    recold = new short[colors];
                    for (int i = 0; i < colors; i++) {
                        recols[i] = buffer.getShort();
                        recold[i] = buffer.getShort();
                    }
                    break;
                case 41:
                    int textures = Buf.getUnsignedByte(buffer);
                    retextureFrom = new short[textures];
                    retextureTo = new short[textures];
                    for (int i = 0; i < textures; i++) {
                        retextureFrom[i] = buffer.getShort();
                        retextureTo[i] = buffer.getShort();
                    }
                    break;
                case 60:
                    var m = Byte.toUnsignedInt(buffer.get());
                    _n = new int[m];
                    for (int i = 0; i < m; i++) {
                        _n[i] = Buf.getUnsignedShort(buffer);
                    }
                    break;
                case 93:
                    drawMapDot = false;
                    break;
                case 95:
                    combatLevel = Buf.getUnsignedShort(buffer);
                    break;
                case 97:
                    resizeh = Buf.getUnsignedShort(buffer);
                    break;
                case 98:
                    resizev = Buf.getUnsignedShort(buffer);
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
                    headIconPrayer = Buf.getUnsignedShort(buffer);
                    break;
                case 103:
                    _aw = Buf.getUnsignedShort(buffer);
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
                case 118:
                    transformVarbit = Buf.getUnsignedShort(buffer);
                    if (0xFFFF == transformVarbit) {
                        transformVarbit = -1;
                    }

                    transformVarp = Buf.getUnsignedShort(buffer);
                    if (transformVarp == 0xFFFF) {
                        transformVarp = -1;
                    }

                    int var4 = -1;
                    if (118 == opcode) {
                        var4 = Buf.getUnsignedShort(buffer);
                        if (0xFFFF == var4) {
                            var4 = -1;
                        }
                    }

                    int var5 = Buf.getUnsignedByte(buffer);
                    transforms = new int[var5 + 2];

                    for(int var6 = 0; var6 <= var5; ++var6) {
                        transforms[var6] = Buf.getUnsignedShort(buffer);
                        if (transforms[var6] == 0xFFFF) {
                            transforms[var6] = -1;
                        }
                    }

                    transforms[var5 + 1] = var4;
                    break;
                case 249:
                    params = Buf.decodeParams(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
