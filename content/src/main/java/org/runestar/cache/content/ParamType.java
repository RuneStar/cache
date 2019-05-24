package org.runestar.cache.content;

import java.nio.ByteBuffer;

public final class ParamType extends ConfigType {

    public boolean b = true;

    public char type = 0;

    public int keyInt = 0;

    public String keyString = null;

    @Override
    protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = Buf.getUnsignedByte(buffer);
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
                    keyString = Buf.getString(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
