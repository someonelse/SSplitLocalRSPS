package server.world.map;

import java.util.Objects;

public final class Z {

    private final int x;
    private final int y;
    private final int height; // was KKLI
    private final int s;      // typically level or subtype

    public Z(int futureX, int futureY, int height, int s) {
        this.x = futureX;
        this.y = futureY;
        this.height = height;
        this.s = s;
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, s, x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Z))
            return false;
        Z z = (Z) obj;
        return height == z.height && s == z.s && x == z.x && y == z.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getS() {
        return s;
    }
}
