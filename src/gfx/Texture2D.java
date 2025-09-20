package gfx;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Texture2D implements AutoCloseable {
	private final int id;
	private final int width, height;
	
	public Texture2D(String path, boolean srgb) {
		STBImage.stbi_set_flip_vertically_on_load(true);
		int tex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, tex);
		
		int w, h, comp;
		try (var stack = stackPush()){
			IntBuffer pw = stack.mallocInt(1);
			IntBuffer ph= stack.mallocInt(1);
			IntBuffer pc = stack.mallocInt(1);
			ByteBuffer data = STBImage.stbi_load(path, pw, ph, pc, 4);
			if (data == null) throw new RuntimeException("STB load failed: " + path + " -> " + STBImage.stbi_failure_reason());
			w = pw.get(0); h = ph.get(0); comp = 4;
			
			int internal = GL11.GL_RGBA8;
			glTexImage2D(GL_TEXTURE_2D, 0, internal, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
			STBImage.stbi_image_free(data);
		}
		
		//sampler defaults
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
		glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		this.id = tex;
		this.width = w;
		this.height = h;
	}
	
	public void bind(int unit) {
		GL30.glActiveTexture(GL20.GL_TEXTURE0 + unit);
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public static void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public int id() {return id;}
	public int width() {return width;}
	public int height() {return height;}
	
	@Override public void close() {GL11.glDeleteTextures(id);}

}
