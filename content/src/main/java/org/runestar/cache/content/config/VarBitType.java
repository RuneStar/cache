package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class VarBitType extends ConfigType {

    public static final int GROUP = 14;

    public int baseVar = 0;

    public int startBit = 0;

    public int endBit = 0;

    @Override
    protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 1:
                    baseVar = in.g2();
                    startBit = in.g1();
                    endBit = in.g1();
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
