package data;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 *
 *
 * Example plants.cba:
 * plant carrot {
 *   minutesPerStage = 2,3,4
 *   stageMeshes = res/meshes/carrot_0.obj,res/meshes/carrot_1.obj,res/meshes/carrot_2.obj
 *   yieldItem = carrot
 *   yieldCount = 1
 *   slicesPerItem = 3
 * }
 *
 * Example recipes.cba:
 * recipe carrot_soup {
 *   requires = carrot_slice:3
 *   time = 5
 *   output = carrot_soup:1
 * }
 */
public final class CbaLoader {
    private static final Pattern HEADER = Pattern.compile("\\s*(plant|recipe)\\s+([a-zA-Z0-9_\\-]+)\\s*\\{\\s*");
    private static final Pattern KV     = Pattern.compile("\\s*([a-zA-Z0-9_]+)\\s*=\\s*(.+?)\\s*");
    private static final Pattern CLOSER = Pattern.compile("\\s*}\\s*");

    public static List<Map<String,String>> loadBlocks(String path) {
        List<Map<String,String>> blocks = new ArrayList<>();
        List<String> lines;
        try { lines = Files.readAllLines(Path.of(path)); }
        catch (IOException e) { throw new RuntimeException("Failed to read: " + path, e); }

        Map<String,String> cur = null;
        String curType = null, curId = null;

        for (String raw : lines) {
            String line = stripComment(raw).trim();
            if (line.isEmpty()) continue;

            Matcher h = HEADER.matcher(line);
            if (h.matches()) {
                curType = h.group(1);
                curId   = h.group(2);
                cur = new LinkedHashMap<>();
                cur.put("_type", curType);
                cur.put("_id", curId);
                continue;
            }
            if (CLOSER.matcher(line).matches()) {
                if (cur != null) {
                    blocks.add(cur);
                    cur = null; curType = null; curId = null;
                }
                continue;
            }
            if (cur != null) {
                Matcher m = KV.matcher(line);
                if (m.matches()) {
                    cur.put(m.group(1), m.group(2));
                }
            }
        }
        return blocks;
    }

    private static String stripComment(String s) {
        int i = s.indexOf('#');
        if (i >= 0) return s.substring(0, i);
        return s;
    }

    // Helpers
    public static List<Integer> parseIntList(String csv) {
        String[] parts = csv.split(",");
        List<Integer> out = new ArrayList<>(parts.length);
        for (String p : parts) {
            if (!p.isBlank()) out.add(Integer.parseInt(p.trim()));
        }
        return out;
    }

    public static List<String> parseStringList(String csv) {
        String[] parts = csv.split(",");
        List<String> out = new ArrayList<>(parts.length);
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }

    public static Map<String,Integer> parseRequirements(String spec) {
        // "carrot_slice:3,tomato_slice:1"
        Map<String,Integer> m = new LinkedHashMap<>();
        for (String kv : spec.split(",")) {
            String[] p = kv.trim().split(":");
            if (p.length == 2) m.put(p[0].trim(), Integer.parseInt(p[1].trim()));
        }
        return m;
    }

    public static String[] parseItemCount(String spec) {
        // "carrot_soup:1" -> ["carrot_soup","1"]
        String[] p = spec.trim().split(":");
        if (p.length != 2) throw new IllegalArgumentException("Invalid item:count: " + spec);
        return new String[]{ p[0].trim(), p[1].trim() };
    }
}
