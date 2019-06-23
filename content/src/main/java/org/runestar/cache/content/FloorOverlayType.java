package org.runestar.cache.content;

import java.nio.ByteBuffer;

public final class FloorOverlayType extends ConfigType {

    public boolean _o = true;

    public int rgb = 0;

    public int rgb2 = -1;

    public int texture = -1;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = buffer.getUnsignedByte();
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    rgb = buffer.getMedium();
                    break;
                case 2:
                    texture = buffer.getUnsignedByte();
                    break;
                case 5:
                    _o = false;
                    break;
                case 7:
                    rgb2 = buffer.getMedium();
                    break;
                case 8:
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
