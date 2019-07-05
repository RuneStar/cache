package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class VarType extends ConfigType {

    public int type = 0;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int code = getUnsignedByte(buffer);
            switch (code) {
                case 0:
                    return;
                case 5:
                    type = getUnsignedShort(buffer);
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
