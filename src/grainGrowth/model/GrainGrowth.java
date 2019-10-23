package grainGrowth.model;

import grainGrowth.model.core.Cell;
import grainGrowth.model.core.Coords;
import grainGrowth.model.core.Space;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class GrainGrowth {

    private Space space;
    private Space nextIterationSpace;

    private boolean changed;


    public GrainGrowth(Space space) {
        this.space = space;
        nextIterationSpace = new Space(space);
    }


    public void simulateGrainGrowth() {
        changed = true;
        while (changed) {
            performIteration();
        }
    }


    public void performIteration() {
        changed = false;
        Coords coords = new Coords(0, 0);
        for (int i = 0; i < space.getSizeY(); i++) {
            coords.setY(i);
            for (int j = 0; j < space.getSizeX(); j++) {
                coords.setX(j);
                Cell oldCell = space.getCells()[i][j];

                if (oldCell.getId() == 0) {
                    List<Cell> neighbours = space.findNeighbours(coords);
                    int newId = getMostFrequentId(neighbours);
                    if (newId != 0) {
                        nextIterationSpace.getCells()[i][j].setId(newId);
                        changed = true;
                    }
                }
            }
        }
        updateSpace();
    }


    private int getMostFrequentId(List<Cell> neighbours) {
        HashMap<Integer, Integer> amountByGrainId = new HashMap<>();

        for (Cell cell : neighbours) {
            int id = cell.getId();
            if (id == 0) {
                continue;
            }
            if (amountByGrainId.containsKey(id)) {
                int amount = amountByGrainId.get(id) + 1;
                amountByGrainId.put(id, amount);
            } else {
                amountByGrainId.put(id, 1);
            }
        }

        AtomicInteger mostFrequentId = new AtomicInteger();
        AtomicInteger mostFrequentIdCounter = new AtomicInteger();
        amountByGrainId.forEach((key, value) -> {
            if (value > mostFrequentIdCounter.get()) {
                mostFrequentId.set(key);
                mostFrequentIdCounter.set(value);
            }
        });

        return mostFrequentId.get();
    }


    private void updateSpace() {
        for (int i = 0; i < space.getSizeY(); i++) {
            for (int j = 0; j < space.getSizeX(); j++) {
                Cell currentCell = space.getCells()[i][j];
                Cell nextIterationCell = nextIterationSpace.getCells()[i][j];
                currentCell.setId(nextIterationCell.getId());
            }
        }
    }


    public Space getSpace() {
        return space;
    }

}
