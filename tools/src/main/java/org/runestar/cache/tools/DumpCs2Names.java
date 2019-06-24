package org.runestar.cache.tools;

import org.runestar.cache.content.ConfigType;
import org.runestar.cache.content.EnumType;
import org.runestar.cache.content.IDKType;
import org.runestar.cache.content.LocType;
import org.runestar.cache.content.NPCType;
import org.runestar.cache.content.ObjType;
import org.runestar.cache.content.ParamType;
import org.runestar.cache.content.StructType;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.SortedMap;
import java.util.TreeMap;

public class DumpCs2Names {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of(".cs2"));

        var objNames = new TreeMap<Integer, String>();
        var locNames = new TreeMap<Integer, String>();
        var paramTypes = new TreeMap<Integer, Integer>();
        var modelNames = new TreeMap<Integer, String>();
        var structNames = new TreeMap<Integer, String>();
        var npcNames = new TreeMap<Integer, String>();
        var seqNames = new TreeMap<Integer, String>();

        try (var disk = DiskCache.open(Path.of(".cache"))) {
            var cache = MemCache.of(disk);

            String[] bodyPartNames = new String[]{"hair","jaw","torso","arms","hands","legs","feet"};
            for (var file : cache.archive(ConfigType.ARCHIVE).group(IDKType.GROUP).files()) {
                var idk = new IDKType();
                idk.decode(file.data());
                String name = (idk.bodyPart >= 7 ? "female_" : "male_") + bodyPartNames[idk.bodyPart % 7];
                for (var m : idk.head) {
                    if (m != -1) modelNames.putIfAbsent(m, name);
                }
                for (var m : idk.models) {
                    if (m != -1) modelNames.putIfAbsent(m, name);
                }
            }

            for (var file : cache.archive(ConfigType.ARCHIVE).group(ParamType.GROUP).files()) {
                var param = new ParamType();
                param.decode(file.data());
                paramTypes.put(file.id(), (int) param.type);
            }

            var structNameKeys = new int[]{610,660,682,689,732};
            for (var file : cache.archive(ConfigType.ARCHIVE).group(StructType.GROUP).files()) {
                var struct = new StructType();
                struct.decode(file.data());
                if (struct.params == null) continue;
                for (var key : structNameKeys) {
                    var name = struct.params.get(key);
                    if (name != null) {
                        structNames.put(file.id(), escape((String) name));
                        break;
                    }
                }
            }

            for (var file : cache.archive(ConfigType.ARCHIVE).group(ObjType.GROUP).files()) {
                var obj = new ObjType();
                obj.decode(file.data());
                var name = escape(obj.name);
                if (obj.certtemplate != -1) objNames.putIfAbsent(obj.certtemplate, "certtemplate");
                if (obj.placeholdertemplate != -1) objNames.putIfAbsent(obj.placeholdertemplate, "placeholdertemplate");
                if (obj.boughttemplate != -1) objNames.putIfAbsent(obj.boughttemplate, "boughttemplate");
                if (name == null) continue;
                objNames.put(file.id(), name);
                if (obj.countco != null) {
                    for (var i = 0; i < obj.countco.length; i++) {
                        var count = obj.countco[i];
                        if (count == 0) break;
                        objNames.putIfAbsent(obj.countobj[i], name + "_x" + count);
                    }
                }
                if (obj.certtemplate == -1 && obj.certlink >= 0) objNames.put(obj.certlink, "cert_" + name);
                if (obj.placeholdertemplate == -1 && obj.placeholderlink >= 0) objNames.put(obj.placeholderlink, "placeholder_" + name);
                if (obj.boughttemplate == -1 && obj.boughtlink >= 0) objNames.put(obj.boughtlink, "bought_" + name);
            }
            for (var file : cache.archive(ConfigType.ARCHIVE).group(ObjType.GROUP).files()) {
                var name = objNames.get(file.id());
                if (name == null) continue;
                var obj = new ObjType();
                obj.decode(file.data());
                if (obj.model > 0) modelNames.putIfAbsent(obj.model, name);
                if (obj.manwear != -1) modelNames.putIfAbsent(obj.manwear, name);
                if (obj.manwear2 != -1) modelNames.putIfAbsent(obj.manwear2, name);
                if (obj.manwear3 != -1) modelNames.putIfAbsent(obj.manwear3, name);
                if (obj.womanwear != -1) modelNames.putIfAbsent(obj.womanwear, name);
                if (obj.womanwear2 != -1) modelNames.putIfAbsent(obj.womanwear2, name);
                if (obj.womanwear3 != -1) modelNames.putIfAbsent(obj.womanwear3, name);
                if (obj.manhead != -1) modelNames.putIfAbsent(obj.manhead, name);
                if (obj.manhead2 != -1) modelNames.putIfAbsent(obj.manhead2, name);
                if (obj.womanhead != -1) modelNames.putIfAbsent(obj.womanhead, name);
                if (obj.womanhead2 != -1) modelNames.putIfAbsent(obj.womanhead2, name);
            }
            var objToPrayer = new EnumType();
            objToPrayer.decode(cache.archive(ConfigType.ARCHIVE).group(EnumType.GROUP).file(496).data());
            var prayerToName = new EnumType();
            prayerToName.decode(cache.archive(ConfigType.ARCHIVE).group(EnumType.GROUP).file(860).data());
            for (var file : cache.archive(ConfigType.ARCHIVE).group(ObjType.GROUP).files()) {
                if (objNames.containsKey(file.id())) continue;
                var obj = new ObjType();
                obj.decode(file.data());
                var spellName = obj.params == null ? null : (String) obj.params.get(601);
                if (spellName != null) {
                    objNames.put(file.id(), escape(spellName));
                    continue;
                }
                var prayer = objToPrayer.getInt(file.id());
                if (prayer != -1) {
                    var prayerName = prayerToName.getString(prayer);
                    objNames.put(file.id(), escape(prayerName));
                }
            }

            for (var file : cache.archive(ConfigType.ARCHIVE).group(LocType.GROUP).files()) {
                var loc = new LocType();
                loc.decode(file.data());
                var name = escape(loc.name);
                if (name != null) locNames.put(file.id(), name);
            }
            for (var file : cache.archive(ConfigType.ARCHIVE).group(LocType.GROUP).files()) {
                if (locNames.containsKey(file.id())) continue;
                var loc = new LocType();
                loc.decode(file.data());
                if (loc.transforms == null) continue;
                String name = null;
                for (var locId : loc.transforms) {
                    if (locId == -1) continue;
                    name = locNames.get(locId);
                    if (name != null) break;
                }
                if (name == null) continue;
                locNames.put(file.id(), name);
                for (var locId : loc.transforms) {
                    if (locId == -1) continue;
                    locNames.putIfAbsent(locId, name);
                }
            }
            for (var file : cache.archive(ConfigType.ARCHIVE).group(LocType.GROUP).files()) {
                var name = locNames.get(file.id());
                if (name == null) continue;
                var loc = new LocType();
                loc.decode(file.data());
                if (loc.models != null) {
                    for (var n : loc.models) {
                        modelNames.putIfAbsent(n, name);
                    }
                }
                if (loc.anim != -1) seqNames.putIfAbsent(loc.anim, name);
            }

            for (var file : cache.archive(ConfigType.ARCHIVE).group(NPCType.GROUP).files()) {
                var npc = new NPCType();
                npc.decode(file.data());
                var name = escape(npc.name);
                if (name != null) npcNames.put(file.id(), name);
            }
            for (var file : cache.archive(ConfigType.ARCHIVE).group(NPCType.GROUP).files()) {
                if (npcNames.containsKey(file.id())) continue;
                var npc = new NPCType();
                npc.decode(file.data());
                if (npc.transforms == null) continue;
                String name = null;
                for (var npcId : npc.transforms) {
                    if (npcId == -1) continue;
                    name = npcNames.get(npcId);
                    if (name != null) break;
                }
                if (name == null) continue;
                npcNames.put(file.id(), name);
                for (var npcId : npc.transforms) {
                    if (npcId == -1) continue;
                    npcNames.putIfAbsent(npcId, name);
                }
            }
            for (var file : cache.archive(ConfigType.ARCHIVE).group(NPCType.GROUP).files()) {
                var name = npcNames.get(file.id());
                if (name == null) continue;
                var npc = new NPCType();
                npc.decode(file.data());
                if (file.id() == 13 && name.equals("piles")) name = "human";
                if (npc.readyanim != -1) seqNames.putIfAbsent(npc.readyanim, name + "_ready");
                if (npc.walkanim != -1) seqNames.putIfAbsent(npc.walkanim, name + "_walk_f");
                if (npc.walkbackanim != -1) seqNames.putIfAbsent(npc.walkbackanim, name + "_walk_b");
                if (npc.walkleftanim != -1) seqNames.putIfAbsent(npc.walkleftanim, name + "_walk_l");
                if (npc.walkrightanim != -1) seqNames.putIfAbsent(npc.walkrightanim, name + "_walk_r");
                if (npc.models != null) {
                    for (var m : npc.models) modelNames.putIfAbsent(m, name);
                }
                if (npc.head != null) {
                    for (var m : npc.head) modelNames.putIfAbsent(m, name);
                }
            }

//            for (var file : cache.archive(2).group(12).files()) {
//                var seq = new SeqType();
//                seq.decode(file.data());
//                if (seq.weapon >= 512) {
//                    var weaponName = objNames.get(seq.weapon - 512);
//                    if (weaponName != null) seqNames.putIfAbsent(file.id(), weaponName);
//                }
//                if (seq.shield >= 512) {
//                    var shieldName = objNames.get(seq.shield - 512);
//                    if (shieldName != null) seqNames.putIfAbsent(file.id(), shieldName);
//                }
//            }

//            for (var file : cache.archive(2).group(13).files()) {
//                var spot = new SpotType();
//                spot.decode(file.data());
//                if (spot.seq == -1 || spot.model == -1) continue;
//                var modelName = modelNames.get(spot.model);
//                if (modelName != null) seqNames.putIfAbsent(spot.seq, modelName);
//                var seqName = seqNames.get(spot.model);
//                if (seqName != null) modelNames.putIfAbsent(spot.model, seqName);
//            }

            for (var file : cache.archive(ConfigType.ARCHIVE).group(ObjType.GROUP).files()) {
                if (objNames.containsKey(file.id())) continue;
                var obj = new ObjType();
                obj.decode(file.data());
                var modelName = modelNames.get(obj.model);
                if (modelName == null) continue;
                objNames.put(file.id(), "dummy_" + modelName);
            }
        }

        modelNames.remove(16238);
        objNames.remove(6512);
//        seqNames.remove(3354);
        objNames.remove(8245);

        write("param-types.tsv", paramTypes);
        write("obj-names.tsv", objNames);
        write("loc-names.tsv", locNames);
        write("model-names.tsv", modelNames);
        write("struct-names.tsv", structNames);
        write("npc-names.tsv", npcNames);
        write("seq-names.tsv", seqNames);
    }

    private static void write(String fileName, SortedMap<Integer, ?> names) throws IOException {
        try (var w = Files.newBufferedWriter(Path.of(".cs2", fileName))) {
            for (var e : names.entrySet()) {
                w.write(e.getKey().toString());
                w.write('\t');
                w.write(e.getValue().toString());
                w.write('\n');
            }
        }
    }

    private static String escape(String name) {
        if (name.equalsIgnoreCase("null")) return null;
        name = name.toLowerCase()
                .replaceAll("([']|<.*?>)", "")
                .replaceAll("[- /)(.,! ]", "_")
                .replaceAll("[%&+?]", "_")
                .replaceAll("(^_+|_+$)", "")
                .replaceAll("_{2,}", "_");
        if (name.isBlank()) return null;
        return name;
    }
}
