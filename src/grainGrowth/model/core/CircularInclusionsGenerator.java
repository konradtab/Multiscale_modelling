package grainGrowth.model.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CircularInclusionsGenerator extends InclusionsGenerator {


    protected CircularInclusionsGenerator(int number, int size, Space space, List<Coords> availableCellCords) {
        super(number, size, space, availableCellCords);
    }


    @Override
    protected Set<Coords> determineCoordsSet(Coords randomizedCoords) {
        Set<Coords> coordsSet = new HashSet<>();

        int modifier = size;

        for (int y = randomizedCoords.getY() - modifier; y <= randomizedCoords.getY() + modifier; y++) {
            for (int x = randomizedCoords.getX() - modifier; x <= randomizedCoords.getX() + modifier; x++) {
                Coords coords = new Coords(x, y);
                double distance = computeDistance(coords, randomizedCoords);
                if (distance < modifier) {
                    space.getBoundaryCondition().correctCoordsIfNeeded(coords);
                    coordsSet.add(coords);
                }
            }
        }

        return coordsSet;
    }


    private double computeDistance(Coords coords1, Coords coords2) {
        return Math.sqrt(Math.pow(coords1.getX() - coords2.getX(), 2) + Math.pow(coords1.getY() - coords2.getY(), 2));
    }

}
