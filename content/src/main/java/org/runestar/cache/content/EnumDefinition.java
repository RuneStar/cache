package org.runestar.cache.content;

import java.nio.ByteBuffer;

public class EnumDefinition {

    public int[] intVals = null;

    public char keyType = 0;

    public char valType = 0;

    public String defaultString = null;

    public int defaultInt = 0;

    public int size = 0;

    public int[] keys = null;

    public String[] stringVals = null;

    public void read(ByteBuffer buffer) {
        while (true) {
            int opcode = Byte.toUnsignedInt(buffer.get());
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    keyType = (char) buffer.get();
                    break;
                case 2:
                    valType = (char) buffer.get();
                    break;
                case 3:
                    defaultString = Bytes.readString(buffer);
                    break;
                case 4:
                    defaultInt = buffer.getInt();
                    break;
                case 5:
                    size = Short.toUnsignedInt(buffer.getShort());
                    keys = new int[size];
                    stringVals = new String[size];
                    for (int i = 0; i < size; i++) {
                        keys[i] = buffer.getInt();
                        stringVals[i] = Bytes.readString(buffer);
                    }
                    break;
                case 6:
                    size = Short.toUnsignedInt(buffer.getShort());
                    keys = new int[size];
                    intVals = new int[size];
                    for (int i = 0; i < size; i++) {
                        keys[i] = buffer.getInt();
                        intVals[i] = buffer.getInt();
                    }
                    break;
                default:
                    throw new UnsupportedOperationException(String.valueOf(opcode));
            }
        }
    }
}
