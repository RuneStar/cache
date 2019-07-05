package org.runestar.cache.content;

import java.nio.ByteBuffer;

import static org.runestar.cache.content.Buf.*;

public final class ParamType extends ConfigType {

    public static final int GROUP = 11;

    public boolean autodisable = true;

    public byte type = 0;

    public int defaultint = 0;

    public String defaultstr = null;

    @Override protected void decode0(ByteBuffer buffer) {
        while (true) {
            int code = getUnsignedByte(buffer);
            switch (code) {
                case 0:
                    return;
                case 1:
                    type = buffer.get();
                    break;
                case 2:
                    defaultint = buffer.getInt();
                    break;
                case 4:
                    autodisable = false;
                    break;
                case 5:
                    defaultstr = getString(buffer);
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
