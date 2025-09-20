package data;

import java.util.*;

public final class RecipeDB {
    private static final Map<String, RecipeDef> byId = new HashMap<>();
    public static void clear() { byId.clear(); }
    public static RecipeDef get(String id) { return byId.get(id); }
    public static Collection<RecipeDef> all() { return byId.values(); }

    public static void loadFromCba(String path) {
        clear();
        var blocks = CbaLoader.loadBlocks(path);
        for (var b : blocks) {
            if (!"recipe".equals(b.get("_type"))) continue;
            String id = b.get("_id");
            int timeMin = Integer.parseInt( require(b,"time") );
            String[] out = CbaLoader.parseItemCount( require(b,"output") );
            var reqs = CbaLoader.parseRequirements( require(b,"requires") );

            var def = new RecipeDef(id, timeMin, out[0], Integer.parseInt(out[1]));
            def.requires.putAll(reqs);
            byId.put(id, def);
        }
        if (byId.isEmpty()) throw new IllegalStateException("No recipes loaded from " + path);
    }

    private static String require(Map<String,String> b, String key) {
        String v = b.get(key);
        if (v == null) throw new IllegalArgumentException("Missing key '" + key + "' in recipe " + b.get("_id"));
        return v;
    }
}
