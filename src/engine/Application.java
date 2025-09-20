package engine;

import org.lwjgl.glfw.GLFW;

public abstract class Application {

	protected final Window window;
	
	private static double FIXED_DT = 1.0 / 280.0;
	
	protected Application(Window window) {
		this.window = window;
	}
	
	public final void run() {
		window.create();
		Input.attach(window.handle());
		
		init();
		double accumulator = 0.0;
		double last = GLFW.glfwGetTime();
		
		while(!window.shouldClose()) {
			double now = GLFW.glfwGetTime();
			double frameTime = now - last;
			last = now;
			
			Time.beginFrame();
			Input.newFrame();
			window.pollEvents();
			
			
			//fixed update loop
			accumulator += frameTime;
			while (accumulator >= FIXED_DT) {
				update(FIXED_DT);
				accumulator -= FIXED_DT;
			}
			
			render();
			window.swapBuffers();
			
			//close on esc
			if (Input.keyDown(GLFW.GLFW_KEY_ESCAPE)) window.requestClose();
		}
		
		shutdown();
		window.destroy();
	}
	
	//lifecycle
	protected abstract void init();
	protected abstract void update(double dtFixed);
	protected abstract void render();
	protected abstract void shutdown();
}
