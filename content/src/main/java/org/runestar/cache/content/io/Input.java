package org.runestar.cache.content.io;

import java.util.LinkedHashMap;
import java.util.Map;

public interface Input {

    Input duplicate(int offset);

    int remaining();

    byte peek();

    void skip(int n);

    byte g1s();

    short g2s();

    int g4s();

    String gjstr();

    default int g1() {
        return Byte.toUnsignedInt(g1s());
    }

    default int g2() {
        return Short.toUnsignedInt(g2s());
    }

    default int g3() {
        return (g2() << 8) | g1();
    }

    default String gjstr2() {
        if (g1s() != 0) throw new IllegalStateException();
        return gjstr();
    }

    default Map<Integer, Object> decodeParams() {
        int length = g1();
        var params = new LinkedHashMap<Integer, Object>(length);
        for (int i = 0; i < length; i++) {
            boolean isString = g1s() != 0;
            params.put(g3(), isString ? gjstr() : g4s());
        }
        return params;
    }

    default int gSmart1or2s() {
        return peek() < 0 ? g2() - 0xc000 : g1() - 0x40;
    }

    default int gSmart2or4() {
        if (peek() < 0) {
            return g4s() & Integer.MAX_VALUE;
        } else {
            int n = g2();
            return n == 0x7FFF ? -1 : n;
        }
    }

    default int g2m() {
        int n = g2();
        return n == 0xFFFF ? -1 : n;
    }
}
