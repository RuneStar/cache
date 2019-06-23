package org.runestar.cache.content;

import java.nio.ByteBuffer;

public final class SeqType extends ConfigType {

    public int[] _d = null;
    
    public int[] frameIds2 = null;

    public boolean _k = false;

    public int righthand = -1;

    public int lefthand = -1;

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
            int opcode = buffer.getUnsignedByte();
            switch (opcode) {
                case 0:
                    return;
                case 1: {
                    int n = buffer.getUnsignedShort();
                    frameLengths = new int[n];
                    for (var i = 0; i < n; i++) {
                        frameLengths[i] = buffer.getUnsignedShort();
                    }
                    frameIds = new int[n];
                    for (var i = 0; i < n; i++) {
                        frameIds[i] = buffer.getUnsignedShort();
                    }
                    for (var i = 0; i < n; i++) {
                        frameIds[i] += buffer.getUnsignedShort() << 16;
                    }
                    break;
                }
                case 2:
                    frameCount = buffer.getUnsignedShort();
                    break;
                case 3: {
                    int n = buffer.getUnsignedByte();
                    _d = new int[n + 1];
                    for (var i = 0; i < n; i++) {
                        _d[i] = buffer.getUnsignedByte();
                    }
                    _d[n] = 9999999;
                    break;
                }
                case 4:
                    _k = true;
                    break;
                case 5:
                    _n = buffer.getUnsignedByte();
                    break;
                case 6:
                    lefthand = buffer.getUnsignedShort();
                    break;
                case 7:
                    righthand = buffer.getUnsignedShort();
                    break;
                case 8:
                    _z = buffer.getUnsignedByte();
                    break;
                case 9:
                    _j = buffer.getUnsignedByte();
                    break;
                case 10:
                    _s = buffer.getUnsignedByte();
                    break;
                case 11:
                    _t = buffer.getUnsignedByte();
                    break;
                case 12: {
                    int n = buffer.getUnsignedByte();
                    frameIds2 = new int[n];
                    for (var i = 0; i < n; i++) {
                        frameIds2[i] = buffer.getUnsignedShort();
                    }
                    for (var i = 0; i < n; i++) {
                        frameIds2[i] += buffer.getUnsignedShort() << 16;
                    }
                    break;
                }
                case 13: {
                    int n = buffer.getUnsignedByte();
                    _e = new int[n];
                    for (var i = 0; i < n; i++) {
                        _e[i] = buffer.getMedium();
                    }
                    break;
                }
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }

    @Override protected void postDecode() {
        if (_j == -1) {
            _j = _d != null ? 2 : 0;
        }
        if (_s == -1) {
            _s = null != _d ? 2 : 0;
        }
    }
}
