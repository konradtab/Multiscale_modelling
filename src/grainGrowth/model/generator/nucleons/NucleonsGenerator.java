package grainGrowth.model.generator.nucleons;

import grainGrowth.model.core.Coords;
import grainGrowth.model.core.Space;
import grainGrowth.model.generator.GeneratorUtils;

import java.util.List;
import java.util.Random;

public class NucleonsGenerator {

    public static void putNucleonsRandomly(int number, Space space) {
            List<Coords> freeCellCoords = GeneratorUtils.determineFreeCellCords(space);

        int freeCellCoordsSize = freeCellCoords.size();
        int firstIdToPut = space.findMaxCellId() + 1;

        Random random = new Random();

        for (int i = 0; i < number && freeCellCoordsSize != 0; i++, freeCellCoordsSize--) {
            Coords randomizedCoords = freeCellCoords.remove(random.nextInt(freeCellCoordsSize));
            space.getCell(randomizedCoords).setId(firstIdToPut);

            firstIdToPut++;
        }
    }


    public static void fillSpaceWithNumberOfUniqueIds(int numberOfUniqueIds, Space space) {
        Random random = new Random();
        List<Coords> freeCellCoords = GeneratorUtils.determineFreeCellCords(space);
        int firstIdToPut = space.findMaxCellId() + 1;

        for (Coords coords : freeCellCoords) {
            space.getCell(coords).setId(random.nextInt(numberOfUniqueIds) + firstIdToPut);
        }
    }

}
