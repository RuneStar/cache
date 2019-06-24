package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.runestar.cache.content.Buf.*;

public final class LocType extends ConfigType {

    public static final int GROUP = 6;

    public boolean isRotated = false;

    public boolean isSolid = false;

    public boolean sharelight = false;

    public Map<Integer, Object> params = null;

    public int modelSizeY = 128;

    public int ambient = 0;

    public int modelHeight = 128;

    public int modelSizeX = 128;

    public int contrast = 0;

    public int offsetX = 0;

    public int offsetY = 0;

    public int offsetHeight = 0;

    public int hillskew = -1;

    public int[] models = null;

    public int[] _p = null;

    public short[] recol_s = null;

    public short[] recol_d = null;

    public short[] retex_d = null;

    public short[] retex_s = null;

    public boolean boolean2 = false;

    public boolean clipped = true;

    public boolean boolean1 = true;

    public boolean occlude = false;

    public int interactType = 2;

    public int int3 = -1;

    public int mapSceneId = -1;

    public int int5 = 0;

    public int ambientSoundId = -1;

    public int int6 = 0;

    public int mapIconId = -1;

    public int int4 = 0;

    public int sizeY = 1;

    public int sizeX = 1;

    public int int1 = -1;

    public int int2 = 16;

    public int anim = -1;

    public int[] multi = null;

    public int multivarbit = -1;

    public int multivar = -1;

    public int[] _av = null;

    public String name = "null";

    public String[] op = new String[5];

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1: {
                    int n = getUnsignedByte(buffer);
                    if (n > 0) {
                        _p = new int[n];
                        models = new int[n];
                        for(int var5 = 0; var5 < n; ++var5) {
                            models[var5] = getUnsignedShort(buffer);
                            _p[var5] = getUnsignedByte(buffer);
                        }
                    }
                    break;
                }
                case 2:
                    name = getString(buffer);
                    break;
                case 5: {
                    int n = getUnsignedByte(buffer);
                    if (n > 0) {
                        _p = null;
                        models = new int[n];
                        for(int var5 = 0; var5 < n; ++var5) {
                            models[var5] = getUnsignedShort(buffer);
                        }
                    }
                    break;
                }
                case 14:
                    sizeX = getUnsignedByte(buffer);
                    break;
                case 15:
                    sizeY = getUnsignedByte(buffer);
                    break;
                case 17:
                    interactType = 0;
                    boolean1 = false;
                    break;
                case 18:
                    boolean1 = false;
                    break;
                case 19:
                    int1 = getUnsignedByte(buffer);
                    break;
                case 21:
                    hillskew = 0;
                    break;
                case 22:
                    sharelight = true;
                    break;
                case 23:
                    occlude = true;
                    break;
                case 24:
                    anim = getUnsignedShortM1(buffer);
                    break;
                case 27:
                    interactType = 1;
                    break;
                case 28:
                    int2 = getUnsignedByte(buffer);
                    break;
                case 29:
                    ambient = buffer.get();
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34: {
                    var s = getString(buffer);
                    if (!s.equalsIgnoreCase("Hidden")) op[opcode - 30] = s;
                    break;
                }
                case 39:
                    contrast = buffer.get() * 25;
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
                case 62:
                    isRotated = true;
                    break;
                case 64:
                    clipped = false;
                    break;
                case 65:
                    modelSizeX = getUnsignedShort(buffer);
                    break;
                case 66:
                    modelHeight = getUnsignedShort(buffer);
                    break;
                case 67:
                    modelSizeY = getUnsignedShort(buffer);
                    break;
                case 68:
                    mapSceneId = getUnsignedShort(buffer);
                    break;
                case 69:
                    buffer.position(buffer.position() + 1);
                    break;
                case 70:
                    offsetX = getUnsignedShort(buffer);
                    break;
                case 71:
                    offsetHeight = getUnsignedShort(buffer);
                    break;
                case 72:
                    offsetY = getUnsignedShort(buffer);
                    break;
                case 73:
                    boolean2 = true;
                    break;
                case 74:
                    isSolid = true;
                    break;
                case 75:
                    int3 = getUnsignedByte(buffer);
                    break;
                case 78:
                    ambientSoundId = getUnsignedShort(buffer);
                    int4 = getUnsignedByte(buffer);
                    break;
                case 79: {
                    int5 = getUnsignedShort(buffer);
                    int6 = getUnsignedShort(buffer);
                    int4 = getUnsignedByte(buffer);
                    int n = getUnsignedByte(buffer);
                    _av = new int[n];
                    for(int var5 = 0; var5 < n; ++var5) {
                        _av[var5] = getUnsignedShort(buffer);
                    }
                    break;
                }
                case 81:
                    hillskew = getUnsignedByte(buffer) * 256;
                    break;
                case 82:
                    mapIconId = getUnsignedShort(buffer);
                    break;
                case 77:
                case 92: {
                    multivarbit = getUnsignedShortM1(buffer);
                    multivar = getUnsignedShortM1(buffer);
                    int last = -1;
                    if (opcode == 92) last = getUnsignedShortM1(buffer);
                    int n = getUnsignedByte(buffer);
                    multi = new int[n + 2];
                    for(int i = 0; i <= n; i++) {
                        multi[i] = getUnsignedShortM1(buffer);
                    }
                    multi[n + 1] = last;
                    break;
                }
                case 249:
                    params = decodeParams(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }

    @Override protected void postDecode() {
        if (-1 == int1) {
            int1 = 0;
            if (models != null && (null == _p || _p[0] == 10)) {
                int1 = 1;
            }

            for(int var2 = 0; var2 < 5; ++var2) {
                if (op[var2] != null) {
                    int1 = 1;
                }
            }
        }

        if (int3 == -1) {
            int3 = 0 != interactType ? 1 : 0;
        }

        if (isSolid) {
            interactType = 0;
            boolean1 = false;
        }
    }
}
