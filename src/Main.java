import engine.Application;
import engine.Window;
import gfx.Shader;
import gfx.Texture2D;
import scene.RenderSystem;
import scene.Scene;
import scene.SceneLoader;
import gfx.Camera;
import gfx.MeshPNT;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Main extends Application {
    private Shader shader;
    private Camera cam;
    private Scene scene;
    private RenderSystem renderer;
    private Shader litShader;
    private MeshPNT cubeMesh;
    private Texture2D crateTex;      // any 512x512 or whatever
    private final org.joml.Matrix4f tmpModel = new org.joml.Matrix4f();
    
    private final java.util.List<org.joml.Vector3f> placedCrates = new java.util.ArrayList<>();
    private int selI = Integer.MIN_VALUE, selJ = Integer.MIN_VALUE;
    private boolean prevMouseDown = false;

    public Main(){ super(new Window(1280, 720, "Farm Game", true)); }

    @Override protected void init() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        shader = new Shader("res/shaders/lit_textured.vert", "res/shaders/lit_textured.frag");
        renderer = new RenderSystem(shader);

        cam = new Camera();
        cam.setPrespective(60f, window.width()/(float)window.height(), 0.1f, 200f);

        scene = SceneLoader.load("res/scenes/test.scene.json");
    }

    @Override protected void update(double dtFixed) {
        // simple orbit with arrow keys for demo
        if (org.lwjgl.glfw.GLFW.glfwGetKey(window.handle(), GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) cam.orbit( 0.015f, 0, 0);
        if (org.lwjgl.glfw.GLFW.glfwGetKey(window.handle(), GLFW.GLFW_KEY_RIGHT)== GLFW.GLFW_PRESS) cam.orbit(-0.015f, 0, 0);
        if (org.lwjgl.glfw.GLFW.glfwGetKey(window.handle(), GLFW.GLFW_KEY_UP)   == GLFW.GLFW_PRESS) cam.orbit(0, -0.01f, 0);
        if (org.lwjgl.glfw.GLFW.glfwGetKey(window.handle(), GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) cam.orbit(0,  0.01f, 0);
    }

    @Override protected void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        renderer.render(
                scene,
                cam.viewMatrix(),
                cam.projMatrix(),
                new Vector3f(-0.3f, -1.0f, -0.4f),
                new Vector3f(1.0f, 0.97f, 0.92f),
                0.25f
        );
    }

    @Override protected void shutdown() {
        try {
			scene.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        shader.close();
    }

    public static void main(String[] args) { new Main().run(); }
}
