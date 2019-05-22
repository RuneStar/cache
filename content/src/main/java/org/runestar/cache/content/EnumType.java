package org.runestar.cache.content;

import java.nio.ByteBuffer;

public final class EnumType {

    public int[] intVals = null;

    public char keyType = 0;

    public char valType = 0;

    public String defaultString = null;

    public int defaultInt = 0;

    public int size = 0;

    public int[] keys = null;

    public String[] stringVals = null;

    public void decode(ByteBuffer buffer) {
        while (true) {
            int opcode = Buf.getUnsignedByte(buffer);
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
                    defaultString = Buf.getString(buffer);
                    break;
                case 4:
                    defaultInt = buffer.getInt();
                    break;
                case 5:
                    size = Buf.getUnsignedShort(buffer);
                    keys = new int[size];
                    stringVals = new String[size];
                    for (int i = 0; i < size; i++) {
                        keys[i] = buffer.getInt();
                        stringVals[i] = Buf.getString(buffer);
                    }
                    break;
                case 6:
                    size = Buf.getUnsignedShort(buffer);
                    keys = new int[size];
                    intVals = new int[size];
                    for (int i = 0; i < size; i++) {
                        keys[i] = buffer.getInt();
                        intVals[i] = buffer.getInt();
                    }
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
