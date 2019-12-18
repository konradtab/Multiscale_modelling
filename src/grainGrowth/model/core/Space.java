package grainGrowth.model.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Space {

    private final int sizeX;
    private final int sizeY;

    private AbsorbentBoundaryCondition boundaryCondition;
    private MooreNeighbourhood mooreNeighbourHood;
    private Map<Coords, Cell> cellsByCoords;


    public Space(int sizeX, int sizeY) {
        boundaryCondition = new AbsorbentBoundaryCondition(sizeX, sizeY);
        mooreNeighbourHood = new MooreNeighbourhood(boundaryCondition);

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        cellsByCoords = new HashMap<>();

        initializeCells();
    }


    public Space(Space otherSpace) {
        sizeX = otherSpace.getSizeX();
        sizeY = otherSpace.getSizeY();
        cellsByCoords = new HashMap<>();
        boundaryCondition = otherSpace.getBoundaryCondition();
        mooreNeighbourHood = otherSpace.getMooreNeighbourHood();

        otherSpace.cellsByCoords.forEach((coords, cell) -> {
            Cell newCell = new Cell();
            newCell.copyPropertiesFromOtherCell(cell);
            cellsByCoords.put(coords, newCell);
        });

    }


    private void initializeCells() {
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                cellsByCoords.put(Coords.coords(j, i), new Cell());
            }
        }
    }


    public int getSizeX() {
        return sizeX;
    }


    public int getSizeY() {
        return sizeY;
    }


    public Map<Coords, Cell> getCellsByCoords() {
        return cellsByCoords;
    }


    public void determineBorderCells() {
        cellsByCoords.forEach((coords, cell) -> {
            for (Coords neighbourCoords : mooreNeighbourHood.findNeighboursCoords(coords)) {
                Cell neighbour = getCell(neighbourCoords);
                if (cell.getId() != neighbour.getId()) {
                    neighbour.setGrainBoundary(true);
                    cell.setGrainBoundary(true);
                }
            }
        });
    }


    public void resetBorderProperty() {
        cellsByCoords.values().forEach(cell -> cell.setGrainBoundary(false));
    }


    public int findMaxCellId() {
        int maxCellsId = 0;
        for (Cell cell : cellsByCoords.values()) {
            int id = cell.getId();
            if (id > maxCellsId) {
                maxCellsId = id;
            }
        }
        return maxCellsId;
    }


    public List<Cell> findNeighbours(Coords coords) {
        return getNeighboursCells(mooreNeighbourHood.findNeighboursCoords(coords));
    }


    public List<Cell> getNeighboursCells(List<Coords> neighboursCoords) {
        List<Cell> neighbours = new LinkedList<>();
        for (Coords c : neighboursCoords) {
            neighbours.add(getCell(c));
        }
        return neighbours;
    }


    public Cell getCell(Coords coords) {
        return cellsByCoords.get(coords);
    }


    public AbsorbentBoundaryCondition getBoundaryCondition() {
        return boundaryCondition;
    }


    public MooreNeighbourhood getMooreNeighbourHood() {
        return mooreNeighbourHood;
    }

}
