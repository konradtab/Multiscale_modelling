package grainGrowth.model.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SquaredInclusionsGenerator extends InclusionsGenerator {


    protected SquaredInclusionsGenerator(int number, int size, Space space, List<Coords> availableCellCords) {
        super(number, size, space, availableCellCords);
    }


    @Override
    protected Set<Coords> determineCoordsSet(Coords randomizedCoords) {
        Set<Coords> coordsSet = new HashSet<>();

        int modifier = size - 1;

        for (int y = randomizedCoords.getY() - modifier; y < randomizedCoords.getY() + modifier; y++) {
            for (int x = randomizedCoords.getX() - modifier; x < randomizedCoords.getX() + modifier; x++) {
                Coords coords = new Coords(x, y);
                space.getBoundaryCondition().correctCoordsIfNeeded(coords);
                coordsSet.add(coords);
            }
        }

        return coordsSet;
    }

}
