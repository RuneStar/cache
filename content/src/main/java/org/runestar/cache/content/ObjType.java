package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

public final class ObjType extends ConfigType {

    public String name = "null";

    public int resizeX = 128;

    public int resizeY = 128;

    public int resizeZ = 128;

    public int xan2d = 0;

    public int yan2d = 0;

    public int zan2d = 0;

    public int cost = 0;

    public boolean tradeable = false;

    public int stackable = 0;

    public int inventoryModel = 0;

    public boolean members = false;

    public short[] recols = null;

    public short[] recold = null;

    public short[] retextureFrom = null;

    public short[] retextureTo = null;

    public int zoom2d = 200_000;

    public int xof2d = 0;

    public int yof2d = 0;

    public int ambient = 0;

    public int contrast = 0;

    public int[] countCo = null;

    public int[] countObj = null;

    public String[] ops = new String[] { null, null, "Take", null, null };

    public String[] iops = new String[] { null, null, null, null, "Drop" };

    public int maleModel0 = -1;

    public int maleModel1 = -1;

    public int maleModel2 = -1;

    public int maleOffset = 0;

    public int maleHeadModel = -1;

    public int maleHeadModel2 = -1;

    public int femaleModel0 = -1;

    public int femaleModel1 = -1;

    public int femaleModel2 = -1;

    public int femaleOffset = 0;

    public int femaleHeadModel = -1;

    public int femaleHeadModel2 = -1;

    public int notedId = -1;

    public int notedTemplate = -1;

    public int team = 0;

    public int shiftClickDropIndex = -2;

    public int boughtId = -1;

    public int boughtTemplateId = -1;

    public int placeholderId = -1;

    public int placeholderTemplateId = -1;

    public Map<Integer, Object> params = null;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = Buf.getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    inventoryModel = buffer.getShort();
                    break;
                case 2:
                    name = Buf.getString(buffer);
                    break;
                case 4:
                    zoom2d = Buf.getUnsignedShort(buffer);
                    break;
                case 5:
                    xan2d = Buf.getUnsignedShort(buffer);
                    break;
                case 6:
                    yan2d = Buf.getUnsignedShort(buffer);
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
                    maleModel0 = Buf.getUnsignedShort(buffer);
                    maleOffset = Buf.getUnsignedByte(buffer);
                    break;
                case 24:
                    maleModel1 = Buf.getUnsignedShort(buffer);
                    break;
                case 25:
                    femaleModel0 = Buf.getUnsignedShort(buffer);
                    femaleOffset = Buf.getUnsignedByte(buffer);
                    break;
                case 26:
                    femaleModel1 = Buf.getUnsignedShort(buffer);
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                    var s = Buf.getString(buffer);
                    if (!s.equals("Hidden")) {
                        ops[opcode - 30] = s;
                    }
                    break;
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                    iops[opcode - 35] = Buf.getString(buffer);
                    break;
                case 40:
                    int colors = Buf.getUnsignedByte(buffer);
                    recols = new short[colors];
                    recold = new short[colors];
                    for (int i = 0; i < colors; i++) {
                        recols[i] = buffer.getShort();
                        recold[i] = buffer.getShort();
                    }
                    break;
                case 41:
                    int textures = Buf.getUnsignedByte(buffer);
                    retextureFrom = new short[textures];
                    retextureTo = new short[textures];
                    for (int i = 0; i < textures; i++) {
                        retextureFrom[i] = buffer.getShort();
                        retextureTo[i] = buffer.getShort();
                    }
                    break;
                case 42:
                    shiftClickDropIndex = Buf.getUnsignedByte(buffer);
                    break;
                case 65:
                    tradeable = true;
                    break;
                case 78:
                    maleModel2 = Buf.getUnsignedShort(buffer);
                    break;
                case 79:
                    femaleModel2 = Buf.getUnsignedShort(buffer);
                    break;
                case 90:
                    maleHeadModel = Buf.getUnsignedShort(buffer);
                    break;
                case 91:
                    femaleHeadModel = Buf.getUnsignedShort(buffer);
                    break;
                case 92:
                    maleHeadModel2 = Buf.getUnsignedShort(buffer);
                    break;
                case 93:
                    femaleHeadModel2 = Buf.getUnsignedShort(buffer);
                    break;
                case 95:
                    zan2d = Buf.getUnsignedShort(buffer);
                    break;
                case 97:
                    notedId = Buf.getUnsignedShort(buffer);
                    break;
                case 98:
                    notedTemplate = Buf.getUnsignedShort(buffer);
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
                    if (countObj == null) {
                        countObj = new int[10];
                        countCo = new int[10];
                    }
                    countObj[opcode - 100] = Buf.getUnsignedShort(buffer);
                    countCo[opcode - 100] = Buf.getUnsignedShort(buffer);
                    break;
                case 110:
                    resizeX = Buf.getUnsignedShort(buffer);
                    break;
                case 111:
                    resizeY = Buf.getUnsignedShort(buffer);
                    break;
                case 112:
                    resizeZ = Buf.getUnsignedShort(buffer);
                    break;
                case 113:
                    ambient = Buf.getUnsignedByte(buffer);
                    break;
                case 114:
                    contrast = Buf.getUnsignedByte(buffer);
                    break;
                case 115:
                    team = Buf.getUnsignedByte(buffer);
                    break;
                case 139:
                    boughtId = Buf.getUnsignedShort(buffer);
                    break;
                case 140:
                    boughtTemplateId = Buf.getUnsignedShort(buffer);
                    break;
                case 148:
                    placeholderId = Buf.getUnsignedShort(buffer);
                    break;
                case 149:
                    placeholderTemplateId = Buf.getUnsignedShort(buffer);
                    break;
                case 249:
                    params = Buf.decodeParams(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
