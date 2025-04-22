package world;

import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds and manages all voxel blocks that compose the game world.
 * <p>
 * Use an HashMap with lookup O(1)
 *  used in order to ray‑cast .
 * </p>
 */
public class World {

    /**
     * Map: Int Position → block
     */
    private final Map<Vector3i, Block> blocks = new HashMap<>();

    // ==== CONSTRUCTOR =======================================================
    public World() {
        generateFlatWorld(10, 1, 10);   // prototipo: plateau 10×1×10
    }

    // ==== WORLD GENERATION ==================================================
    /**
     * Generate a flat world.
     *
     */
    private void generateFlatWorld(int width, int height, int depth) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    addBlock(new Block(x, y, z));
                }
            }
        }
    }

    // ==== CRUD BLOCK  =======================================================
    public void addBlock(Block block) {
        Vector3i pos = new Vector3i((int) block.getPosition().x,
                (int) block.getPosition().y,
                (int) block.getPosition().z);
        blocks.put(pos, block);
    }

    public Block removeBlock(int x, int y, int z) {
        return blocks.remove(new Vector3i(x, y, z));
    }

    public Block getBlock(int x, int y, int z) {
        return blocks.get(new Vector3i(x, y, z));
    }

    public boolean hasBlock(int x, int y, int z) {
        return blocks.containsKey(new Vector3i(x, y, z));
    }

    // ==== RENDER ============================================================
    /**
     * Render a Block.
     * TODO: Passare a VAO/VBO e/o "chunk" per efficienza, ma per il prototipo il render immediato va bene.
     */
    public void render() {
        for (Block block : blocks.values()) {
            block.render();
        }
    }
}
