package gfx;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUseProgram;


public class Shader implements AutoCloseable {
	private final int program;
	
	public Shader(String vertexPath, String fragmentPath) {
		String vs = readFile(vertexPath);
		String fs = readFile(fragmentPath);
		
		int vert = compile(GL_VERTEX_SHADER, vs, vertexPath);
		int frag = compile(GL_FRAGMENT_SHADER, fs, fragmentPath);
		
		program = glCreateProgram();
		glAttachShader(program, vert);
		glAttachShader(program, frag);
		glLinkProgram(program);
		
		int linked = glGetProgrami(program, GL_LINK_STATUS);
		if (linked == 0) {
			String log = glGetProgramInfoLog(program);
			throw new RuntimeException("Shader link error:\n" + log);
		}
		
		glDetachShader(program, vert);
		glDetachShader(program, frag);
		glDeleteShader(vert);
		glDeleteShader(frag);
	}
	
	private static int compile(int type, String src, String label) {
		int id = glCreateShader(type);
		glShaderSource(id, src);
		glCompileShader(id);
		int ok = glGetShaderi(id, GL_COMPILE_STATUS);
		if (ok == GL_FALSE) {
			String log = glGetShaderInfoLog(id);
			throw new RuntimeException("Shader compiler error (" + label + "):\n" + log);
		}
		return id;
	}
	
	private static String readFile(String path) {
		try {return Files.readString(Path.of(path));}
		catch (IOException e) {throw new RuntimeException("Failed to read:" + path, e);}
	}
	
	public void bind() {glUseProgram(program);}
	public static void unbind() {glUseProgram(0);}
	
	public int id() {return program;}
	
	public void setMat4(String name, org.joml.Matrix4f mat) {
		int loc = glGetUniformLocation(program, name);
		if (loc >= 0) {
			try (var stack = org.lwjgl.system.MemoryStack.stackPush()){
				glUniformMatrix4fv(loc,false, mat.get(stack.mallocFloat(16)));
			}
		}
		
	}
	
	public void setVec3(String name, org.joml.Vector3f v) {
		int loc = glGetUniformLocation(program, name);
		if (loc >= 0) glUniform3f(loc, v.x, v.y, v.z);
	}
	
	public void setFloat(String name, float v) {
		int loc = glGetUniformLocation(program, name);
		if (loc >= 0) glUniform1f(loc, v);
	}
	
	public void setInt(String name, int v) {
		int loc = glGetUniformLocation(program, name);
		if (loc >= 0) glUniform1i(loc, v);
	}
	
	@Override public void close() {
		glDeleteProgram(program);
	}
	

}
