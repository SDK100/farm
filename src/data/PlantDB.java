package data;

import java.util.*;

public final class PlantDB {
    private static final Map<String, PlantDef> byId = new HashMap<>();
    public static void clear() { byId.clear(); }
    public static PlantDef get(String id) { return byId.get(id); }

    public static void loadFromCba(String path) {
        clear();
        var blocks = CbaLoader.loadBlocks(path);
        for (var b : blocks) {
            if (!"plant".equals(b.get("_type"))) continue;
            String id = b.get("_id");
            var minutes = CbaLoader.parseIntList( require(b, "minutesPerStage") );
            var meshes  = CbaLoader.parseStringList( require(b, "stageMeshes") );
            String yieldItem = require(b, "yieldItem");
            int yieldCount = Integer.parseInt( require(b, "yieldCount") );
            int slices = Integer.parseInt( b.getOrDefault("slicesPerItem", "1") );

            var def = new PlantDef(id, minutes, meshes, yieldItem, yieldCount, slices);
            byId.put(id, def);
        }
        if (byId.isEmpty()) throw new IllegalStateException("No plants loaded from " + path);
    }

    private static String require(Map<String,String> b, String key) {
        String v = b.get(key);
        if (v == null) throw new IllegalArgumentException("Missing key '" + key + "' in block " + b.get("_id"));
        return v;
    }

    public static Collection<PlantDef> all() { return byId.values(); }
}
