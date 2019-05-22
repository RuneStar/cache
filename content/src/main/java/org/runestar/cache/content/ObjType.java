package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

public final class ObjType {

    public String name = "null";

    public int resizeX = 128;

    public int resizeY = 128;

    public int resizeZ = 128;

    public int xan2d = 0;

    public int yan2d = 0;

    public int zan2d = 0;

    public int price = 0;

    public boolean isTradeable = false;

    public int stackable = 0;

    public int inventoryModel = 0;

    public boolean isMembersOnly = false;

    public short[] recolorFrom = null;

    public short[] recolorTo = null;

    public short[] retextureFrom = null;

    public short[] retextureTo = null;

    public int zoom2d = 200_000;

    public int xOffset2d = 0;

    public int yOffset2d = 0;

    public int ambient = 0;

    public int contrast = 0;

    public int[] countCo = null;

    public int[] countObj = null;

    public String[] options = new String[] { null, null, "Take", null, null };

    public String[] interfaceOptions = new String[] { null, null, null, null, "Drop" };

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

    public void read(ByteBuffer buffer) {
        while (true) {
            int opcode = Buffer.getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    inventoryModel = buffer.getShort();
                    break;
                case 2:
                    name = Buffer.getString(buffer);
                    break;
                case 4:
                    zoom2d = Buffer.getUnsignedShort(buffer);
                    break;
                case 5:
                    xan2d = Buffer.getUnsignedShort(buffer);
                    break;
                case 6:
                    yan2d = Buffer.getUnsignedShort(buffer);
                    break;
                case 7:
                    xOffset2d = buffer.getShort();
                    break;
                case 8:
                    yOffset2d = buffer.getShort();
                    break;
                case 11:
                    stackable = 1;
                    break;
                case 12:
                    price = buffer.getInt();
                    break;
                case 16:
                    isMembersOnly = true;
                    break;
                case 23:
                    maleModel0 = Buffer.getUnsignedShort(buffer);
                    maleOffset = Buffer.getUnsignedByte(buffer);
                    break;
                case 24:
                    maleModel1 = Buffer.getUnsignedShort(buffer);
                    break;
                case 25:
                    femaleModel0 = Buffer.getUnsignedShort(buffer);
                    femaleOffset = Buffer.getUnsignedByte(buffer);
                    break;
                case 26:
                    femaleModel1 = Buffer.getUnsignedShort(buffer);
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                    var s = Buffer.getString(buffer);
                    if (!s.equals("Hidden")) {
                        options[opcode - 30] = s;
                    }
                    break;
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                    interfaceOptions[opcode - 35] = Buffer.getString(buffer);
                    break;
                case 40:
                    int colors = Buffer.getUnsignedByte(buffer);
                    recolorFrom = new short[colors];
                    recolorTo = new short[colors];
                    for (int i = 0; i < colors; i++) {
                        recolorFrom[i] = buffer.getShort();
                        recolorTo[i] = buffer.getShort();
                    }
                    break;
                case 41:
                    int textures = Buffer.getUnsignedByte(buffer);
                    retextureFrom = new short[textures];
                    retextureTo = new short[textures];
                    for (int i = 0; i < textures; i++) {
                        retextureFrom[i] = buffer.getShort();
                        retextureTo[i] = buffer.getShort();
                    }
                    break;
                case 42:
                    shiftClickDropIndex = Buffer.getUnsignedByte(buffer);
                    break;
                case 65:
                    isTradeable = true;
                    break;
                case 78:
                    maleModel2 = Buffer.getUnsignedShort(buffer);
                    break;
                case 79:
                    femaleModel2 = Buffer.getUnsignedShort(buffer);
                    break;
                case 90:
                    maleHeadModel = Buffer.getUnsignedShort(buffer);
                    break;
                case 91:
                    femaleHeadModel = Buffer.getUnsignedShort(buffer);
                    break;
                case 92:
                    maleHeadModel2 = Buffer.getUnsignedShort(buffer);
                    break;
                case 93:
                    femaleHeadModel2 = Buffer.getUnsignedShort(buffer);
                    break;
                case 95:
                    zan2d = Buffer.getUnsignedShort(buffer);
                    break;
                case 97:
                    notedId = Buffer.getUnsignedShort(buffer);
                    break;
                case 98:
                    notedTemplate = Buffer.getUnsignedShort(buffer);
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
                    countObj[opcode - 100] = Buffer.getUnsignedShort(buffer);
                    countCo[opcode - 100] = Buffer.getUnsignedShort(buffer);
                    break;
                case 110:
                    resizeX = Buffer.getUnsignedShort(buffer);
                    break;
                case 111:
                    resizeY = Buffer.getUnsignedShort(buffer);
                    break;
                case 112:
                    resizeZ = Buffer.getUnsignedShort(buffer);
                    break;
                case 113:
                    ambient = Buffer.getUnsignedByte(buffer);
                    break;
                case 114:
                    contrast = Buffer.getUnsignedByte(buffer);
                    break;
                case 115:
                    team = Buffer.getUnsignedByte(buffer);
                    break;
                case 139:
                    boughtId = Buffer.getUnsignedShort(buffer);
                    break;
                case 140:
                    boughtTemplateId = Buffer.getUnsignedShort(buffer);
                    break;
                case 148:
                    placeholderId = Buffer.getUnsignedShort(buffer);
                    break;
                case 149:
                    placeholderTemplateId = Buffer.getUnsignedShort(buffer);
                    break;
                case 249:
                    params = Buffer.getParams(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
