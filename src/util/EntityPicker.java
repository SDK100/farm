package util;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class EntityPicker {
	
	public static void rayFromNDC(float nx, float ny, Matrix4f invViewProj, Vector3f outOrigin, Vector3f outDir) {
		
		Vector4f pNear = new Vector4f(nx, ny, -1f, 1f);
		Vector4f pFar = new Vector4f(nx, ny, 1f, 1f);
		
		invViewProj.transform(pNear).div(pNear.w);
		invViewProj.transform(pFar).div(pFar.w);
		
		outOrigin.set(pNear.x, pNear.y, pNear.z);
		outDir.set(pFar.x - pNear.x, pFar.y - pNear.y, pFar.z - pNear.z).normalize();
	}
	
	//ray plane intersection (returns true if hit)
	public static boolean rayPlaneY(Vector3f ro, Vector3f rd, float planeY, Vector3f out) {
		float denom = rd.y;
		if (Math.abs(denom) < 1e-6f) return false;
		float t = (planeY - ro.y)/ denom;
		if (t < 0f) return false;
		out.set(ro.x + rd.x * t, planeY, ro.z + rd.z * t);
		return true;
	}
	

}
