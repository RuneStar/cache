package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class ParamType extends ConfigType {

    public boolean b = true;

    public byte type = 0;

    public int intkey = 0;

    public String stringkey = null;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int opcode = getUnsignedByte(buffer);
            switch (opcode) {
                case 0:
                    return;
                case 1:
                    type = buffer.get();
                    break;
                case 2:
                    intkey = buffer.getInt();
                    break;
                case 4:
                    b = false;
                    break;
                case 5:
                    stringkey = getString(buffer);
                    break;
                default:
                    throw new UnsupportedOperationException(Integer.toString(opcode));
            }
        }
    }
}
