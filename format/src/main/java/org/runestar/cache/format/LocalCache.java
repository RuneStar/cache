package org.runestar.cache.format;

import java.nio.ByteBuffer;

public interface LocalCache extends AutoCloseable {

    int MASTER_ARCHIVE = 255;

    ByteBuffer getGroupCompressed(int archive, int group);

    default Group getGroup(int archive, int group) {
        return getGroup(archive, group, null);
    }

    default Group getGroup(int archive, int group, int[] key) {
        var gc = getGroupCompressed(archive, group);
        return gc == null ? null : Group.decompress(gc, key);
    }

    default Index getIndex(int archive) {
        var g = getGroup(MASTER_ARCHIVE, archive);
        return g == null ? null : Index.decode(g.data);
    }

    void setGroupCompressed(int archive, int group, ByteBuffer buf);

    int getArchiveCount();
}
