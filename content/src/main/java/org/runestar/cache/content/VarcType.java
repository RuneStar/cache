package org.runestar.cache.content;

import java.nio.ByteBuffer;

public final class VarcType extends ConfigType {

    public boolean persist = false;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = buffer.getUnsignedByte();
            switch (opcode) {
                case 0:
                    return;
                case 2:
                    persist = true;
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
