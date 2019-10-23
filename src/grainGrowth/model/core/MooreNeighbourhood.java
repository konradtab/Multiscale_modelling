package grainGrowth.model.core;

import java.util.LinkedList;
import java.util.List;


public class MooreNeighbourhood {

    private AbsorbentBoundaryCondition boundaryCondition;


    public MooreNeighbourhood(AbsorbentBoundaryCondition boundaryCondition) {
        this.boundaryCondition = boundaryCondition;
    }


    public List<Coords> findNeighboursCoords(Coords coords) {
        int x = coords.getX();
        int y = coords.getY();

        List<Coords> neighboursCoords = new LinkedList<>();

        neighboursCoords.add(new Coords(x - 1, y - 1));
        neighboursCoords.add(new Coords(x, y - 1));
        neighboursCoords.add(new Coords(x + 1, y - 1));
        neighboursCoords.add(new Coords(x - 1, y));
        neighboursCoords.add(new Coords(x + 1, y));
        neighboursCoords.add(new Coords(x - 1, y + 1));
        neighboursCoords.add(new Coords(x, y + 1));
        neighboursCoords.add(new Coords(x + 1, y + 1));

        for (Coords c : neighboursCoords) {
            boundaryCondition.correctCoordsIfNeeded(c);
        }

        return neighboursCoords;
    }

}
