package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class SeqType extends ConfigType {

    public static final int GROUP = 12;

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

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1: {
                    int n = in.g2();
                    frameLengths = new int[n];
                    for (var i = 0; i < n; i++) {
                        frameLengths[i] = in.g2();
                    }
                    frameIds = new int[n];
                    for (var i = 0; i < n; i++) {
                        frameIds[i] = in.g2();
                    }
                    for (var i = 0; i < n; i++) {
                        frameIds[i] += in.g2() << 16;
                    }
                    break;
                }
                case 2:
                    frameCount = in.g2();
                    break;
                case 3: {
                    int n = in.g1();
                    _d = new int[n + 1];
                    for (var i = 0; i < n; i++) {
                        _d[i] = in.g1();
                    }
                    _d[n] = 9999999;
                    break;
                }
                case 4:
                    _k = true;
                    break;
                case 5:
                    _n = in.g1();
                    break;
                case 6:
                    lefthand = in.g2();
                    break;
                case 7:
                    righthand = in.g2();
                    break;
                case 8:
                    _z = in.g1();
                    break;
                case 9:
                    _j = in.g1();
                    break;
                case 10:
                    _s = in.g1();
                    break;
                case 11:
                    _t = in.g1();
                    break;
                case 12: {
                    int n = in.g1();
                    frameIds2 = new int[n];
                    for (var i = 0; i < n; i++) {
                        frameIds2[i] = in.g2();
                    }
                    for (var i = 0; i < n; i++) {
                        frameIds2[i] += in.g2() << 16;
                    }
                    break;
                }
                case 13: {
                    int n = in.g1();
                    _e = new int[n];
                    for (var i = 0; i < n; i++) {
                        _e[i] = in.g3();
                    }
                    break;
                }
                default:
                    unrecognisedCode(code);
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
