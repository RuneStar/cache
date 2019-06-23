package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

public final class ObjType extends ConfigType {

    public String name = "null";

    public int resizex = 128;

    public int resizey = 128;

    public int resizez = 128;

    public int xan2d = 0;

    public int yan2d = 0;

    public int zan2d = 0;

    public int cost = 0;

    public boolean stockmarket = false;

    public int stackable = 0;

    public int model = 0;

    public boolean members = false;

    public short[] recol_s = null;

    public short[] recol_d = null;

    public short[] retex_s = null;

    public short[] retex_d = null;

    public int zoom2d = 200_000;

    public int xof2d = 0;

    public int yof2d = 0;

    public int ambient = 0;

    public int contrast = 0;

    public int[] countco = null;

    public int[] countobj = null;

    public String[] op = new String[] { null, null, "Take", null, null };

    public String[] iop = new String[] { null, null, null, null, "Drop" };

    public int manwear = -1;

    public int manwear2 = -1;

    public int manwear3 = -1;

    public int manwearyoff = 0;

    public int manhead = -1;

    public int manhead2 = -1;

    public int womanwear = -1;

    public int womanwear2 = -1;

    public int womanwear3 = -1;

    public int womanwearyoff = 0;

    public int womanhead = -1;

    public int womanhead2 = -1;

    public int certlink = -1;

    public int certtemplate = -1;

    public int team = 0;

    public int shiftclickindex = -2;

    public int boughtlink = -1;

    public int boughttemplate = -1;

    public int placeholderlink = -1;

    public int placeholdertemplate = -1;

    public Map<Integer, Object> params = null;

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
                    name = buffer.getString();
                    break;
                case 4:
                    zoom2d = buffer.getUnsignedShort();
                    break;
                case 5:
                    xan2d = buffer.getUnsignedShort();
                    break;
                case 6:
                    yan2d = buffer.getUnsignedShort();
                    break;
                case 7:
                    xof2d = buffer.getShort();
                    break;
                case 8:
                    yof2d = buffer.getShort();
                    break;
                case 11:
                    stackable = 1;
                    break;
                case 12:
                    cost = buffer.getInt();
                    break;
                case 16:
                    members = true;
                    break;
                case 23:
                    manwear = buffer.getUnsignedShort();
                    manwearyoff = buffer.getUnsignedByte();
                    break;
                case 24:
                    manwear2 = buffer.getUnsignedShort();
                    break;
                case 25:
                    womanwear = buffer.getUnsignedShort();
                    womanwearyoff = buffer.getUnsignedByte();
                    break;
                case 26:
                    womanwear2 = buffer.getUnsignedShort();
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34: {
                    var s = buffer.getString();
                    if (!s.equalsIgnoreCase("Hidden")) op[opcode - 30] = s;
                    break;
                }
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                    iop[opcode - 35] = buffer.getString();
                    break;
                case 40:
                case 41:{
                    int n = buffer.getUnsignedByte();
                    recol_s = new short[n];
                    recol_d = new short[n];
                    for (int i = 0; i < n; i++) {
                        recol_s[i] = buffer.getShort();
                        recol_d[i] = buffer.getShort();
                    }
                    break;
                }
                case 42:
                    shiftclickindex = buffer.getUnsignedByte();
                    break;
                case 65:
                    stockmarket = true;
                    break;
                case 78:
                    manwear3 = buffer.getUnsignedShort();
                    break;
                case 79:
                    womanwear3 = buffer.getUnsignedShort();
                    break;
                case 90:
                    manhead = buffer.getUnsignedShort();
                    break;
                case 91:
                    womanhead = buffer.getUnsignedShort();
                    break;
                case 92:
                    manhead2 = buffer.getUnsignedShort();
                    break;
                case 93:
                    womanhead2 = buffer.getUnsignedShort();
                    break;
                case 95:
                    zan2d = buffer.getUnsignedShort();
                    break;
                case 97:
                    certlink = buffer.getUnsignedShort();
                    break;
                case 98:
                    certtemplate = buffer.getUnsignedShort();
                    break;
                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                    if (countobj == null) {
                        countobj = new int[10];
                        countco = new int[10];
                    }
                    countobj[opcode - 100] = buffer.getUnsignedShort();
                    countco[opcode - 100] = buffer.getUnsignedShort();
                    break;
                case 110:
                    resizex = buffer.getUnsignedShort();
                    break;
                case 111:
                    resizey = buffer.getUnsignedShort();
                    break;
                case 112:
                    resizez = buffer.getUnsignedShort();
                    break;
                case 113:
                    ambient = buffer.getUnsignedByte();
                    break;
                case 114:
                    contrast = buffer.getUnsignedByte() * 5;
                    break;
                case 115:
                    team = buffer.getUnsignedByte();
                    break;
                case 139:
                    boughtlink = buffer.getUnsignedShort();
                    break;
                case 140:
                    boughttemplate = buffer.getUnsignedShort();
                    break;
                case 148:
                    placeholderlink = buffer.getUnsignedShort();
                    break;
                case 149:
                    placeholdertemplate = buffer.getUnsignedShort();
                    break;
                case 249:
                    params = buffer.decodeParams();
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
