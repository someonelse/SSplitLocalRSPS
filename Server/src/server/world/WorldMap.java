package server.world;

import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class WorldMap {

    // Use ConcurrentHashMap for better concurrency (can revert to Hashtable if you need exact legacy behavior)
    public static final Map<Integer, GameObject> gameObjects = new ConcurrentHashMap<>();

    public static boolean solidObjectExists(int x, int y) {
        var go = gameObjects.get(y + (x << 16));
        if (go == null) return false;
        int type = go.type();
        if ((type == 2 || type == 4 || type == 7 || type == 8 || type == 9 || type == 10) &&
            go.x() == x && go.y() == y) {
            return true;
        }
        return false;
    }

    public static boolean canMove(int baseX, int baseY, int toX, int toY) {
        int diffX = baseX - toX;
        int diffY = baseY - toY;
        int moveX = Integer.compare(toX, baseX); // -1, 0, or 1
        int moveY = Integer.compare(toY, baseY); // -1, 0, or 1

        // Diagonal movement
        if (moveX != 0 && moveY != 0) {
            if (solidObjectExists(baseX + moveX, baseY + moveY) ||
                solidObjectExists(baseX + moveX, baseY) ||
                solidObjectExists(baseX, baseY + moveY)) {
                return false;
            }
        } else if (moveX != 0) {
            if (solidObjectExists(baseX + moveX, baseY)) {
                return false;
            }
        } else if (moveY != 0) {
            if (solidObjectExists(baseX, baseY + moveY)) {
                return false;
            }
        }
        return true;
    }

    public static void loadWorldMap() {
        try (var in = new RandomAccessFile("./Data/worldmap.bin", "r")) {
            var cache = new byte[(int) in.length()];
            in.readFully(cache);
            int ptr = 0;
            long a = System.currentTimeMillis();
            for (int i = 0; i < 1280618; i++) {
                int objectId = ((cache[ptr++] & 0xFF) << 8) | (cache[ptr++] & 0xFF);
                int objectX = ((cache[ptr++] & 0xFF) << 8) | (cache[ptr++] & 0xFF);
                int objectY = ((cache[ptr++] & 0xFF) << 8) | (cache[ptr++] & 0xFF);
                int objectHeight = cache[ptr++] & 0xFF;
                int objectType = cache[ptr++] & 0xFF;
                int objectFace = cache[ptr++] & 0xFF;
                var go = new GameObject(objectId, objectType, objectX, objectY, objectFace);
                if (go.type() != 0) {
                    gameObjects.put(go.y() + (go.x() << 16), go);
                }
            }
            long took = System.currentTimeMillis() - a;
            System.out.println("Loaded " + gameObjects.size() + " clips.");
            System.out.println("Loaded WorldMap In (" + took + " ms)... ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final Map<Integer, String> map = new HashMap<>();
}
