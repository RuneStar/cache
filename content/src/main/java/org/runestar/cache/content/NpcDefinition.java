package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

public final class NpcDefinition {

    public int transformVarbit = -1;

    public int _af = 0;

    public int _an = 0;

    public int transformVarp = -1;

    public int heightScale = 128;

    public int widthScale = 128;

    public int[] _n = null;

    public int[] archives = null;

    public short[] retextureFrom = null;

    public short[] retextureTo = null;

    public short[] recolorFrom = null;

    public short[] recolorTo = null;

    public boolean _ab = false;

    public boolean _ag = true;

    public boolean isInteractable = true;

    public boolean _o = false;

    public boolean drawMapDot = true;

    public int _aw = 0;

    public int headIconPrayer = -1;

    public int size = 1;

    public int walkSequence = -1;

    public int idleSequence = -1;

    public int walkTurnSequence = -1;

    public int turnRightSequence = -1;

    public int turnLeftSequence = -1;

    public int combatLevel = -1;

    public int walkTurnLeftSequence = -1;

    public int walkTurnRightSequence = -1;

    public int[] transforms = null;

    public String name = "null";

    public String[] actions = new String[5];

    public Map<Integer, Object> params = null;

    public void read(ByteBuffer buffer) {
        while (true) {
            int opcode = Buffer.getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    var n = Buffer.getUnsignedByte(buffer);
                    archives = new int[n];
                    for (int i = 0; i < n; i++) {
                        archives[i] = Buffer.getUnsignedShort(buffer);
                    }
                    break;
                case 2:
                    name = Buffer.getString(buffer);
                    break;
                case 12:
                    size = Buffer.getUnsignedByte(buffer);
                    break;
                case 13:
                    idleSequence = Buffer.getUnsignedShort(buffer);
                    break;
                case 14:
                    walkSequence = Buffer.getUnsignedShort(buffer);
                    break;
                case 15:
                    turnLeftSequence = Buffer.getUnsignedShort(buffer);
                    break;
                case 16:
                    turnRightSequence = Buffer.getUnsignedShort(buffer);
                    break;
                case 17:
                    walkSequence = Buffer.getUnsignedShort(buffer);
                    walkTurnSequence = Buffer.getUnsignedShort(buffer);
                    walkTurnLeftSequence = Buffer.getUnsignedShort(buffer);
                    walkTurnRightSequence = Buffer.getUnsignedShort(buffer);
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                    var action = Buffer.getString(buffer);
                    if (!action.equals("Hidden")) {
                        actions[opcode - 30] = action;
                    }
                    break;
                case 40:
                    int colors = Buffer.getUnsignedByte(buffer);
                    recolorFrom = new short[colors];
                    recolorTo = new short[colors];
                    for (int i = 0; i < colors; i++) {
                        recolorFrom[i] = buffer.getShort();
                        recolorTo[i] = buffer.getShort();
                    }
                    break;
                case 41:
                    int textures = Buffer.getUnsignedByte(buffer);
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
                        _n[i] = Buffer.getUnsignedShort(buffer);
                    }
                    break;
                case 93:
                    drawMapDot = false;
                    break;
                case 95:
                    combatLevel = Buffer.getUnsignedShort(buffer);
                    break;
                case 97:
                    widthScale = Buffer.getUnsignedShort(buffer);
                    break;
                case 98:
                    heightScale = Buffer.getUnsignedShort(buffer);
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
                    headIconPrayer = Buffer.getUnsignedShort(buffer);
                    break;
                case 103:
                    _aw = Buffer.getUnsignedShort(buffer);
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
                    transformVarbit = Buffer.getUnsignedShort(buffer);
                    if (0xFFFF == transformVarbit) {
                        transformVarbit = -1;
                    }

                    transformVarp = Buffer.getUnsignedShort(buffer);
                    if (transformVarp == 0xFFFF) {
                        transformVarp = -1;
                    }

                    int var4 = -1;
                    if (118 == opcode) {
                        var4 = Buffer.getUnsignedShort(buffer);
                        if (0xFFFF == var4) {
                            var4 = -1;
                        }
                    }

                    int var5 = Buffer.getUnsignedByte(buffer);
                    transforms = new int[var5 + 2];

                    for(int var6 = 0; var6 <= var5; ++var6) {
                        transforms[var6] = Buffer.getUnsignedShort(buffer);
                        if (transforms[var6] == 0xFFFF) {
                            transforms[var6] = -1;
                        }
                    }

                    transforms[var5 + 1] = var4;
                    break;
                case 249:
                    params = Buffer.getParams(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
