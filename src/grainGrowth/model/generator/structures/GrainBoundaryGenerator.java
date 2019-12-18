package grainGrowth.model.generator.structures;

import grainGrowth.model.core.Cell;
import grainGrowth.model.core.Coords;
import grainGrowth.model.core.Space;
import grainGrowth.model.generator.inclusions.InclusionsGenerator;

import java.util.*;
import java.util.stream.Collectors;


public class GrainBoundaryGenerator extends StructureGenerator {

    private LinkedList<Integer> idsToInclusionsConversion;
    private Space space;
    private int boundarySize;


    public GrainBoundaryGenerator(Collection<Grain> grains, Space space, int boundarySize) {
        idsToInclusionsConversion = new LinkedList<>();
        cellsByCoordsToLock = new HashMap<>();
        this.space = space;
        this.boundarySize = boundarySize;
        this.grains = grains.stream()
                .filter(grain -> grain.getStructureType() == StructureType.GRAIN_BOUNDARY)
                .collect(Collectors.toCollection(LinkedList::new));
    }


    public void generateAllBoundaries() {
        determineCellsByCoordsToLock();
        setGrainBoundariesIds();
        blockCellsFromGrowing();
    }


    @Override
    protected void determineCellsByCoordsToLock() {
        cellsByCoordsToLock = grains.stream()
                .map(Grain::getCellsByCoords)
                .flatMap(coordsCellMap -> coordsCellMap.entrySet().stream())
                .filter(coordsCellEntry -> coordsCellEntry.getValue().isGrainBoundary())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    @Override
    public void generate() {
        determineSelectedCellsCoordsToLock();
        setGrainBoundariesIds();
        blockCellsFromGrowing();
    }


    protected void determineSelectedCellsCoordsToLock() {
        determineCellsByCoordsToLock();

        Map<Coords, Cell> additionalBoundariesCoords = new HashMap<>();
        for (Map.Entry<Coords, Cell> coordsCells : cellsByCoordsToLock.entrySet()) {
            List<Coords> neighboursCoords = space.getMooreNeighbourHood().findNeighboursCoords(coordsCells.getKey());
            for (Coords neighbourCoords : neighboursCoords) {
                Cell neighbour = space.getCell(neighbourCoords);
                if (neighbour.isGrainBoundary()) {
                    additionalBoundariesCoords.put(neighbourCoords, neighbour);
                }
            }
        }

        cellsByCoordsToLock.putAll(additionalBoundariesCoords);
    }


    private void setGrainBoundariesIds() {
        grains.forEach(grain -> {
            if (grain.getId() != -1) {
                idsToInclusionsConversion.add(grain.getId());
                grain.setId(-1);
            }
        });
    }


    @Override
    protected void blockCellsFromGrowing() {
        InclusionsGenerator inclusionsGenerator = InclusionsGenerator.createInstance(0, boundarySize, space);
        cellsByCoordsToLock.keySet().forEach(inclusionsGenerator::placeInclusions);
    }


    public int computeGbPercentage() {
        int counter = 0;
        for (Cell cell : space.getCellsByCoords().values()) {
            if (cell.getId() == -1) {
                counter++;
            }
        }
        return counter * 100 / (space.getSizeX() * space.getSizeY());
    }


    public LinkedList<Integer> getIdsToInclusionsConversion() {
        return idsToInclusionsConversion;
    }

}
