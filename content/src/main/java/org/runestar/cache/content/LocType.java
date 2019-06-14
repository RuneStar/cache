package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.runestar.cache.content.Buf.*;

public final class LocType extends ConfigType {

    public boolean isRotated = false;

    public boolean isSolid = false;

    public boolean nonFlatShading = false;

    public Map<Integer, Object> params = null;

    public int modelSizeY = 128;

    public int ambient = 0;

    public int modelHeight = 128;

    public int modelSizeX = 128;

    public int transformVarbit = -1;

    public int contrast = 0;

    public int transformConfigId = -1;

    public int offsetX = 0;

    public int offsetY = 0;

    public int offsetHeight = 0;

    public int clipType = -1;

    public int[] models = null;

    public int[] _p = null;

    public short[] recol_s = null;

    public short[] recol_d = null;

    public short[] retex_d = null;

    public short[] retex_s = null;

    public boolean boolean2 = false;

    public boolean clipped = true;

    public boolean boolean1 = true;

    public boolean modelClipped = false;

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

    public int animationId = -1;

    public int[] transforms = null;

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
                    int var4 = getUnsignedByte(buffer);
                    if (var4 > 0) {
                        _p = new int[var4];
                        models = new int[var4];
                        for(int var5 = 0; var5 < var4; ++var5) {
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
                    int var4 = getUnsignedByte(buffer);
                    if (var4 > 0) {
                        _p = null;
                        models = new int[var4];
                        for(int var5 = 0; var5 < var4; ++var5) {
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
                    clipType = 1;
                    break;
                case 22:
                    nonFlatShading = true;
                    break;
                case 23:
                    modelClipped = true;
                    break;
                case 24:
                    animationId = getUnsignedShort(buffer);
                    if (animationId == 0xFFFF) {
                        animationId = -1;
                    }
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
                    var action = getString(buffer);
                    if (!action.equals("Hidden")) {
                        op[opcode - 30] = action;
                    }
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
                    int var4 = getUnsignedByte(buffer);
                    _av = new int[var4];
                    for(int var5 = 0; var5 < var4; ++var5) {
                        _av[var5] = getUnsignedShort(buffer);
                    }
                    break;
                }
                case 81:
                    clipType = getUnsignedByte(buffer) * 256;
                    break;
                case 82:
                    mapIconId = getUnsignedShort(buffer);
                    break;
                case 77:
                case 92: {
                    transformVarbit = getUnsignedShort(buffer);
                    if (transformVarbit == 0xFFFF) {
                        transformVarbit = -1;
                    }

                    transformConfigId = getUnsignedShort(buffer);
                    if (0xFFFF == transformConfigId) {
                        transformConfigId = -1;
                    }

                    int var4 = -1;
                    if (92 == opcode) {
                        var4 = getUnsignedShort(buffer);
                        if (var4 == 0xFFFF) {
                            var4 = -1;
                        }
                    }

                    int var5 = getUnsignedByte(buffer);
                    transforms = new int[2 + var5];

                    for(int var6 = 0; var6 <= var5; ++var6) {
                        transforms[var6] = getUnsignedShort(buffer);
                        if (transforms[var6] == 0xFFFF) {
                            transforms[var6] = -1;
                        }
                    }

                    transforms[var5 + 1] = var4;
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
