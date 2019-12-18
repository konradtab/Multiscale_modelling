package grainGrowth.model.core;

public class Coords {
    private int x;
    private int y;

    private Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Coords coords(int x, int y) {
        return new Coords(x, y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Coords coords = (Coords) obj;
        return this.x == coords.x && this.y == coords.y;
    }

    @Override
    public int hashCode() {
        int tmp = (y + ((x + 1) / 2));
        return x + (tmp * tmp);
    }


    @Override
    public String toString() {
        return "Coords{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
