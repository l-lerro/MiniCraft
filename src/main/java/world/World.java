package world;

import org.joml.Vector3f;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class World {

    private List<Block> blocks = new ArrayList<>();


    public World(){
        generateFlatWorld(10, 1, 10);

    }

    private void generateFlatWorld(int width, int height, int depth){

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                for(int z = 0; z < depth; z++){
                    blocks.add(new Block(x, y, z));
                }
            }
        }

    }

    public void render(){
        for(Block block : blocks){
            block.render();
        }
    }


}