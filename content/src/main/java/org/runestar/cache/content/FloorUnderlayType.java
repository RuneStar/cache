package org.runestar.cache.content;

import java.nio.ByteBuffer;

public final class FloorUnderlayType extends ConfigType {

    public int rgb = 0;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = buffer.getUnsignedByte();
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    rgb = buffer.getMedium();
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
