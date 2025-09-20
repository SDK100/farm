package scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {
	public final Vector3f position = new Vector3f();
	public final Vector3f rotationEuler = new Vector3f();
	public final Vector3f scale = new Vector3f();
	public int parent = -1;
	
	public Matrix4f toMatrix(Matrix4f dst) {
		return dst.identity()
				.translate(position)
				.rotateX(rotationEuler.x)
				.rotateY(rotationEuler.y)
				.rotateZ(rotationEuler.z)
				.scale(scale);
	}

}
