package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

public final class VarBitType extends ConfigType {

    public static final int GROUP = 14;

    public int baseVar = 0;

    public int startBit = 0;

    public int endBit = 0;

    public int value = 0;

    /**
     * The objective of this function is to calculate the varbit value based on the bit information given.
     * First the mask is created, then anded with the baseValue then right shifted based on the start bit.
     */
    int getValue(int baseValue, int start, int end) {
        int mask = 0;
        for (int i = start; i < end + 1; i++) {
            var val = 1 << i;
            mask = mask | val;
        }

        return (baseValue & mask) >> start;
    }


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
                    value = getValue(baseVar, startBit, endBit);
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
