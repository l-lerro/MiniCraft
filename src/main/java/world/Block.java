package world;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class Block {

    private Vector3f position;


    public Block(float x, float y, float z){
        this.position = new Vector3f(x, y, z);

    }

    public void render() {
        glPushMatrix();
        glTranslatef(position.x, position.y, position.z);
        drawCube();
        glPopMatrix();
    }

    private void drawCube(){
        glBegin(GL_QUADS);

        // Front Face (Z+)
        glColor3f(1.0f, 0.0f, 0.0f); // Rosso
        glVertex3f(-0.5f, -0.5f,  0.5f);
        glVertex3f( 0.5f, -0.5f,  0.5f);
        glVertex3f( 0.5f,  0.5f,  0.5f);
        glVertex3f(-0.5f,  0.5f,  0.5f);

        // Back Face (Z-)
        glColor3f(0.0f, 1.0f, 0.0f); // Verde
        glVertex3f(-0.5f, -0.5f, -0.5f);
        glVertex3f(-0.5f,  0.5f, -0.5f);
        glVertex3f( 0.5f,  0.5f, -0.5f);
        glVertex3f( 0.5f, -0.5f, -0.5f);

        // Top Face (Y+)
        glColor3f(0.0f, 0.0f, 1.0f); // Blu
        glVertex3f(-0.5f,  0.5f, -0.5f);
        glVertex3f(-0.5f,  0.5f,  0.5f);
        glVertex3f( 0.5f,  0.5f,  0.5f);
        glVertex3f( 0.5f,  0.5f, -0.5f);

        // Bottom Face (Y-)
        glColor3f(1.0f, 1.0f, 0.0f); // Giallo
        glVertex3f(-0.5f, -0.5f, -0.5f);
        glVertex3f( 0.5f, -0.5f, -0.5f);
        glVertex3f( 0.5f, -0.5f,  0.5f);
        glVertex3f(-0.5f, -0.5f,  0.5f);

        // Right Face (X+)
        glColor3f(1.0f, 0.0f, 1.0f); // Magenta
        glVertex3f( 0.5f, -0.5f, -0.5f);
        glVertex3f( 0.5f,  0.5f, -0.5f);
        glVertex3f( 0.5f,  0.5f,  0.5f);
        glVertex3f( 0.5f, -0.5f,  0.5f);

        // Left Face (X-)
        glColor3f(0.0f, 1.0f, 1.0f); // Ciano
        glVertex3f(-0.5f, -0.5f, -0.5f);
        glVertex3f(-0.5f, -0.5f,  0.5f);
        glVertex3f(-0.5f,  0.5f,  0.5f);
        glVertex3f(-0.5f,  0.5f, -0.5f);

        glEnd();
    }



}
