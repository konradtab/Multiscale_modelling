package grainGrowth.model.core;


public class AbsorbentBoundaryCondition {

    private int sizeX;
    private int sizeY;


    public AbsorbentBoundaryCondition(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }


    void correctCoordsIfNeeded(Coords coords) {
        if (coords.getX() < 0) {
            coords.setX(0);
        } else if (coords.getX() >= sizeX) {
            coords.setX(sizeX - 1);
        }

        if (coords.getY() < 0) {
            coords.setY(0);
        } else if (coords.getY() >= sizeY) {
            coords.setY(sizeY - 1);
        }
    }

}
