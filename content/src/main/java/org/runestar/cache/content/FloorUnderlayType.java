package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.getMedium;
import static org.runestar.cache.content.Buf.getUnsignedByte;

public final class FloorUnderlayType extends ConfigType {

    public int rgb = 0;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int code = getUnsignedByte(buffer);
            switch (code) {
                case 0:
                    return;
                case 1:
                    rgb = getMedium(buffer);
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
