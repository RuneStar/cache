package org.runestar.cache;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface WritableStore extends ReadableStore {

    void setArchive(int index, int archive, ByteBuffer buf) throws IOException;
}
