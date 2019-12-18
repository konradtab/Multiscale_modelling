package grainGrowth.model.simulation;

import grainGrowth.model.core.Cell;
import grainGrowth.model.core.Coords;
import grainGrowth.model.core.Space;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;


public class MonteCarloGrainGrowth extends GrainGrowth {

    private int numberOfIterations;
    private double gbEnergy;


    public MonteCarloGrainGrowth(Space space, int numberOfIterations, double gbEnergy) {
        super(space);
        this.numberOfIterations = numberOfIterations;
        this.gbEnergy = gbEnergy;
    }


    @Override
    public void simulateGrainGrowth() {
        space.resetBorderProperty();
        for (int i = 0; i < numberOfIterations; i++) {
            performIteration();
        }
        space.determineBorderCells();
    }


    private void performIteration() {
        List<Map.Entry<Coords, Cell>> cellByCoordsEntryList = space.getCellsByCoords().entrySet()
                .stream()
                .filter(coordsCellEntry -> coordsCellEntry.getValue().isGrowable())
                .collect(Collectors.toList());

        Collections.shuffle(cellByCoordsEntryList);

        cellByCoordsEntryList.forEach(randomizedCellByCoords -> {
            List<Cell> neighbours = space.findNeighbours(randomizedCellByCoords.getKey())
                    .stream()
                    .filter(Cell::isGrowable)
                    .collect(Collectors.toList());
            if (neighbours.size() > 0) {
                performMonteCarloGrowth(neighbours, randomizedCellByCoords.getValue());
            }
        });

    }


    private void performMonteCarloGrowth(List<Cell> neighbours, Cell cell) {
        Random random = new Random();

        double currentEnergy = countEnergy(cell.getId(), neighbours);
        int newId = neighbours.get(random.nextInt(neighbours.size())).getId();
        double newEnergy = countEnergy(newId, neighbours);

        if (newEnergy <= currentEnergy) {
            cell.setId(newId);
        }
    }


    private double countEnergy(int id, List<Cell> neighbours) {
        int count = 0;
        for (Cell cell : neighbours) {
            if (cell.getId() != id) {
                count++;
            }
        }
        return gbEnergy * count;
    }

}
