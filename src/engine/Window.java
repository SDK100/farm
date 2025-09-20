package engine;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public final class Window {
	
	private long handle;
	private int width, height;
	private String title;
	private boolean vsync;
	
	public Window(int width, int height, String title, boolean vsync) {
		this.width = width;
		this.height = height;
		this.title = title;
		this.vsync = vsync;
	}
	
	public void create() {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!GLFW.glfwInit()) throw new IllegalStateException("GLFW init failed");
		
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_FALSE);
		
		handle = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		if (handle == 0L) throw new RuntimeException("Failed to create window");
		
		GLFW.glfwMakeContextCurrent(handle);
		GL.createCapabilities();
		
		setVsync(vsync);
		GLFW.glfwShowWindow(handle);
		
		//gl viewport
		GL11.glViewport(0, 0, width, height);
		GL11.glClearColor(0.08f, 0.09f, 0.11f, 1.0f);
		
		//resize callback
		GLFW.glfwSetFramebufferSizeCallback(handle, (win, w, h) -> {
			width = w; height = h;
			GL11.glViewport(0, 0, w, h);
		});
	}
	
	public void pollEvents() {GLFW.glfwPollEvents();}
	public void swapBuffers() {GLFW.glfwSwapBuffers(handle);}
	public boolean shouldClose() {return GLFW.glfwWindowShouldClose(handle);}
	public void requestClose() {GLFW.glfwSetWindowShouldClose(handle, true);}
	
	public void destroy() {
		GLFW.glfwDestroyWindow(handle);
		GLFW.glfwTerminate();
	}
	
	public long handle() {return handle;}
	public int width() {return width;}
	public int height() {return height;}
	
	public void setVsync(boolean on) {
		vsync = on;
		GLFW.glfwSwapInterval(vsync ? 1 : 0);
	}
	
	
	
}
