package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class IDKType extends ConfigType {

    public int bodyPart = -1;

    public int[] models = new int[]{-1, -1, -1, -1, -1};

    public int[] models2 = null;

    public short[] retextureFrom = null;

    public short[] recols = null;

    public short[] recold = null;

    public short[] retextureTo = null;

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
                    var n = getUnsignedByte(buffer);
                    models2 = new int[n];
                    for (int i = 0; i < n; i++) {
                        models2[i] = getUnsignedShort(buffer);
                    }
                    break;
                }
                case 3:
                    _k = true;
                    break;
                case 40: {
                    int n = getUnsignedByte(buffer);
                    recols = new short[n];
                    recold = new short[n];
                    for (int i = 0; i < n; i++) {
                        recols[i] = buffer.getShort();
                        recold[i] = buffer.getShort();
                    }
                    break;
                }
                case 41: {
                    int n = getUnsignedByte(buffer);
                    retextureFrom = new short[n];
                    retextureTo = new short[n];
                    for (int i = 0; i < n; i++) {
                        retextureFrom[i] = buffer.getShort();
                        retextureTo[i] = buffer.getShort();
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
                    models[opcode - 60] = getUnsignedShort(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
