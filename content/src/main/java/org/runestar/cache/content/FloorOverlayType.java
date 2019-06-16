package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.getMedium;
import static org.runestar.cache.content.Buf.getUnsignedByte;

public final class FloorOverlayType extends ConfigType {

    public boolean _o = true;

    public int rgb = 0;

    public int rgb2 = -1;

    public int texture = -1;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    rgb = getMedium(buffer);
                    break;
                case 2:
                    texture = getUnsignedByte(buffer);
                    break;
                case 5:
                    _o = false;
                    break;
                case 7:
                    rgb2 = getMedium(buffer);
                    break;
                case 8:
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
