package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

public class ItemDefinition {

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

    public short[] colorFind = null;

    public short[] colorReplace = null;

    public short[] textureFind = null;

    public short[] textureReplace = null;

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
            int opcode = Byte.toUnsignedInt(buffer.get());
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    inventoryModel = buffer.getShort();
                    break;
                case 2:
                    name = Bytes.readString(buffer);
                    break;
                case 4:
                    zoom2d = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 5:
                    xan2d = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 6:
                    yan2d = Short.toUnsignedInt(buffer.getShort());
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
                    maleModel0 = Short.toUnsignedInt(buffer.getShort());
                    maleOffset = Byte.toUnsignedInt(buffer.get());
                    break;
                case 24:
                    maleModel1 = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 25:
                    femaleModel0 = Short.toUnsignedInt(buffer.getShort());
                    femaleOffset = Byte.toUnsignedInt(buffer.get());
                    break;
                case 26:
                    femaleModel1 = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                    var s = Bytes.readString(buffer);
                    if (!s.equals("Hidden")) {
                        options[opcode - 30] = s;
                    }
                    break;
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                    interfaceOptions[opcode - 35] = Bytes.readString(buffer);
                    break;
                case 40:
                    int colors = Byte.toUnsignedInt(buffer.get());
                    colorFind = new short[colors];
                    colorReplace = new short[colors];
                    for (int i = 0; i < colors; i++) {
                        colorFind[i] = buffer.getShort();
                        colorReplace[i] = buffer.getShort();
                    }
                    break;
                case 41:
                    int textures = Byte.toUnsignedInt(buffer.get());
                    textureFind = new short[textures];
                    textureReplace = new short[textures];
                    for (int i = 0; i < textures; i++) {
                        textureFind[i] = buffer.getShort();
                        textureReplace[i] = buffer.getShort();
                    }
                    break;
                case 42:
                    shiftClickDropIndex = Byte.toUnsignedInt(buffer.get());
                    break;
                case 65:
                    isTradeable = true;
                    break;
                case 78:
                    maleModel2 = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 79:
                    femaleModel2 = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 90:
                    maleHeadModel = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 91:
                    femaleHeadModel = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 92:
                    maleHeadModel2 = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 93:
                    femaleHeadModel2 = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 95:
                    zan2d = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 97:
                    notedId = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 98:
                    notedTemplate = Short.toUnsignedInt(buffer.getShort());
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
                    countObj[opcode - 100] = Short.toUnsignedInt(buffer.getShort());
                    countCo[opcode - 100] = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 110:
                    resizeX = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 111:
                    resizeY = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 112:
                    resizeZ = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 113:
                    ambient = Byte.toUnsignedInt(buffer.get());
                    break;
                case 114:
                    contrast = Byte.toUnsignedInt(buffer.get());
                    break;
                case 115:
                    team = Byte.toUnsignedInt(buffer.get());
                    break;
                case 139:
                    boughtId = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 140:
                    boughtTemplateId = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 148:
                    placeholderId = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 149:
                    placeholderTemplateId = Short.toUnsignedInt(buffer.getShort());
                    break;
                case 249:
                    Bytes.readParams(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(String.valueOf(opcode));
            }
        }
    }
}
