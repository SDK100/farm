import engine.Application;
import engine.Window;
import gfx.Shader;
import scene.RenderSystem;
import scene.Scene;
import scene.SceneLoader;
import gfx.Camera;
import util.EntityPicker;
import world.Grid;
import gfx.MeshUtils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;




public class Main extends Application {
    private Shader shader;
    private Camera cam;
    private Scene scene;
    private RenderSystem renderer;
  //grid + ground
    private Grid grid;
    private gfx.MeshPNT ground;
    private gfx.Texture2D groundTex;
    private gfx.Material groundMat; 

    //tile highlight
    private gfx.MeshPNT tileQuad;
    private gfx.Shader flatShader;

    //picking state
    private final org.joml.Vector3f hoverPoint = new org.joml.Vector3f();
    private final org.joml.Vector3f tileCenter = new org.joml.Vector3f();
    private final org.joml.Matrix4f tileModel = new org.joml.Matrix4f();
    private final org.joml.Matrix4f invViewProj = new org.joml.Matrix4f();
    private final org.joml.Vector3f rayO = new org.joml.Vector3f();
    private final org.joml.Vector3f rayD = new org.joml.Vector3f();
    private int hoverI = Integer.MIN_VALUE, hoverJ = Integer.MIN_VALUE;
    
    //selection state
 // selection state
    private int selectedI = Integer.MIN_VALUE, selectedJ = Integer.MIN_VALUE;
    private final Matrix4f selectedModel = new Matrix4f();
    private boolean prevLMB = false;



    public Main(){ super(new Window(1280, 720, "Game engine", true)); }

    @Override protected void init() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        
        grid = new Grid(1.0f, -50f, -50f);                 
        ground = MeshUtils.makeGroundPlane(100f, 100f);
        groundTex = new gfx.Texture2D("res/textures/texture.png", true);
        groundMat = new gfx.Material(groundTex);     

        tileQuad = MeshUtils.makeUnitTileQuad();
        flatShader = new gfx.Shader("res/shaders/flat_color.vert", "res/shaders/flat_color.frag");

        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_BLEND);
        org.lwjgl.opengl.GL11.glBlendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);


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
        if (org.lwjgl.glfw.GLFW.glfwGetKey(window.handle(), GLFW.GLFW_KEY_UP)   == GLFW.GLFW_PRESS) cam.orbit(0, 0.01f, 0);
        if (org.lwjgl.glfw.GLFW.glfwGetKey(window.handle(), GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) cam.orbit(0,  -0.01f, 0);
        
        invViewProj.set(cam.projMatrix()).mul(cam.viewMatrix()).invert();

        // mouse → NDC
        double mx = engine.Input.mouseX();
        double my = engine.Input.mouseY();
        float nx = (float)((mx / window.width()) * 2.0 - 1.0);
        float ny = (float)(1.0 - (my / window.height()) * 2.0);

        // ray
        EntityPicker.rayFromNDC(nx, ny, invViewProj, rayO, rayD);

        // ray ∩ ground (y=0)
        boolean hit = EntityPicker.rayPlaneY(rayO, rayD, 0f, hoverPoint);
        if (hit) {
            int[] ij = new int[2];
            grid.worldToCell(hoverPoint, ij);
            hoverI = ij[0]; hoverJ = ij[1];

            grid.cellCenter(hoverI, hoverJ, tileCenter);
            tileModel.identity()
                     .translate(tileCenter.x, 0.002f, tileCenter.z) 
                     .scale(grid.cellSize(), 1f, grid.cellSize());
        } else {
            hoverI = hoverJ = Integer.MIN_VALUE;
        }
        
     // left-click to select tile
        boolean mouseDown = GLFW.glfwGetMouseButton(window.handle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (mouseDown && hoverI != Integer.MIN_VALUE && hoverJ != Integer.MIN_VALUE) {
            selectedI = hoverI;
            selectedJ = hoverJ;
            selectedModel.set(tileModel); // copy current hover transform
        }
        
        

        boolean lmb = GLFW.glfwGetMouseButton(window.handle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (lmb && !prevLMB && hoverI != Integer.MIN_VALUE) {
            selectedI = hoverI; selectedJ = hoverJ;
            selectedModel.set(tileModel); // copy current hover transform (includes the y-lift)
        }
        prevLMB = lmb;


    }

    @Override
    protected void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        boolean wasCull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        if (wasCull) GL11.glDisable(GL11.GL_CULL_FACE);

        // 1) draw ground
        shader.bind();
        shader.setMat4("uView", cam.viewMatrix());
        shader.setMat4("uProj", cam.projMatrix());
        Matrix4f groundModel = new Matrix4f().translate(0f, -0.01f, 0f);
        shader.setMat4("uModel", groundModel);
        shader.setVec3("uSunDir", new Vector3f(-0.3f, -1.0f, -0.4f));
        shader.setVec3("uSunColor", new Vector3f(1.0f, 0.97f, 0.92f));
        shader.setFloat("uAmbient", 0.35f);
        shader.setInt("uAlbedo", 0);
        groundTex.bind(0);
        shader.setVec3("uTint", new Vector3f(1,1,1));
        ground.draw();
        Shader.unbind();

        if (wasCull) GL11.glEnable(GL11.GL_CULL_FACE);

        // 2) draw scene
        renderer.render(
            scene,
            cam.viewMatrix(),
            cam.projMatrix(),
            new Vector3f(-0.3f, -1.0f, -0.4f),
            new Vector3f(1.0f, 0.97f, 0.92f),
            0.25f
        );

        // -------- Overlay state for tile highlights --------
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false); // don't write depth
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-1.0f, -1.0f); // pull forward

        // 3) hover highlight (blue)
        if (hoverI != Integer.MIN_VALUE) {
            flatShader.bind();
            flatShader.setMat4("uView", cam.viewMatrix());
            flatShader.setMat4("uProj", cam.projMatrix());
            flatShader.setMat4("uModel", tileModel); // be sure tileModel's Y-lift is ~0.01f
            flatShader.setVec3("uColor", new Vector3f(0.2f, 0.7f, 1.0f));
            flatShader.setFloat("uAlpha", 0.35f);
            tileQuad.draw();
            Shader.unbind();
        }

        // 4) selected highlight (orange) — draw AFTER hover so it wins visually
        if (selectedI != Integer.MIN_VALUE) {
            flatShader.bind();
            flatShader.setMat4("uView", cam.viewMatrix());
            flatShader.setMat4("uProj", cam.projMatrix());
            flatShader.setMat4("uModel", selectedModel);
            flatShader.setVec3("uColor", new Vector3f(1.0f, 0.5f, 0.0f));
            flatShader.setFloat("uAlpha", 0.55f);
            tileQuad.draw();
            Shader.unbind();
        }

        // -------- restore state --------
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDepthMask(true);
        if (wasCull) GL11.glEnable(GL11.GL_CULL_FACE);
    }



    @Override protected void shutdown() {
        try {
			scene.close();
			tileQuad.close();
	        ground.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        shader.close();
        
        groundTex.close();
        flatShader.close();

    }

    public static void main(String[] args) { new Main().run(); }
}
