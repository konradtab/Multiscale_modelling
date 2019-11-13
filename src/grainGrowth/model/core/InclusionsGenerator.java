package grainGrowth.model.core;

import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class InclusionsGenerator {

    public static InclusionType TYPE = InclusionType.Circular;
    protected int size;
    protected Space space;
    private int number;
    private List<Coords> availableCellCords;


    protected InclusionsGenerator(int number, int size, Space space, List<Coords> availableCellCords) {
        this.number = number;
        this.size = size;
        this.space = space;
        this.availableCellCords = availableCellCords;
    }

    public static InclusionsGenerator createInclusionsGenerator(int number, int size, Space space, List<Coords> availableCellCords) {
        switch (TYPE) {
            case Circular:
                return new CircularInclusionsGenerator(number, size, space, availableCellCords);
            case Square:
                return new SquaredInclusionsGenerator(number, size, space, availableCellCords);
        }
        return null;
    }

    public void putInclusionsInAvailableCells() {
        int availableCellCordsSize = availableCellCords.size();
        Random random = new Random();

        for (int i = 0; i < number && availableCellCordsSize != 0; i++, availableCellCordsSize--) {
            Coords randomizedCoords = availableCellCords.remove(random.nextInt(availableCellCordsSize));

            Set<Coords> coordsSet = determineCoordsSet(randomizedCoords);

            for (Coords c : coordsSet) {
                space.getCell(c).setId(-1);
            }

        }
    }


    protected abstract Set<Coords> determineCoordsSet(Coords randomizedCoords);

}
