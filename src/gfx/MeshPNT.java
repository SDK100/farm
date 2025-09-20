package gfx;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class MeshPNT implements AutoCloseable {
	private final int vao, vbo, ebo;
	private final int indexCount;
	
	// pos(3) normal(3) uv(2)
	public MeshPNT(float[] vertices, int[] indices) {
	    indexCount = indices.length;

	    vao = GL30.glGenVertexArrays();
	    vbo = GL15.glGenBuffers();
	    ebo = GL15.glGenBuffers();

	    GL30.glBindVertexArray(vao);

	    // VBO (vertex data)
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
	    try (var stack = org.lwjgl.system.MemoryStack.stackPush()) {
	        FloatBuffer fb = stack.mallocFloat(vertices.length);
	        fb.put(vertices).flip();
	        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fb, GL15.GL_STATIC_DRAW);
	    }

	    // EBO (index data) — IMPORTANT: element array target!
	    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
	    try (var stack = org.lwjgl.system.MemoryStack.stackPush()) {
	        IntBuffer ib = stack.mallocInt(indexCount);
	        ib.put(indices).flip();
	        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ib, GL15.GL_STATIC_DRAW);
	    }

	    int stride = (3 + 3 + 2) * Float.BYTES;
	    GL20.glVertexAttribPointer(0, 3, GL15.GL_FLOAT, false, stride, 0L);
	    GL20.glEnableVertexAttribArray(0);
	    GL20.glVertexAttribPointer(1, 3, GL15.GL_FLOAT, false, stride, 3L * Float.BYTES);
	    GL20.glEnableVertexAttribArray(1);
	    GL20.glVertexAttribPointer(2, 2, GL15.GL_FLOAT, false, stride, 6L * Float.BYTES);
	    GL20.glEnableVertexAttribArray(2);

	    // leave EBO bound; just unbind the VAO
	    GL30.glBindVertexArray(0);
	}
	
	public static MeshPNT makeCubePNT(float s) {
		float h = s * 0.5f;
		
		float[] v = {
		        // +X
		        h,-h,-h,  1,0,0,  0,0,
		        h, h,-h,  1,0,0,  1,0,
		        h, h, h,  1,0,0,  1,1,
		        h,-h, h,  1,0,0,  0,1,
		        // -X
		       -h,-h, h, -1,0,0,  0,0,
		       -h, h, h, -1,0,0,  1,0,
		       -h, h,-h, -1,0,0,  1,1,
		       -h,-h,-h, -1,0,0,  0,1,
		        // +Y
		       -h, h,-h,  0,1,0,  0,0,
		        h, h,-h,  0,1,0,  1,0,
		        h, h, h,  0,1,0,  1,1,
		       -h, h, h,  0,1,0,  0,1,
		        // -Y
		       -h,-h, h,  0,-1,0, 0,0,
		        h,-h, h,  0,-1,0, 1,0,
		        h,-h,-h,  0,-1,0, 1,1,
		       -h,-h,-h,  0,-1,0, 0,1,
		        // +Z
		       -h,-h, h,  0,0,1,  0,0,
		        h,-h, h,  0,0,1,  1,0,
		        h, h, h,  0,0,1,  1,1,
		       -h, h, h,  0,0,1,  0,1,
		        // -Z
		        h,-h,-h,  0,0,-1, 0,0,
		       -h,-h,-h,  0,0,-1, 1,0,
		       -h, h,-h,  0,0,-1, 1,1,
		        h, h,-h,  0,0,-1,  0,1,
		    };
		
		int[] idx = new int[36];
		int[] base = {0,4,8,12,16,20};
		int k = 0;
		for (int b : base) {
			idx[k++] = b; idx[k++] = b+1; idx[k++] = b+2;
	        idx[k++] = b; idx[k++] = b+2; idx[k++] = b+3;
		}
		
		return new MeshPNT(v, idx);
	}
	
	public void draw() {
		GL30.glBindVertexArray(vao);
		GL20.glDrawElements(GL20.GL_TRIANGLES, indexCount, GL20.GL_UNSIGNED_INT, 0L);
		GL30.glBindVertexArray(0);
	}
	
	
	@Override public void close() throws Exception {
		GL30.glDeleteVertexArrays(vao);
		GL20.glDeleteBuffers(vbo);
		GL20.glDeleteBuffers(ebo);
	}
	
	

}
