package org.runestar.cache.format;

import org.runestar.cache.format.util.IO;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static org.runestar.cache.format.LocalCache.MASTER_ARCHIVE;

public final class CacheUpdate {

    private CacheUpdate() {}

    public static CompletableFuture<Void> update(RemoteCache remote, LocalCache local) {
        return remote.getMasterIndex().thenCompose(rm -> {
            var fs = new ArrayList<CompletableFuture<Void>>();
            for (int i = 0; i < rm.length; i++) {
                var rim = rm[i];
                var lig = local.getGroup(MASTER_ARCHIVE, i);
                var li = lig == null ? null : Index.decode(lig.data);
                if (li == null || rim.crc32 != lig.crc32 || rim.version != li.version) {
                    fs.add(updateArchive(remote, local, i, li));
                }
            }
            return IO.allOf(fs);
        });
    }

    public static CompletableFuture<Void> updateArchive(RemoteCache remote, LocalCache local, int archive) {
        return updateArchive(remote, local, archive, local.getIndex(archive));
    }

    private static CompletableFuture<Void> updateArchive(RemoteCache remote, LocalCache local, int archive, Index li) {
        return remote.getGroupCompressed(MASTER_ARCHIVE, archive)
                .thenCompose(igc -> {
                    local.setGroupCompressed(MASTER_ARCHIVE, archive, igc.duplicate());
                    return updateArchive0(remote, local, archive, Index.decode(Group.decompress(igc, null).data), li);
                });
    }

    private static CompletableFuture<Void> updateArchive0(RemoteCache remote, LocalCache local, int archive, Index ri, Index li) {
        var fs = new ArrayList<CompletableFuture<Void>>();
        int lj = 0;
        for (var rg : ri.groups) {
            Index.Group lg = null;
            while (li != null && lj < li.groups.length) {
                var g = li.groups[lj++];
                if (rg.id == g.id) {
                    lg = g;
                    break;
                } else if (rg.id < g.id) {
                    lj--;
                    break;
                }
            }
            if (lg == null || rg.crc32 != lg.crc32 || rg.version != lg.version) {
                fs.add(remote.getGroupCompressed(archive, rg.id).thenAccept(gc -> local.setGroupCompressed(archive, rg.id, gc)));
            }
        }
        return IO.allOf(fs);
    }
}
