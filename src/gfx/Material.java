package gfx;

import org.joml.Vector3f;

public class Material {
	
	public Texture2D albedo;
	public final Vector3f tint = new Vector3f(1f,1f,1f);
	
	public Material(Texture2D albedo) {this.albedo = albedo;}
	public Material() {}

}
