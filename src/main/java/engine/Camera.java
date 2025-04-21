package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class Camera {

    public Vector3f position;
    public float yaw = 0.0f;
    public float pitch = 0.0f;

    public Camera(){
        this.position = new Vector3f(0, 0, 0);
    }

    public void applyView(){
        Matrix4f view = new Matrix4f()
                .rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0))
                .translate(-position.x, -position.y, -position.z);

        float[] matrix = new float[16];
        view.get(matrix);

        glLoadMatrixf(matrix);
    }
}
