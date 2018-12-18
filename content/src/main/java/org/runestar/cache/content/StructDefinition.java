package org.runestar.cache.content;

import java.nio.ByteBuffer;
import java.util.Map;

public class StructDefinition {

    public Map<Integer, Object> params = null;

    public void read(ByteBuffer buffer) {
        while (true) {
            int opcode = Byte.toUnsignedInt(buffer.get());
            switch (opcode) {
                case 0:
                    return;
                case 249:
                    params = Bytes.readParams(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(String.valueOf(opcode));
            }
        }
    }
}
