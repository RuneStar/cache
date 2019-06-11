package org.runestar.cache.content;

import java.nio.ByteBuffer;

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
            int opcode = Buf.getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1: {
                    var n = Buf.getUnsignedShort(buffer);
                    frameLengths = new int[n];
                    for (var i = 0; i < n; i++) {
                        frameLengths[i] = Buf.getUnsignedShort(buffer);
                    }
                    frameIds = new int[n];
                    for (var i = 0; i < n; i++) {
                        frameIds[i] = Buf.getUnsignedShort(buffer);
                    }
                    for (var i = 0; i < n; i++) {
                        frameIds[i] += Buf.getUnsignedShort(buffer) << 16;
                    }
                    break;
                }
                case 2:
                    frameCount = Buf.getUnsignedShort(buffer);
                    break;
                case 3: {
                    var n = Buf.getUnsignedByte(buffer);
                    _d = new int[n + 1];
                    for (var i = 0; i < n; i++) {
                        _d[i] = Buf.getUnsignedByte(buffer);
                    }
                    _d[n] = 9999999;
                    break;
                }
                case 4:
                    _k = true;
                    break;
                case 5:
                    _n = Buf.getUnsignedByte(buffer);
                    break;
                case 6:
                    shield = Buf.getUnsignedShort(buffer);
                    break;
                case 7:
                    weapon = Buf.getUnsignedShort(buffer);
                    break;
                case 8:
                    _z = Buf.getUnsignedByte(buffer);
                    break;
                case 9:
                    _j = Buf.getUnsignedByte(buffer);
                    break;
                case 10:
                    _s = Buf.getUnsignedByte(buffer);
                    break;
                case 11:
                    _t = Buf.getUnsignedByte(buffer);
                    break;
                case 12: {
                    var n = Buf.getUnsignedByte(buffer);
                    frameIds2 = new int[n];
                    for (var i = 0; i < n; i++) {
                        frameIds2[i] = Buf.getUnsignedShort(buffer);
                    }
                    for (var i = 0; i < n; i++) {
                        frameIds2[i] += Buf.getUnsignedShort(buffer) << 16;
                    }
                    break;
                }
                case 13: {
                    var n = Buf.getUnsignedByte(buffer);
                    _e = new int[n];
                    for (var i = 0; i < n; i++) {
                        _e[i] = Buf.getMedium(buffer);
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
