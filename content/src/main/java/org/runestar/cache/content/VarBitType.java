package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class VarBitType extends ConfigType {

    public int baseVar = 0;

    public int startBit = 0;

    public int endBit = 0;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int code = getUnsignedByte(buffer);
            switch (code) {
                case 0:
                    return;
                case 1:
                    baseVar = getUnsignedShort(buffer);
                    startBit = getUnsignedByte(buffer);
                    endBit = getUnsignedByte(buffer);
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
