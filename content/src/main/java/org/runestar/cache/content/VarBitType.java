package org.runestar.cache.content;

import java.nio.ByteBuffer;

public class VarBitType extends ConfigType {

    public int baseVar = 0;

    public int startBit = 0;

    public int endBit = 0;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = buffer.getUnsignedByte();
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    baseVar = buffer.getUnsignedShort();
                    startBit = buffer.getUnsignedByte();
                    endBit = buffer.getUnsignedByte();
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
