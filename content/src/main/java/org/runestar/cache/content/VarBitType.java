package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public class VarBitType extends ConfigType {

    public int baseVar = 0;

    public int startBit = 0;

    public int endBit = 0;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    baseVar = getUnsignedShort(buffer);
                    startBit = getUnsignedByte(buffer);
                    endBit = getUnsignedByte(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
