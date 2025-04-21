package player;

import org.joml.Vector3f;

public class Player {



    private Vector3f position;

    public Player(float x, float y, float z){
        position = new Vector3f(x, y, x);

    }

    public void move(float dx, float dy, float dz){
        position.add(dx, dy, dz);
    }

    // Getter
    public Vector3f getPosition() {
        return position;
    }

}
