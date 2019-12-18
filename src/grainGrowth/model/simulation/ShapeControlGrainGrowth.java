package grainGrowth.model.simulation;

import grainGrowth.model.core.Cell;
import grainGrowth.model.core.Coords;
import grainGrowth.model.core.Space;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ShapeControlGrainGrowth extends SimpleGrainGrowth {

    private double probabilityForForthRule;


    public ShapeControlGrainGrowth(Space space, double probabilityForForthRule) {
        super(space);
        this.probabilityForForthRule = probabilityForForthRule;
    }


    @Override
    protected void performGrowthIfPossible(Coords coords) {
        List<Cell> neighbours = space.findNeighbours(coords);
        int newId = getIdOccurringEqualToAmount(neighbours, 5);
        boolean cellChanged = setNewIdIfDifferentThanZero(coords, newId);
        if (cellChanged) {
            return;
        }

        neighbours = findNeighboursForSecondRule(coords);
        newId = getIdOccurringEqualToAmount(neighbours, 3);
        cellChanged = setNewIdIfDifferentThanZero(coords, newId);
        if (cellChanged) {
            return;
        }

        neighbours = findNeighboursForThirdsRule(coords);
        newId = getIdOccurringEqualToAmount(neighbours, 3);
        cellChanged = setNewIdIfDifferentThanZero(coords, newId);
        if (cellChanged) {
            return;
        }

        if (Math.random() < probabilityForForthRule) {
            super.performGrowthIfPossible(coords);
        } else {
            changed = true;
        }
    }


    private List<Cell> findNeighboursForSecondRule(Coords coords) {
        return space.getNeighboursCells(space.getMooreNeighbourHood().findNearestNeighboursCoords(coords));
    }


    private List<Cell> findNeighboursForThirdsRule(Coords coords) {
        return space.getNeighboursCells(space.getMooreNeighbourHood().findFurtherNeighboursCoords(coords));
    }


    private int getIdOccurringEqualToAmount(List<Cell> neighbours, int neededAmount) {
        Map<Integer, Integer> amountByGrainId = new HashMap<>();

        for (Cell cell : neighbours) {
            int id = cell.getId();
            if (id == 0 || !cell.isGrowable()) {
                continue;
            }
            if (amountByGrainId.containsKey(id)) {
                int amount = amountByGrainId.get(id) + 1;
                if (amount == neededAmount) {
                    return id;
                }
                amountByGrainId.put(id, amount);
            } else {
                amountByGrainId.put(id, 1);
            }
        }

        return 0;
    }

}
