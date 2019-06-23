package org.runestar.cache.content;

import java.nio.ByteBuffer;

public final class IDKType extends ConfigType {

    public int bodyPart = -1;

    public int[] head = new int[]{-1, -1, -1, -1, -1};

    public int[] models = null;

    public short[] retex_s = null;

    public short[] recol_s = null;

    public short[] recol_d = null;

    public short[] retex_d = null;

    public boolean _k = false;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = buffer.getUnsignedByte();
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    bodyPart = buffer.getUnsignedByte();
                    break;
                case 2: {
                    int n = buffer.getUnsignedByte();
                    models = new int[n];
                    for (int i = 0; i < n; i++) {
                        models[i] = buffer.getUnsignedShort();
                    }
                    break;
                }
                case 3:
                    _k = true;
                    break;
                case 40:
                case 41:{
                    int n = buffer.getUnsignedByte();
                    recol_s = new short[n];
                    recol_d = new short[n];
                    for (int i = 0; i < n; i++) {
                        recol_s[i] = buffer.getShort();
                        recol_d[i] = buffer.getShort();
                    }
                    break;
                }
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                    head[opcode - 60] = buffer.getUnsignedShort();
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
