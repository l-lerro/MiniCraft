package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import player.Player;
import world.World;
import world.Block;   // nuovo: usiamo Block per l'evidenziazione

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Main game loop class.
 * Manage Game Initialization, Input, Logics and Rendering
 * OPENGL IMMEDIATE MODE
 *
 */
public class Game implements Runnable {

    // -------- Window & timing ------------------------------------------------
    private final int width  = 800;
    private final int height = 600;
    private long   window;                  // GLFW window handle
    private double lastFrameTime = 0.0;     // seconds
    private float  deltaTime     = 0.0f;    // seconds between frames

    // -------- Scene ----------------------------------------------------------
    private World  world;
    private Player player;
    private Camera camera;

    // -------- Movement -------------------------------------------------------
    private static final float BASE_MOVE_SPEED = 5.0f;  // blocks per second

    // -------- Mouse input ----------------------------------------------------
    private float lastX = width  / 2.0f;
    private float lastY = height / 2.0f;
    private boolean firstMouse = true;

    // -------- Interactions ---------------------------------------------------
    private static final int RAY_RANGE = 8;     // distanza massima d'interazione (blocchi)
    private Block highlighted = null;           // blocco attualmente sotto al cross‑hair

    // ========================================================================
    /** Run Game Loop on current Thread. */
    public void start() { run(); }

    @Override public void run() { init(); loop(); destroy(); }

    // ====== INITIALISATION ===================================================
    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // --- Window and Context ---
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        window = glfwCreateWindow(width, height, "Minicraft", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create GLFW window");

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);                       // V‑Sync
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        GL.createCapabilities();                   // dopo il contesto!!!!

        // --- OPEN GL STATUS ---
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE); glCullFace(GL_BACK);

        // --- PROJECTION (JOML) ---
        Matrix4f projection = new Matrix4f()
                .perspective((float) Math.toRadians(70), (float) width / height, 0.1f, 1000f);
        glMatrixMode(GL_PROJECTION);
        glLoadMatrixf(projection.get(new float[16]));
        glMatrixMode(GL_MODELVIEW);

        // --- ENTITIES---
        world  = new World();
        player = new Player(0, 0, 0);
        camera = new Camera();
        camera.position.set(0, 2, 0);             // spawn sopra il terreno

        // --- Mouse callback ---
        glfwSetCursorPosCallback(window, (w, xpos, ypos) -> {
            if (firstMouse) { lastX = (float) xpos; lastY = (float) ypos; firstMouse = false; }
            float xoffset = (float) (xpos - lastX) * 0.1f;   // sensitivity 0.1
            float yoffset = (float) (ypos - lastY) * 0.1f;
            lastX = (float) xpos; lastY = (float) ypos;

            camera.yaw   += xoffset;
            camera.pitch += yoffset;              // Y positivo = guarda in basso (tuo setup)
            camera.pitch = Math.max(-89f, Math.min(89f, camera.pitch));
        });

        lastFrameTime = glfwGetTime();
    }

    // ====== MAIN LOOP ========================================================
    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            updateDeltaTime();
            handleInput();
            highlighted = rayCast();     // Find looked Block
            render();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void updateDeltaTime() {
        double now = glfwGetTime();
        deltaTime = (float) (now - lastFrameTime);
        lastFrameTime = now;
    }

    // ====== PER‑FRAME INPUT ==================================================
    private void handleInput() {
        // Base Vectors
        Vector3f forward = new Vector3f((float) Math.sin(Math.toRadians(camera.yaw)), 0,
                (float) -Math.cos(Math.toRadians(camera.yaw))).normalize();
        Vector3f right   = forward.cross(new Vector3f(0,1,0), new Vector3f()).normalize();
        float speed = BASE_MOVE_SPEED * deltaTime;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)  camera.position.fma(speed,  forward);
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)  camera.position.fma(-speed, forward);
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)  camera.position.fma(speed,  right);
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)  camera.position.fma(-speed, right);
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS)      camera.position.y += speed;
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) camera.position.y -= speed;
    }

    // ====== RENDER ===========================================================
    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.2f, 0.4f, 0.6f, 1.0f);

        glMatrixMode(GL_MODELVIEW); glLoadIdentity();
        camera.applyView();

        world.render();
        if (highlighted != null) drawHighlight(highlighted);
    }

    /** Draw Yellow Wireframe on looked Block */
    private void drawHighlight(Block b) {
        Vector3f pos = b.getPosition();
        glDisable(GL_CULL_FACE);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glLineWidth(3f);
        glColor3f(1f, 1f, 0f);

        glPushMatrix();
        glTranslatef(pos.x, pos.y, pos.z);
        drawUnitCubeOutline();
        glPopMatrix();

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_CULL_FACE);
    }

    /** Draw spigoli of a unit Cube centered in its origin. */
    private void drawUnitCubeOutline() {
        float h = 0.5f;
        glBegin(GL_LINES);
        // bottom square
        glVertex3f(-h,-h,-h); glVertex3f( h,-h,-h);
        glVertex3f( h,-h,-h); glVertex3f( h,-h, h);
        glVertex3f( h,-h, h); glVertex3f(-h,-h, h);
        glVertex3f(-h,-h, h); glVertex3f(-h,-h,-h);
        // top square
        glVertex3f(-h, h,-h); glVertex3f( h, h,-h);
        glVertex3f( h, h,-h); glVertex3f( h, h, h);
        glVertex3f( h, h, h); glVertex3f(-h, h, h);
        glVertex3f(-h, h, h); glVertex3f(-h, h,-h);
        // verticals
        glVertex3f(-h,-h,-h); glVertex3f(-h, h,-h);
        glVertex3f( h,-h,-h); glVertex3f( h, h,-h);
        glVertex3f( h,-h, h); glVertex3f( h, h, h);
        glVertex3f(-h,-h, h); glVertex3f(-h, h, h);
        glEnd();
    }

    // ====== RAY CAST =========================================================
    private Block rayCast() {
        Vector3f origin = new Vector3f(camera.position);
        Vector3f dir = new Vector3f(
                (float) Math.sin(Math.toRadians(camera.yaw)) * (float) Math.cos(Math.toRadians(camera.pitch)),
                (float) -Math.sin(Math.toRadians(camera.pitch)),
                (float) -Math.cos(Math.toRadians(camera.yaw)) * (float) Math.cos(Math.toRadians(camera.pitch))
        ).normalize();

        int x = (int) Math.floor(origin.x);
        int y = (int) Math.floor(origin.y);
        int z = (int) Math.floor(origin.z);

        int stepX = dir.x > 0 ? 1 : -1;
        int stepY = dir.y > 0 ? 1 : -1;
        int stepZ = dir.z > 0 ? 1 : -1;

        float tMaxX = intBound(origin.x, dir.x);
        float tMaxY = intBound(origin.y, dir.y);
        float tMaxZ = intBound(origin.z, dir.z);

        float tDeltaX = dir.x != 0 ? Math.abs(1f / dir.x) : Float.POSITIVE_INFINITY;
        float tDeltaY = dir.y != 0 ? Math.abs(1f / dir.y) : Float.POSITIVE_INFINITY;
        float tDeltaZ = dir.z != 0 ? Math.abs(1f / dir.z) : Float.POSITIVE_INFINITY;

        float dist = 0f;
        while (dist <= RAY_RANGE) {
            Block b = world.getBlock(x, y, z);
            if (b != null) return b;

            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    x += stepX; dist = tMaxX; tMaxX += tDeltaX;
                } else {
                    z += stepZ; dist = tMaxZ; tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    y += stepY; dist = tMaxY; tMaxY += tDeltaY;
                } else {
                    z += stepZ; dist = tMaxZ; tMaxZ += tDeltaZ;
                }
            }
        }
        return null;
    }

    /** Lenght from cube origin to its face over 'ds'. */
    private static float intBound(float s, float ds) {
        if (ds > 0)      return (float) ((Math.floor(s + 1) - s) / ds);
        else if (ds < 0) return (float) ((s - Math.floor(s)) / -ds);
        else             return Float.POSITIVE_INFINITY;
    }

    // ====== CLEAN‑UP =========================================================
    private void destroy() {
        glfwDestroyWindow(window);
        glfwTerminate();
        GLFWErrorCallback cb = glfwSetErrorCallback(null);
        if (cb != null) cb.free();
    }
}