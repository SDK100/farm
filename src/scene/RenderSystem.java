package scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import gfx.Material;
import gfx.Shader;

public class RenderSystem {
	private final Shader shader;
	private final Matrix4f model = new Matrix4f();
	
	public RenderSystem(Shader shader) {this.shader = shader;}
	
	public void render(Scene scene, Matrix4f view, Matrix4f proj, Vector3f sunDir, Vector3f sunColor, float ambient) {
		shader.bind();
		shader.setMat4("uView", view);
		shader.setMat4("uProj", proj);
		shader.setVec3("uSunDir", sunDir);
		shader.setFloat("uAmbient", ambient);
		shader.setInt("uAlbedo", 0);
		
		for (var e: scene.renderables.entrySet()) {
			int id = e.getKey();
			var rend = e.getValue();
			var tr = scene.transforms.get(id);
			if (rend == null || tr == null) continue;
			
			tr.toMatrix(model);
			shader.setMat4("uModel", model);
			
			Material m = rend.mat;
			if (m != null && m.albedo != null) m.albedo.bind(0);
			shader.setVec3("uTint", (m!= null) ? m.tint : new Vector3f(1,1,1));
			
			rend.mesh.draw();
		}
		Shader.unbind();
	}

}
