package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class SpotAnimType extends ConfigType {

    public int _a = 0;

    public int orientation = 0;

    public int widthScale = 128;

    public int heightScale = 128;

    public int model = 0;

    public int _z = 0;

    public short[] retextureTo = null;

    public short[] recold = null;

    public short[] recols = null;

    public short[] retextureFrom = null;

    public int seq = -1;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    model = getUnsignedShort(buffer);
                    break;
                case 2:
                    seq = getUnsignedShort(buffer);
                    break;
                case 4:
                    widthScale = getUnsignedShort(buffer);
                    break;
                case 5:
                    heightScale = getUnsignedShort(buffer);
                    break;
                case 6:
                    orientation = getUnsignedShort(buffer);
                    break;
                case 7:
                    _a = getUnsignedByte(buffer);
                    break;
                case 8:
                    _z = getUnsignedByte(buffer);
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
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
