package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class IDKType extends ConfigType {

    public static final int GROUP = 3;

    public int bodyPart = -1;

    public int[] head = {-1, -1, -1, -1, -1};

    public int[] models = null;

    public short[] retex_s = null;

    public short[] recol_s = null;

    public short[] recol_d = null;

    public short[] retex_d = null;

    public boolean _k = false;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    bodyPart = getUnsignedByte(buffer);
                    break;
                case 2: {
                    int n = getUnsignedByte(buffer);
                    models = new int[n];
                    for (int i = 0; i < n; i++) {
                        models[i] = getUnsignedShort(buffer);
                    }
                    break;
                }
                case 3:
                    _k = true;
                    break;
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
                    head[opcode - 60] = getUnsignedShort(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
