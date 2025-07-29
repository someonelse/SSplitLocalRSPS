package server.world;

public class WorldObject {
    public int x;
    public int y;
    public int height;
    public int id;
    public int type;
    public int face;

    public WorldObject() {
        // All fields initialized to zero by default.
    }

    public WorldObject(int x, int y, int height, int id, int type, int face) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.id = id;
        this.type = type;
        this.face = face;
    }
}
