package grainGrowth.model.generator.inclusions;

import grainGrowth.model.core.Coords;
import grainGrowth.model.core.Space;
import grainGrowth.model.generator.GeneratorUtils;

import java.util.List;
import java.util.Random;
import java.util.Set;


public abstract class InclusionsGenerator {

    public static InclusionType TYPE = InclusionType.CIRCULAR;
    protected int size;
    protected Space space;
    private int number;
    protected List<Coords> availableCellCords;


    protected InclusionsGenerator(int number, int size, Space space) {
        this.number = number;
        this.size = size;
        this.space = space;
    }


    public static InclusionsGenerator createInstance(int number, int size, Space space) {
        if (TYPE == InclusionType.SQUARED) {
            return new SquaredInclusionsGenerator(number, size, space);
        } else {
            return new CircularInclusionsGenerator(number, size, space);
        }
    }


    public void putInclusionsRandomly() {
        availableCellCords = GeneratorUtils.determineFreeCellCords(space);
        if (availableCellCords.isEmpty()) {
            availableCellCords = GeneratorUtils.determineGrainBoundaryCellsCoords(space);
        }
        putInclusionsRandomlyInAvailableCells();
    }


    private void putInclusionsRandomlyInAvailableCells() {
        int availableCellCordsSize = availableCellCords.size();
        Random random = new Random();

        for (int i = 0; i < number && availableCellCordsSize != 0; i++, availableCellCordsSize--) {
            Coords randomizedCellCoords = availableCellCords.remove(random.nextInt(availableCellCordsSize));

            placeInclusions(randomizedCellCoords);
        }
    }


    public void placeInclusions(Coords cellCoords) {
        Set<Coords> coordsSet = determineCoordsSet(cellCoords);

        for (Coords c : coordsSet) {
            space.getCell(c).setId(-1);
            space.getCell(c).setGrowable(false);
        }
    }


    protected abstract Set<Coords> determineCoordsSet(Coords randomizedCoords);

}
