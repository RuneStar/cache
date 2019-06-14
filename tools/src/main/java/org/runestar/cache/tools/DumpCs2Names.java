package org.runestar.cache.tools;

import org.runestar.cache.content.EnumType;
import org.runestar.cache.content.IDKType;
import org.runestar.cache.content.LocType;
import org.runestar.cache.content.NPCType;
import org.runestar.cache.content.ObjType;
import org.runestar.cache.content.ParamType;
import org.runestar.cache.content.SeqType;
import org.runestar.cache.content.StructType;
import org.runestar.cache.format.disk.DiskCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class DumpCs2Names {

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("gen"));

        var objNames = new TreeMap<Integer, String>();
        var locNames = new TreeMap<Integer, String>();
        var paramTypes = new TreeMap<Integer, Integer>();
        var modelNames = new TreeMap<Integer, String>();
        var structNames = new TreeMap<Integer, String>();
        var npcNames = new TreeMap<Integer, String>();
        var seqNames = new TreeMap<Integer, String>();

        try (var disk = DiskCache.open(Path.of(".cache"))) {
            var cache = MemCache.of(disk);



            var objModels = new HashMap<Integer, Integer>();
            for (var file : cache.archive(2).group(10).files()) {
                var obj = new ObjType();
                obj.decode(file.data());
                var name = escape(obj.name);
                if (name == null) continue;
                objNames.put(file.id(), name);
                objModels.putIfAbsent(obj.inventoryModel, file.id());
                if (obj.certlink != -1) {
                    objNames.put(obj.certlink, "cert_" + name);
                }
                if (obj.placeholderlink != -1) {
                    objNames.put(obj.placeholderlink, "placeholder_" + name);
                }
                if (obj.boughtlink != -1) {
                    objNames.put(obj.boughtlink, "bought_" + name);
                }
                if (obj.countco != null) {
                    for (var i = 0; i < obj.countco.length; i++) {
                        var count = obj.countco[i];
                        if (count == 0) break;
                        var countId = obj.countobj[i];
                        objNames.putIfAbsent(countId, name + "_x" + count);
                    }
                }
            }
            var objToPrayer = new EnumType();
            objToPrayer.decode(cache.archive(2).group(8).file(496).data());
            var prayerToName = new EnumType();
            prayerToName.decode(cache.archive(2).group(8).file(860).data());
            for (var file : cache.archive(2).group(10).files()) {
                if (objNames.containsKey(file.id())) continue ;
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
                    continue;
                }
                var origId = objModels.get(obj.inventoryModel);
                if (origId != null) {
                    objNames.put(file.id(), "dummy_" + objNames.get(origId));
                }
            }


            for (var file : cache.archive(2).group(6).files()) {
                var loc = new LocType();
                loc.decode(file.data());
                var name = escape(loc.name);
                if (name != null) locNames.put(file.id(), name);
            }
            for (var file : cache.archive(2).group(6).files()) {
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
                if (name != null) {
                    locNames.put(file.id(), name);
                    for (var locId : loc.transforms) {
                        if (locId == -1) continue;
                        locNames.putIfAbsent(locId, name);
                    }
                }
            }
            for (var file : cache.archive(2).group(6).files()) {
                var name = locNames.get(file.id());
                if (name == null) continue;
                var loc = new LocType();
                loc.decode(file.data());
                if (loc.models != null) {
                    for (var n : loc.models) {
                        if (n != 16238) modelNames.putIfAbsent(n, name);
                    }
                }
                if (loc.animationId != -1) seqNames.putIfAbsent(loc.animationId, name);
            }



            for (var file : cache.archive(2).group(11).files()) {
                var param = new ParamType();
                param.decode(file.data());
                paramTypes.put(file.id(), (int) param.type);
            }



            var structNameKeys = new int[]{610,660,682,689,732};
            for (var file : cache.archive(2).group(34).files()) {
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



            for (var file : cache.archive(2).group(9).files()) {
                var npc = new NPCType();
                npc.decode(file.data());
                var name = escape(npc.name);
                if (name != null) npcNames.put(file.id(), name);
            }
            for (var file : cache.archive(2).group(9).files()) {
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
                if (name != null) {
                    npcNames.put(file.id(), name);
                    for (var npcId : npc.transforms) {
                        if (npcId == -1) continue;
                        npcNames.putIfAbsent(npcId, name);
                    }
                }
            }
            for (var file : cache.archive(2).group(9).files()) {
                var name = npcNames.get(file.id());
                if (name == null) continue;
                var npc = new NPCType();
                npc.decode(file.data());
                if (npc.idleSeq != -1) seqNames.putIfAbsent(npc.idleSeq, name + "_idle");
                if (npc.turnSeq != -1) seqNames.putIfAbsent(npc.turnSeq, name + "_turn");
                if (npc.turnLeftSeq != -1) seqNames.putIfAbsent(npc.turnLeftSeq, name + "_turnleft");
                if (npc.turnRightSeq != -1) seqNames.putIfAbsent(npc.turnRightSeq, name + "_turnright");
                if (npc.walkSeq != -1) seqNames.putIfAbsent(npc.walkSeq, name + "_walk");
                if (npc.walkLeftSeq != -1) seqNames.putIfAbsent(npc.walkLeftSeq, name + "_walkleft");
                if (npc.walkRightSeq != -1) seqNames.putIfAbsent(npc.walkRightSeq, name + "_walkright");
            }



            for (var file : cache.archive(2).group(12).files()) {
                var seq = new SeqType();
                seq.decode(file.data());
                if (seq.weapon >= 512) {
                    var weaponName = objNames.get(seq.weapon - 512);
                    if (weaponName != null) seqNames.putIfAbsent(file.id(), weaponName);
                }
                if (seq.shield >= 512) {
                    var shieldName = objNames.get(seq.shield - 512);
                    if (shieldName != null) seqNames.putIfAbsent(file.id(), shieldName);
                }
            }

            String[] bodyPartNames = new String[]{"hair","jaw","torso","arms","hands","legs","feet"};
            for (var file : cache.archive(2).group(3).files()) {
                var idk = new IDKType();
                idk.decode(file.data());
                String name;
                if (idk.bodyPart >= 7) {
                    name = "female_" + bodyPartNames[idk.bodyPart - 7];
                } else {
                    name = "male_" + bodyPartNames[idk.bodyPart];
                }
                for (var m : idk.models) {
                    if (m != -1) modelNames.putIfAbsent(m, name);

                }
                for (var m : idk.models2) {
                    if (m != -1) modelNames.putIfAbsent(m, name);
                }
            }

//            for (var file : cache.archive(2).group(13).files()) {
//                var sa = new SpotAnimType();
//                sa.decode(file.data());
//                if (sa.seq == -1 || sa.model == -1) continue;
//                var modelName = modelNames.get(sa.model);
//                if (modelName != null) seqNames.putIfAbsent(sa.seq, modelName);
//                var seqName = seqNames.get(sa.model);
//                if (seqName != null) modelNames.putIfAbsent(sa.model, seqName);
//            }
        }

        write("param-types.tsv", paramTypes);
        write("obj-names.tsv", objNames);
        write("loc-names.tsv", locNames);
        write("model-names.tsv", modelNames);
        write("struct-names.tsv", structNames);
        write("npc-names.tsv", npcNames);
        write("seq-names.tsv", seqNames);
    }

    private static void write(String fileName, SortedMap<Integer, ?> names) throws IOException {
        try (var w = Files.newBufferedWriter(Path.of("gen", fileName))) {
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
