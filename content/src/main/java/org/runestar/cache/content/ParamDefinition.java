package org.runestar.cache.content;

import java.nio.ByteBuffer;

public class ParamDefinition {

    public boolean b = true;

    public char type = 0;

    public int keyInt = 0;

    public String keyString = null;

    public void read(ByteBuffer buffer) {
        while (true) {
            int opcode = Byte.toUnsignedInt(buffer.get());
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    type = (char) buffer.get();
                    break;
                case 2:
                    keyInt = buffer.getInt();
                    break;
                case 4:
                    b = false;
                    break;
                case 5:
                    keyString = Bytes.readString(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(String.valueOf(opcode));
            }
        }
    }
}
