package grainGrowth.model.simulation;

import grainGrowth.model.core.Cell;
import grainGrowth.model.core.Coords;
import grainGrowth.model.core.Space;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SimpleGrainGrowth extends GrainGrowth {

    protected boolean changed;
    private Space nextIterationSpace;


    public SimpleGrainGrowth(Space space) {
        super(space);
        nextIterationSpace = new Space(space);
    }


    @Override
    public void simulateGrainGrowth() {
        space.resetBorderProperty();
        changed = true;
        while (changed) {
            performIteration();
            updateProgress(countProgress());
        }
        space.determineBorderCells();
    }


    private void performIteration() {
        changed = false;
        space.getCellsByCoords().entrySet().stream()
                .filter(coordsCellEntry -> coordsCellEntry.getValue().getId() == 0)
                .forEach(coordsCellEntry -> performGrowthIfPossible(coordsCellEntry.getKey()));
        updateSpace();
    }


    protected void performGrowthIfPossible(Coords coords) {
        List<Cell> neighbours = space.findNeighbours(coords);
        int newId = getMostFrequentId(neighbours);
        setNewIdIfDifferentThanZero(coords, newId);
    }


    private int getMostFrequentId(List<Cell> neighbours) {
        return determineMostFrequentId(designateAmountByGrainId(neighbours));
    }


    private Map<Integer, Integer> designateAmountByGrainId(List<Cell> neighbours) {
        Map<Integer, Integer> amountByGrainId = new HashMap<>();

        for (Cell cell : neighbours) {
            int id = cell.getId();
            if (id == 0 || !cell.isGrowable()) {
                continue;
            }
            if (amountByGrainId.containsKey(id)) {
                int amount = amountByGrainId.get(id) + 1;
                amountByGrainId.put(id, amount);
            } else {
                amountByGrainId.put(id, 1);
            }
        }

        return amountByGrainId;
    }


    private int determineMostFrequentId(Map<Integer, Integer> amountByGrainId) {
        return amountByGrainId.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseGet(() -> new AbstractMap.SimpleEntry<>(0, 0))
                .getKey();
    }


    protected boolean setNewIdIfDifferentThanZero(Coords coords, int newId) {
        if (newId != 0) {
            nextIterationSpace.getCell(coords).setId(newId);
            changed = true;
            return true;
        }
        return false;
    }


    private double countProgress() {
        int filledCellsNumber = (int) space.getCellsByCoords().values().stream()
                .filter(cell -> cell.getId() != 0)
                .count();

        return filledCellsNumber / (double) (space.getSizeX() * space.getSizeY());
    }


    private void updateSpace() {
        space.getCellsByCoords().forEach((coords, cell) -> cell.setId(nextIterationSpace.getCell(coords).getId()));
    }

}
