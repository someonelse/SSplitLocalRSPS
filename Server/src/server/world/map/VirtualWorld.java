package server.world.map;

import java.util.Hashtable;
import java.util.Map;

public class VirtualWorld {

    // Use generics for typesafety; Object for legacy compatibility.
    public static final Map<Object, Object>[] maps = new Hashtable[10331];

    public VirtualWorld() {}

    public static void main(String[] args) {
        init();
    }

    public static void init() {
        for (int i = 0; i < maps.length; i++) {
            maps[i] = new Hashtable<>();
        }

        server.world.map.I.I(true);
        for (int j = 0; j < 0x989680; j++) {
            I(0, 3222, 3242, 3223, 3243, 0);
        }
    }

    public static boolean I(int height, int currentX, int currentY, int futureX, int futureY, int a) {
        if (height % 4 == 0)
            height = 0;
        if (currentX != futureX && futureY != currentY) {
            return server.world.map.I.I(height, currentX, currentY, currentX, futureY, a)
                && server.world.map.I.I(height, currentX, currentY, futureX, currentY, a)
                && server.world.map.I.I(height, currentX, currentY, futureX, futureY, a);
        }
        return server.world.map.I.I(height, currentX, currentY, futureX, futureY, a);
    }
}
