package org.runestar.cache.content;

import java.nio.ByteBuffer;

public final class SpotType extends ConfigType {

    public int model = 0;

    public int anim = -1;

    public int resizeh = 128;

    public int resizev = 128;

    public int orientation = 0;

    public int ambient = 0;

    public int contrast = 0;

    public short[] recol_s = null;

    public short[] recol_d = null;

    public short[] retex_s = null;

    public short[] retex_d = null;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = buffer.getUnsignedByte();
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    model = buffer.getUnsignedShort();
                    break;
                case 2:
                    anim = buffer.getUnsignedShort();
                    break;
                case 4:
                    resizeh = buffer.getUnsignedShort();
                    break;
                case 5:
                    resizev = buffer.getUnsignedShort();
                    break;
                case 6:
                    orientation = buffer.getUnsignedShort();
                    break;
                case 7:
                    ambient = buffer.getUnsignedByte();
                    break;
                case 8:
                    contrast = buffer.getUnsignedByte();
                    break;
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
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
