package scene;

import gfx.Material;
import gfx.MeshPNT;

public class Renderable {
	
	public MeshPNT mesh;
	public Material mat;
	
	public Renderable(MeshPNT mesh, Material mat){
		this.mesh = mesh; this.mat = mat;
	}

}
