package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.runestar.cache.content.Buf.*;

public final class StructType extends ConfigType {

    public static final int GROUP = 34;

    public Map<Integer, Object> params = null;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 249:
                    params = decodeParams(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
