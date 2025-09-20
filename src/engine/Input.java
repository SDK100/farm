package engine;

import org.lwjgl.glfw.GLFW;

public class Input {
	
	private static boolean[] keys = new boolean[512];
	private static boolean[] keysPrev = new boolean[512];
	
	private static boolean[] mouse = new boolean[8];
	private static boolean[] mousePrev = new boolean[8];
	
	private static double mouseX, mouseY, mouseDX, mouseDY, scrollDY;
	
	public static void attach(long window) {
		GLFW.glfwSetKeyCallback(window, (w, key, sc, action, mods) -> {
			if (key >= 0 && key < keys.length) {
				keys[key] = action != GLFW.GLFW_RELEASE;
			}
		});
		
		GLFW.glfwSetMouseButtonCallback(window, (w, btn, action, mods) -> {
			if (btn >= 0 && btn < mouse.length) {
				mouse[btn] = action != GLFW.GLFW_RELEASE;
			}
		});
		
		GLFW.glfwSetCursorPosCallback(window, (w, x, y) -> {
			mouseDX = x - mouseY;
			mouseDY = y - mouseY;
			mouseX = x; mouseY = y;
		});
		
		GLFW.glfwSetScrollCallback(window, (w, sx, sy) -> {
			scrollDY += sy;
		});
	}
	
	public static void newFrame() {
		System.arraycopy(keys, 0, keysPrev, 0, keys.length);
		System.arraycopy(mouse, 0, mousePrev, 0, mouse.length);
		mouseDX = 0; mouseDY = 0; scrollDY = 0;
	}
	
	//keyboard
	
	public static boolean key(int key) {return keys[key];}
	public static boolean keyDown(int key) {return keys[key] && !keysPrev[key];}
	public static boolean keyUp(int key) {return !keys[key] && keysPrev[key];}
	
	//mouse
	
	public static boolean mouse(int btn) {return mouse[btn];}
	public static boolean mouseDown(int btn) {return mouse[btn] && !mousePrev[btn];}
	public static boolean mouseUp(int btn) {return !mouse[btn] && mousePrev[btn];}
	
	
	public static double mouseX() {return mouseX;}
	public static double mouseY() {return mouseY;}
	public static double mouseDX() {return mouseDX;}
	public static double mouseDY() {return mouseDY;}
	public static double scrollDY() {return scrollDY;}

}
