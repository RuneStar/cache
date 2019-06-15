package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class SeqType extends ConfigType {

    public int[] _d = null;
    public int[] frameIds2 = null;
    public boolean _k = false;
    public int weapon = -1;
    public int shield = -1;
    public int _j = -1;
    public int _n = 5;
    public int _s = -1;
    public int _t = 2;
    public int frameCount = -1;
    public int _z = 99;
    public int[] _e = null;
    public int[] frameLengths = null;
    public int[] frameIds = null;


    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1: {
                    int n = getUnsignedShort(buffer);
                    frameLengths = new int[n];
                    for (var i = 0; i < n; i++) {
                        frameLengths[i] = getUnsignedShort(buffer);
                    }
                    frameIds = new int[n];
                    for (var i = 0; i < n; i++) {
                        frameIds[i] = getUnsignedShort(buffer);
                    }
                    for (var i = 0; i < n; i++) {
                        frameIds[i] += getUnsignedShort(buffer) << 16;
                    }
                    break;
                }
                case 2:
                    frameCount = getUnsignedShort(buffer);
                    break;
                case 3: {
                    int n = getUnsignedByte(buffer);
                    _d = new int[n + 1];
                    for (var i = 0; i < n; i++) {
                        _d[i] = getUnsignedByte(buffer);
                    }
                    _d[n] = 9999999;
                    break;
                }
                case 4:
                    _k = true;
                    break;
                case 5:
                    _n = getUnsignedByte(buffer);
                    break;
                case 6:
                    shield = getUnsignedShort(buffer);
                    break;
                case 7:
                    weapon = getUnsignedShort(buffer);
                    break;
                case 8:
                    _z = getUnsignedByte(buffer);
                    break;
                case 9:
                    _j = getUnsignedByte(buffer);
                    break;
                case 10:
                    _s = getUnsignedByte(buffer);
                    break;
                case 11:
                    _t = getUnsignedByte(buffer);
                    break;
                case 12: {
                    int n = getUnsignedByte(buffer);
                    frameIds2 = new int[n];
                    for (var i = 0; i < n; i++) {
                        frameIds2[i] = getUnsignedShort(buffer);
                    }
                    for (var i = 0; i < n; i++) {
                        frameIds2[i] += getUnsignedShort(buffer) << 16;
                    }
                    break;
                }
                case 13: {
                    int n = getUnsignedByte(buffer);
                    _e = new int[n];
                    for (var i = 0; i < n; i++) {
                        _e[i] = getMedium(buffer);
                    }
                    break;
                }
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }

    @Override protected void postDecode() {
        if (this._j == -1) {
            this._j = this._d != null ? 2 : 0;
        }
        if (this._s == -1) {
            this._s = null != this._d ? 2 : 0;
        }
    }
}
