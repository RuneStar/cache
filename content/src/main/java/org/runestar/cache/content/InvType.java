package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class InvType extends ConfigType {

    public int size = 0;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int code = getUnsignedByte(buffer);
            switch (code) {
                case 0:
                    return;
                case 1:
                    size = getUnsignedShort(buffer);
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
