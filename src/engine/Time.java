package engine;

public class Time {
	private static double now, last, dt;
	private static double unscaledNow;
	
	public static void beginFrame() {
		now = glfwTime();
		dt = now - last;
		last = now;
		unscaledNow = now;
	}
	
	private static double glfwTime() {
		return org.lwjgl.glfw.GLFW.glfwGetTime();
	}
	
	public static double delta() {return dt;}
	public static double time() {return unscaledNow;}
	
	

}
