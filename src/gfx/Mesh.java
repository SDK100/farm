package gfx;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Mesh implements AutoCloseable {
	private final int vao,vbo,ebo;
	private final int indexCount;
	
	//layout: position(3), normal(3), color(3)
	
	public Mesh(float[] vertices, int[] indices) {
		indexCount = indices.length;
		
		vao = GL30.glGenVertexArrays();
		vbo = GL30.glGenBuffers();
		ebo = GL30.glGenBuffers();
		
		GL30.glBindVertexArray(vao);
		
		GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, vbo);
		try(var stack = org.lwjgl.system.MemoryStack.stackPush()){
			FloatBuffer vb = stack.mallocFloat(vertices.length);
			vb.put(vertices).flip();
			GL30.glBufferData(GL20.GL_ARRAY_BUFFER, vb, GL20.GL_STATIC_DRAW);
		}
		
		GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, ebo);
		try(var stack = org.lwjgl.system.MemoryStack.stackPush()){
			IntBuffer ib = stack.mallocInt(indices.length);
			ib.put(indices).flip();
			GL30.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, ib, GL20.GL_STATIC_DRAW);
		}
		
		int stride = 9 * Float.BYTES;
		// aPos (location=0)
		GL20.glVertexAttribPointer(0, 3, GL15.GL_FLOAT, false, stride, 0L);
		GL20.glEnableVertexAttribArray(0);
		
		//aNormal (location = 1)
		GL20.glVertexAttribPointer(1, 3, GL15.GL_FLOAT, false, stride, 3L * Float.BYTES);
		GL20.glEnableVertexAttribArray(1);
		
		//aColor (location=2)
		GL20.glVertexAttribPointer(2, 3, GL15.GL_FLOAT, false, stride, 6L * Float.BYTES);
		GL20.glEnableVertexAttribArray(2);
		
		GL30.glBindVertexArray(0);
	}
	
	public void draw() {
		GL30.glBindVertexArray(vao);
		GL30.glDrawElements(GL20.GL_TRIANGLES, indexCount, GL20.GL_UNSIGNED_INT, 0L);
		GL30.glBindVertexArray(0);
	}
	
	@Override public void close() {
		GL30.glDeleteVertexArrays(vao);
		GL30.glDeleteBuffers(vbo);
		GL30.glDeleteBuffers(ebo);
	}
		

}
