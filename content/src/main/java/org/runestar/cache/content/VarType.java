package org.runestar.cache.content;

import java.nio.ByteBuffer;

public final class VarType extends ConfigType {

    public int type = 0;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = buffer.getUnsignedByte();
            switch (opcode) {
                case 0:
                    return;
                case 5:
                    type = buffer.getUnsignedShort();
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
