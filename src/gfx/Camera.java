package gfx;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
	
	private final Vector3f target = new Vector3f(0,0,0);
	private float distance = 4.0f;
	private float yaw = (float)Math.toRadians(45);
	private float pitch = (float)Math.toRadians(25);
	
	private final Matrix4f view = new Matrix4f();
	private final Matrix4f proj = new Matrix4f();
	
	public void setPrespective(float fovDeg, float aspect, float near, float far) {
		proj.identity().perspective((float)Math.toRadians(fovDeg), aspect, near, far);
	}
	
	public void orbit(float dYaw, float dPitch, float dZoom) {
		yaw += dYaw;
		pitch += dPitch;
		pitch = Math.max((float)Math.toRadians(-85), Math.min((float)Math.toRadians(85), pitch));
		distance = Math.max(1.0f, Math.min(50.0f, distance));
	}
	
	public Matrix4f viewMatrix() {
		float x = (float)(Math.cos(pitch) * Math.cos(yaw) * distance);
		float y = (float)(Math.sin(pitch)) * distance;
		float z = (float)(Math.cos(pitch) * Math.sin(yaw)) * distance;
		Vector3f eye = new Vector3f(target.x + x, target.y + y, target.z + z);
		view.identity().lookAt(eye, target, new Vector3f(0,1,0));
		return view;
	}
	
	public Matrix4f projMatrix() {
		return proj;
	}
}
