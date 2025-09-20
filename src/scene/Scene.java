package scene;

import java.util.HashMap;
import java.util.Map;

public class Scene implements AutoCloseable {
	private int nextId = 0;
	
	public final Map<Integer, Transform> transforms = new HashMap<>();
	public final Map<Integer, Renderable> renderables = new HashMap<>();
	
	public Entity create() {return new Entity(nextId++);}
	
	public Transform addTransform(Entity e) {var t = new Transform(); transforms.put(e.id, t); return t;}
	public Renderable addRenderable(Entity e, Renderable r) { renderables.put(e.id, r); return r;}
	
	
	@Override public void close() throws Exception {

		
	}

}
