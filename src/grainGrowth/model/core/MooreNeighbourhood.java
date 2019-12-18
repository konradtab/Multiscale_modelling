package grainGrowth.model.core;

import java.util.LinkedList;
import java.util.List;

import static grainGrowth.model.core.Coords.coords;


public class MooreNeighbourhood {

    private AbsorbentBoundaryCondition boundaryCondition;


    public MooreNeighbourhood(AbsorbentBoundaryCondition boundaryCondition) {
        this.boundaryCondition = boundaryCondition;
    }


    public List<Coords> findNeighboursCoords(Coords coords) {
        List<Coords> neighboursCoords = new LinkedList<>();
        addNearestNeighboursCoords(coords, neighboursCoords);
        addFurtherNeighboursCoords(coords, neighboursCoords);

        for (Coords c : neighboursCoords) {
            boundaryCondition.correctCoordsIfNeeded(c);
        }

        return neighboursCoords;
    }


    public List<Coords> findNearestNeighboursCoords(Coords coords) {
        List<Coords> neighboursCoords = new LinkedList<>();
        addNearestNeighboursCoords(coords, neighboursCoords);

        return neighboursCoords;
    }

    private void addNearestNeighboursCoords(Coords coords, List<Coords> neighboursCoords) {
        int x = coords.getX();
        int y = coords.getY();

        neighboursCoords.add(coords(x, y - 1));
        neighboursCoords.add(coords(x - 1, y));
        neighboursCoords.add(coords(x + 1, y));
        neighboursCoords.add(coords(x, y + 1));

        for (Coords c : neighboursCoords) {
            boundaryCondition.correctCoordsIfNeeded(c);
        }
    }


    public List<Coords> findFurtherNeighboursCoords(Coords coords) {
        List<Coords> neighboursCoords = new LinkedList<>();
        addFurtherNeighboursCoords(coords, neighboursCoords);

        return neighboursCoords;
    }


    private void addFurtherNeighboursCoords(Coords coords, List<Coords> neighboursCoords) {
        int x = coords.getX();
        int y = coords.getY();

        neighboursCoords.add(coords(x - 1, y - 1));
        neighboursCoords.add(coords(x + 1, y - 1));
        neighboursCoords.add(coords(x - 1, y + 1));
        neighboursCoords.add(coords(x + 1, y + 1));

        for (Coords c : neighboursCoords) {
            boundaryCondition.correctCoordsIfNeeded(c);
        }
    }

}
