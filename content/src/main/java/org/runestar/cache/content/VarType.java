package org.runestar.cache.content;

import org.runestar.cache.content.io.Input;

public final class VarType extends ConfigType {

    public int type = 0;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 5:
                    type = in.g2();
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
