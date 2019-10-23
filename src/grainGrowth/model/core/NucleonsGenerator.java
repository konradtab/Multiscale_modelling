package grainGrowth.model.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class NucleonsGenerator {

    public static void putNucleonsRandomly(int number, Space space) {
        List<Coords> freeCellCoords = determineFreeCellCords(space);

        int freeCellCoordsSize = freeCellCoords.size();
        int firstIdToPut = space.getMaxCellId() + 1;

        Random random = new Random();

        for (int i = 0; i < number; i++) {
            if (freeCellCoordsSize == 0) {
                break;
            }

            Coords randomizedCoords = freeCellCoords.remove(random.nextInt(freeCellCoordsSize));
            space.getCell(randomizedCoords).setId(firstIdToPut);

            firstIdToPut++;
            freeCellCoordsSize--;
        }
    }


    private static List<Coords> determineFreeCellCords(Space space) {
        List<Coords> freeCellCoords = new ArrayList<>();
        for (int i = 0; i < space.getSizeY(); i++) {
            for (int j = 0; j < space.getSizeX(); j++) {
                if (space.getCells()[i][j].getId() == 0) {
                    freeCellCoords.add(new Coords(j, i));
                }
            }
        }
        return freeCellCoords;
    }

}
