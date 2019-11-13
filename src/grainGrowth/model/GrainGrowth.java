package grainGrowth.model;

import grainGrowth.model.core.Cell;
import grainGrowth.model.core.Coords;
import grainGrowth.model.core.Space;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class GrainGrowth {

    protected Space space;
    protected Space nextIterationSpace;

    protected boolean changed;


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


    protected void performIteration() {
        changed = false;
        Coords coords = new Coords(0, 0);
        for (int i = 0; i < space.getSizeY(); i++) {
            coords.setY(i);
            for (int j = 0; j < space.getSizeX(); j++) {
                coords.setX(j);
                Cell oldCell = space.getCell(coords);

                if (oldCell.getId() == 0) {
                    performGrowthIfPossible(coords);
                }
            }
        }
        updateSpace();
    }

    protected void performGrowthIfPossible(Coords coords) {
        List<Cell> neighbours = space.findNeighbours(coords);
        int newId = getMostFrequentId(neighbours);
        setNewIdIfDifferentThanZero(coords, newId);
    }

    protected boolean setNewIdIfDifferentThanZero(Coords coords, int newId) {
        if (newId != 0) {
            nextIterationSpace.getCell(coords).setId(newId);
            changed = true;
            return true;
        }
        return false;
    }


    protected int getMostFrequentId(List<Cell> neighbours) {
        HashMap<Integer, Integer> amountByGrainId = new HashMap<>();

        for (Cell cell : neighbours) {
            int id = cell.getId();
            if (id == 0 || id == -1) {
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


    protected void updateSpace() {
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
