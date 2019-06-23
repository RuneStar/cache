package org.runestar.cache.content;

import java.nio.ByteBuffer;

public final class ParamType extends ConfigType {

    public boolean autodisable = true;

    public byte type = 0;

    public int defaultint = 0;

    public String defaultstr = null;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = buffer.getUnsignedByte();
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    type = buffer.get();
                    break;
                case 2:
                    defaultint = buffer.getInt();
                    break;
                case 4:
                    autodisable = false;
                    break;
                case 5:
                    defaultstr = buffer.getString();
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
