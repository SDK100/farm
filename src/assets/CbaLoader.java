package assets;

import game.CropDef;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public final class CbaLoader {

    public static Map<String, CropDef> load(Path file) {
        try {
            List<String> lines = Files.readAllLines(file);
            return parse(lines);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CBA file: " + file, e);
        }
    }

    private static Map<String, CropDef> parse(List<String> rawLines) {
        Map<String, CropDef> out = new HashMap<>();

        // pre-strip comments/trim
        List<String> lines = new ArrayList<>(rawLines.size());
        for (String r : rawLines) {
            String s = r;
            int hash = s.indexOf('#');
            if (hash >= 0) s = s.substring(0, hash);
            s = s.trim();
            if (!s.isEmpty()) lines.add(s);
        }

        CropDef.Builder current = null;
        String currentId = null;

        for (int ln = 0; ln < lines.size(); ln++) {
            String line = lines.get(ln);
            String[] tok = splitOnce(line);

            if (tok[0].equalsIgnoreCase("crop")) {
                if (current != null) throw syntax("nested crop not allowed", ln, line);
                if (tok[1].isEmpty()) throw syntax("missing crop id", ln, line);
                currentId = tok[1];
                current = new CropDef.Builder(currentId);
                continue;
            }

            if (tok[0].equalsIgnoreCase("end")) {
                if (current == null) throw syntax("'end' without 'crop'", ln, line);
                CropDef def = current.build();
                if (def.stages() == 0)
                    throw syntax("crop '"+currentId+"' has no 'stage' entries", ln, line);
                out.put(currentId, def);
                current = null;
                currentId = null;
                continue;
            }

            if (current == null) {
                throw syntax("content outside of 'crop' block", ln, line);
            }

            // inside a crop block
            if (tok[0].equalsIgnoreCase("minutesPerStage")) {
                if (tok[1].isEmpty()) throw syntax("minutesPerStage <float>", ln, line);
                try {
                    double m = Double.parseDouble(tok[1]);
                    current.minutesPerStage(m);
                } catch (NumberFormatException nfe) {
                    throw syntax("invalid minutesPerStage: " + tok[1], ln, line);
                }
                continue;
            }

            if (tok[0].equalsIgnoreCase("stage")) {
                // expect: "stage <idx> <path>"
                String rest = tok[1];
                int sp = rest.indexOf(' ');
                if (sp < 0) throw syntax("stage <index> <obj_path>", ln, line);
                String sIndex = rest.substring(0, sp).trim();
                String path   = rest.substring(sp + 1).trim();
                if (path.isEmpty()) throw syntax("stage missing path", ln, line);
                // we don't actually need the numeric index for storage; allow any order
                try {
                    Integer.parseInt(sIndex); // validate it is a number
                } catch (NumberFormatException nfe) {
                    throw syntax("stage index must be integer: " + sIndex, ln, line);
                }
                current.addStagePath(path);
                continue;
            }

            throw syntax("unknown directive: " + tok[0], ln, line);
        }

        if (current != null) throw syntax("missing 'end' for crop '" + currentId + "'", lines.size()-1, "");
        return out;
    }

    private static RuntimeException syntax(String msg, int ln, String line) {
        return new RuntimeException("CBA syntax error at line " + (ln+1) + ": " + msg + "  >> " + line);
    }

    /** Splits into directive + the rest (may be empty). */
    private static String[] splitOnce(String line) {
        int sp = line.indexOf(' ');
        if (sp < 0) return new String[]{ line, "" };
        String a = line.substring(0, sp).trim();
        String b = line.substring(sp + 1).trim();
        return new String[]{ a, b };
    }
}
