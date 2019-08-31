package org.runestar.cache.tools;

import org.runestar.cache.content.config.*;
import org.runestar.cache.format.Cache;

import java.util.SortedMap;
import java.util.TreeMap;

public class NameExtractor {

    public final SortedMap<Integer, String> objs = new TreeMap<>();

    public final SortedMap<Integer, String> locs = new TreeMap<>();

    public final SortedMap<Integer, String> models = new TreeMap<>();

    public final SortedMap<Integer, String> structs = new TreeMap<>();

    public final SortedMap<Integer, String> npcs = new TreeMap<>();

    public final SortedMap<Integer, String> seqs = new TreeMap<>();

    public final SortedMap<Integer, String> stats = new TreeMap<>();

    public final SortedMap<Integer, String> prayers = new TreeMap<>();

    public NameExtractor(Cache c) {
        MemCache cache = MemCache.of(c);

        var statNames = new EnumType();
        statNames.decode(cache.archive(EnumType.ARCHIVE).group(EnumType.GROUP).file(680).data());
        for (int id : statNames.keys) {
            stats.put(id, escape(statNames.getString(id)));
        }

        var prayerNames = new EnumType();
        prayerNames.decode(cache.archive(ConfigType.ARCHIVE).group(EnumType.GROUP).file(860).data());
        for (int id : prayerNames.keys) {
            prayers.put(id, escape(prayerNames.getString(id)));
        }

        var bodyPartNames = new String[]{"hair", "jaw", "torso", "arms", "hands", "legs", "feet"};
        for (var file : cache.archive(ConfigType.ARCHIVE).group(IDKType.GROUP).files()) {
            var idk = new IDKType();
            idk.decode(file.data());
            String name = (idk.bodyPart >= 7 ? "female_" : "male_") + bodyPartNames[idk.bodyPart % 7];
            for (var m : idk.head) {
                if (m != -1) models.putIfAbsent(m, name);
            }
            for (var m : idk.models) {
                if (m != -1) models.putIfAbsent(m, name);
            }
        }

        var structNameKeys = new int[]{610, 660, 682, 689, 732};
        for (var file : cache.archive(ConfigType.ARCHIVE).group(StructType.GROUP).files()) {
            var struct = new StructType();
            struct.decode(file.data());
            if (struct.params == null) continue;
            for (var key : structNameKeys) {
                var name = struct.params.get(key);
                if (name != null) {
                    structs.put(file.id(), escape((String) name));
                    break;
                }
            }
        }

        for (var file : cache.archive(ConfigType.ARCHIVE).group(ObjType.GROUP).files()) {
            var obj = new ObjType();
            obj.decode(file.data());
            var name = escape(obj.name);
            if (obj.certtemplate != -1) objs.putIfAbsent(obj.certtemplate, "template_for_cert");
            if (obj.placeholdertemplate != -1) objs.putIfAbsent(obj.placeholdertemplate, "template_for_placeholder");
            if (obj.boughttemplate != -1) objs.putIfAbsent(obj.boughttemplate, "template_for_bought");
            if (name == null) continue;
            objs.put(file.id(), name);
            if (obj.countco != null) {
                for (var i = 0; i < obj.countco.length; i++) {
                    var count = obj.countco[i];
                    if (count == 0) break;
                    objs.putIfAbsent(obj.countobj[i], name + "_" + count);
                }
            }
            if (obj.certtemplate == -1 && obj.certlink >= 0) objs.put(obj.certlink, "cert_" + name);
            if (obj.placeholdertemplate == -1 && obj.placeholderlink >= 0) objs.put(obj.placeholderlink, "placeholder_" + name);
            if (obj.boughttemplate == -1 && obj.boughtlink >= 0) objs.put(obj.boughtlink, "bought_" + name);
        }
        for (var file : cache.archive(ConfigType.ARCHIVE).group(ObjType.GROUP).files()) {
            var name = objs.get(file.id());
            if (name == null) continue;
            var obj = new ObjType();
            obj.decode(file.data());
            if (obj.model > 0) models.putIfAbsent(obj.model, name);
            if (obj.manwear != -1) models.putIfAbsent(obj.manwear, name);
            if (obj.manwear2 != -1) models.putIfAbsent(obj.manwear2, name);
            if (obj.manwear3 != -1) models.putIfAbsent(obj.manwear3, name);
            if (obj.womanwear != -1) models.putIfAbsent(obj.womanwear, name);
            if (obj.womanwear2 != -1) models.putIfAbsent(obj.womanwear2, name);
            if (obj.womanwear3 != -1) models.putIfAbsent(obj.womanwear3, name);
            if (obj.manhead != -1) models.putIfAbsent(obj.manhead, name);
            if (obj.manhead2 != -1) models.putIfAbsent(obj.manhead2, name);
            if (obj.womanhead != -1) models.putIfAbsent(obj.womanhead, name);
            if (obj.womanhead2 != -1) models.putIfAbsent(obj.womanhead2, name);
        }
        var objToPrayer = new EnumType();
        objToPrayer.decode(cache.archive(ConfigType.ARCHIVE).group(EnumType.GROUP).file(496).data());
        for (var file : cache.archive(ConfigType.ARCHIVE).group(ObjType.GROUP).files()) {
            if (objs.containsKey(file.id())) continue;
            var obj = new ObjType();
            obj.decode(file.data());
            var spellName = obj.params == null ? null : (String) obj.params.get(601);
            if (spellName != null) {
                objs.put(file.id(), escape(spellName));
                continue;
            }
            var prayer = objToPrayer.getInt(file.id());
            if (prayer != -1) {
                objs.put(file.id(), escape(prayers.get(prayer)));
            }
        }

        for (var file : cache.archive(ConfigType.ARCHIVE).group(LocType.GROUP).files()) {
            var loc = new LocType();
            loc.decode(file.data());
            var name = escape(loc.name);
            if (name != null) locs.put(file.id(), name);
        }
        for (var file : cache.archive(ConfigType.ARCHIVE).group(LocType.GROUP).files()) {
            if (locs.containsKey(file.id())) continue;
            var loc = new LocType();
            loc.decode(file.data());
            if (loc.multi == null) continue;
            String name = null;
            for (var locId : loc.multi) {
                if (locId == -1) continue;
                name = locs.get(locId);
                if (name != null) break;
            }
            if (name == null) continue;
            locs.put(file.id(), name + "_multi");
            for (var locId : loc.multi) {
                if (locId == -1) continue;
                locs.putIfAbsent(locId, name);
            }
        }
        for (var file : cache.archive(ConfigType.ARCHIVE).group(LocType.GROUP).files()) {
            var name = locs.get(file.id());
            if (name == null) continue;
            var loc = new LocType();
            loc.decode(file.data());
            if (loc.multi != null && name.endsWith("_multi")) name = name.substring(0, name.length() - "_multi".length());
            if (loc.models != null) {
                for (var n : loc.models) {
                    models.putIfAbsent(n, name);
                }
            }
            if (loc.anim != -1) seqs.putIfAbsent(loc.anim, name);
        }

        for (var file : cache.archive(ConfigType.ARCHIVE).group(NPCType.GROUP).files()) {
            var npc = new NPCType();
            npc.decode(file.data());
            var name = escape(npc.name);
            if (name != null) npcs.put(file.id(), name);
        }
        for (var file : cache.archive(ConfigType.ARCHIVE).group(NPCType.GROUP).files()) {
            if (npcs.containsKey(file.id())) continue;
            var npc = new NPCType();
            npc.decode(file.data());
            if (npc.multi == null) continue;
            String name = null;
            for (var npcId : npc.multi) {
                if (npcId == -1) continue;
                name = npcs.get(npcId);
                if (name != null) break;
            }
            if (name == null) continue;
            npcs.put(file.id(), name + "_multi");
            for (var npcId : npc.multi) {
                if (npcId == -1) continue;
                npcs.putIfAbsent(npcId, name);
            }
        }
        for (var file : cache.archive(ConfigType.ARCHIVE).group(NPCType.GROUP).files()) {
            var name = npcs.get(file.id());
            if (name == null) continue;
            var npc = new NPCType();
            npc.decode(file.data());
            if (npc.multi != null && name.endsWith("_multi")) name = name.substring(0, name.length() - "_multi".length());
            if (npc.models != null) {
                for (var m : npc.models) models.putIfAbsent(m, name);
            }
            if (npc.head != null) {
                for (var m : npc.head) models.putIfAbsent(m, name);
            }
            if (file.id() == 13 && name.equals("piles")) name = "human";
            if (npc.readyanim != -1) seqs.putIfAbsent(npc.readyanim, name + "_ready");
            if (npc.walkanim != -1) seqs.putIfAbsent(npc.walkanim, name + "_walk_f");
            if (npc.walkbackanim != -1) seqs.putIfAbsent(npc.walkbackanim, name + "_walk_b");
            if (npc.walkleftanim != -1) seqs.putIfAbsent(npc.walkleftanim, name + "_walk_l");
            if (npc.walkrightanim != -1) seqs.putIfAbsent(npc.walkrightanim, name + "_walk_r");
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
            if (objs.containsKey(file.id())) continue;
            var obj = new ObjType();
            obj.decode(file.data());
            var modelName = models.get(obj.model);
            if (modelName == null) continue;
            objs.put(file.id(), "dummy_" + modelName);
        }

        models.remove(16238);
        objs.remove(6512);
//        seqNames.remove(3354);
        objs.remove(8245);
    }

    protected String escape(String name) {
        if (name.equalsIgnoreCase("null")) return null;
        name = name.toLowerCase()
                .replaceAll("([']|<.*?>)", "")
                .replaceAll("[- /)(.,! ]", "_")
                .replaceAll("[%&+?]", "_")
                .replaceAll("(^_+|_+$)", "")
                .replaceAll("_{2,}", "_");
        return name.isBlank() ? null : name;
    }
}
