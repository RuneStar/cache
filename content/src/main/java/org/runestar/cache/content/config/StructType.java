package org.runestar.cache.content.config;

import org.runestar.cache.content.io.Input;

import java.util.Map;

public final class StructType extends ConfigType {

    public static final int GROUP = 34;

    public Map<Integer, Object> params = null;

    @Override protected void decode0(Input in) {
        while (true) {
            int code = in.g1();
            switch (code) {
                case 0:
                    return;
                case 249:
                    params = in.decodeParams();
                    break;
                default:
                    unrecognisedCode(code);
            }
        }
    }
}
