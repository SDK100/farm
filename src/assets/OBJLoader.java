package assets;

import gfx.MeshPNT;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Minimal Wavefront OBJ loader: supports v/vt/vn and triangle faces (f v/vt/vn). */
public final class OBJLoader {
    public static MeshPNT load(String path) {
        List<Vector3f> positions = new ArrayList<>();
        List<Vector2f> uvs = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();

        // key: "v/vt/vn"  -> index
        Map<String, Integer> indexMap = new HashMap<>();
        List<Float> vertexData = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        try {
            for (String line : Files.readAllLines(Path.of(path))) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\s+");
                switch (p[0]) {
                    case "v" -> positions.add(new Vector3f(
                            Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3])));
                    case "vt" -> uvs.add(new Vector2f(
                            Float.parseFloat(p[1]), Float.parseFloat(p[2])));
                    case "vn" -> normals.add(new Vector3f(
                            Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3])));
                    case "f" -> {
                        // Triangulate if quad (fan)
                    	int[] faceIdx = new int[p.length - 1];
                    	for (int i = 1; i < p.length; i++) {
                    	    faceIdx[i-1] = getIndex(p[i], positions, uvs, normals, indexMap, vertexData);
                    	}
                    	for (int i = 1; i + 1 < faceIdx.length; i++) {
                    	    indices.add(faceIdx[0]);
                    	    indices.add(faceIdx[i]);
                    	    indices.add(faceIdx[i+1]);
                    	}

                    }
                    default -> {}
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("OBJ read failed: " + path, e);
        }

        // to arrays
        float[] verts = new float[vertexData.size()];
        for (int i = 0; i < vertexData.size(); i++) verts[i] = vertexData.get(i);
        int[] idx = indices.stream().mapToInt(i -> i).toArray();

        return new MeshPNT(verts, idx);
    }

    // Appends to vertexData if needed, returns index
    private static int getIndex(String token,
            List<Vector3f> pos, List<Vector2f> uv, List<Vector3f> nor,
            Map<String,Integer> map, List<Float> out) {
    	Integer ex = map.get(token);
    	if (ex != null) return ex;

    	String[] parts = token.split("/");
    	int vi = parseOne(parts, 0, pos.size());
    	int ti = parseOne(parts, 1, uv.size());
    	int ni = parseOne(parts, 2, nor.size());
    	
    	if (vi < 0) throw new IllegalArgumentException("OBJ vertex index missing in token: " + token);

    	Vector3f P = pos.get(vi);
    	Vector3f N = (ni >= 0 ? nor.get(ni) : new Vector3f(0,1,0));
    	Vector2f T = (ti >= 0 ? uv.get(ti)  : new Vector2f(0,0));

    	// append interleaved P(3) N(3) T(2)
    	out.add(P.x); out.add(P.y); out.add(P.z);
    	out.add(N.x); out.add(N.y); out.add(N.z);
    	out.add(T.x); out.add(T.y);

    	int newIndex = (out.size() / 8) - 1;
    	map.put(token, newIndex);
    	return newIndex;
    }


    // OBJ uses 1-based indices, negatives allowed (relative). We only support positive for simplicity.
    private static int parseOne(String[] parts, int at, int size) {
        if (parts.length <= at || parts[at].isEmpty()) return -1; // missing vt/vn ok
        int v = Integer.parseInt(parts[at]);
        if (v < 0) v = size + v; else v = v - 1; // OBJ is 1-based; negatives are relative
        if (v < 0 || v >= size) throw new IllegalArgumentException("OBJ index out of range: " + parts[at] + " size=" + size);
        return v;
    }
}
