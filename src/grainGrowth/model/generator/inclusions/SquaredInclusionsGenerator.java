package grainGrowth.model.generator.inclusions;

import grainGrowth.model.core.Coords;
import grainGrowth.model.core.Space;

import java.util.HashSet;
import java.util.Set;


public class SquaredInclusionsGenerator extends InclusionsGenerator {


    protected SquaredInclusionsGenerator(int number, int size, Space space) {
        super(number, size, space);
    }


    @Override
    protected Set<Coords> determineCoordsSet(Coords randomizedCoords) {
        Set<Coords> coordsSet = new HashSet<>();

        if (size == 1) {
            coordsSet.add(Coords.coords(randomizedCoords.getX(), randomizedCoords.getY()));
            return coordsSet;
        }

        int modifier = size - 1;

        for (int y = randomizedCoords.getY() - modifier; y < randomizedCoords.getY() + modifier; y++) {
            for (int x = randomizedCoords.getX() - modifier; x < randomizedCoords.getX() + modifier; x++) {
                Coords coords = Coords.coords(x, y);
                space.getBoundaryCondition().correctCoordsIfNeeded(coords);
                coordsSet.add(coords);
            }
        }

        return coordsSet;
    }

}
