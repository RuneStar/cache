package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.getUnsignedByte;

public final class VarcType extends ConfigType {

    public boolean persist = false;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int code = getUnsignedByte(buffer);
            switch (code) {
                case 0:
                    return;
                case 2:
                    persist = true;
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
