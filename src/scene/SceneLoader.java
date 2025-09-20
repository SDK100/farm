package scene;

import assets.Json.*;
import assets.Json;
import assets.OBJLoader;
import gfx.Material;
import gfx.MeshPNT;
import gfx.Texture2D;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public final class SceneLoader {
    public static Scene load(String path) {
        J root = Json.parseFile(path);
        if (!(root instanceof @SuppressWarnings("unused") JObj obj)) throw new RuntimeException("Scene root must be object");

        // caches so same mesh/texture reused
        Map<String, MeshPNT> meshCache = new HashMap<>();
        Map<String, Texture2D> texCache = new HashMap<>();

        Scene scene = new Scene();

        J arr = ((JObj)root).get("entities");
        if (!(arr instanceof JArr ents)) throw new RuntimeException("Scene must have 'entities' array");

        for (J j : ents.list) {
            if (!(j instanceof JObj e)) continue;
            Entity ent = scene.create();

            // transform
            var t = scene.addTransform(ent);
            vec3(e, "position", t.position);
            vec3(e, "rotationEuler", t.rotationEuler);
            vec3(e, "scale", t.scale);

            // renderable
            String objPath = str(e, "obj");
            if (objPath != null) {
                MeshPNT mesh = meshCache.computeIfAbsent(objPath, OBJLoader::load);

                Material mat = new Material();
                String texPath = str(e, "albedo");
                if (texPath != null) {
                    Texture2D tex = texCache.computeIfAbsent(texPath, p -> new Texture2D(p, true));
                    mat.albedo = tex;
                }
                vec3(e, "tint", mat.tint);

                scene.addRenderable(ent, new scene.Renderable(mesh, mat));
            }
        }
        return scene;
    }

    private static void vec3(JObj o, String key, Vector3f out){
        J v = o.get(key); if (!(v instanceof JArr a) || a.list.size()<3) return;
        out.set((float)((JNum)a.list.get(0)).v, (float)((JNum)a.list.get(1)).v, (float)((JNum)a.list.get(2)).v);
    }
    private static String str(JObj o, String key){
        J v = o.get(key); return (v instanceof JStr s) ? s.v : null;
    }
}
