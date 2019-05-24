package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

public final class StructType extends ConfigType {

    public Map<Integer, Object> params = null;

    @Override
    protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = Buf.getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 249:
                    params = Buf.decodeParams(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
