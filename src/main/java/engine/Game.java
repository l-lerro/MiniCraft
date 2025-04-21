package engine;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.opengl.GL;
import player.Player;
import world.World;

import java.nio.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Game implements Runnable {

    private long window;

    private int width = 800;
    private int height = 600;

    private World world;
    private Player player;
    private Camera camera;

    private float moveSpeed = 0.1f;

    private float lastX = width / 2.0f;
    private float lastY = height / 2.0f;
    private boolean firstMouse = true;

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run(){
        init();
        loop();

        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init(){

        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);

        window = glfwCreateWindow(width, height, "Minicraft", NULL, NULL);
        if(window == NULL){
            throw new RuntimeException("Failed to create GLFW Window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float fov = 70f;
        float aspect = (float)width/height;
        float near = 0.1f, far = 1000f;
        float y_scale = (float)(1f/Math.tan(Math.toRadians(fov/2)));
        float x_scale = y_scale / aspect;
        float frustum_length = far - near;
        float[] proj = new float[16];
        proj[0]  = x_scale;
        proj[5]  = y_scale;
        proj[10] = -((far+near)/frustum_length);
        proj[11] = -1f;
        proj[14] = -((2*near*far)/frustum_length);
        proj[15] = 0f;
        glLoadMatrixf(proj);

        glMatrixMode(GL_MODELVIEW);

        world = new World();
        player = new Player(0, 0, 0);
        camera = new Camera();

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            if (firstMouse) {
                lastX = (float)xpos;
                lastY = (float)ypos;
                firstMouse = false;
            }

            float xoffset = (float)(xpos - lastX);
            float yoffset = (float)(ypos - lastY);

            lastX = (float)xpos;
            lastY = (float)ypos;

            float sensitivity = 0.1f;
            xoffset *= sensitivity;
            yoffset *= sensitivity;

            camera.yaw   += xoffset;
            camera.pitch += yoffset;                // NOTA: sommi yoffset, non sottrai

            // clamp per evitare di guardare “capovolto”
            camera.pitch = Math.max(-89f, Math.min(89f, camera.pitch));
        });

    }

    private void loop(){
        while(!glfwWindowShouldClose(window)){
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glClearColor(0.2f, 0.4f, 0.6f, 1.0f);

            handleInput();

            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
            camera.applyView();

            world.render();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void handleInput(){
        // Calcola il vettore di forward dalla yaw
        Vector3f forward = new Vector3f(
                (float)Math.sin(Math.toRadians(camera.yaw)),
                0,
                (float)-Math.cos(Math.toRadians(camera.yaw))
        ).normalize();

        // Calcola il vettore right come prodotto vettoriale forward × up
        Vector3f up = new Vector3f(0,1,0);
        Vector3f right = new Vector3f();
        forward.cross(up, right).normalize();

        // Movimento
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.position.add(new Vector3f(forward).mul(moveSpeed));
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.position.sub(new Vector3f(forward).mul(moveSpeed));
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.position.add(new Vector3f(right).mul(moveSpeed));
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.position.sub(new Vector3f(right).mul(moveSpeed));
        }

        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            camera.position.y += moveSpeed;
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            camera.position.y -= moveSpeed;
        }
    }

    private void moveInDirection(float angleOffset) {
        float angle = (float) Math.toRadians(camera.yaw + angleOffset);
        camera.position.x += Math.sin(angle) * moveSpeed;
        camera.position.z += -Math.cos(angle) * moveSpeed;
    }
}
