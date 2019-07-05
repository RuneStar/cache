package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

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
            int code = getUnsignedByte(buffer);
            switch (code) {
                case 0:
                    return;
                case 1:
                    model = getUnsignedShort(buffer);
                    break;
                case 2:
                    anim = getUnsignedShort(buffer);
                    break;
                case 4:
                    resizeh = getUnsignedShort(buffer);
                    break;
                case 5:
                    resizev = getUnsignedShort(buffer);
                    break;
                case 6:
                    orientation = getUnsignedShort(buffer);
                    break;
                case 7:
                    ambient = getUnsignedByte(buffer);
                    break;
                case 8:
                    contrast = getUnsignedByte(buffer);
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
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
