package grainGrowth.model.core;

import java.util.LinkedList;
import java.util.List;


public class Space {

    private final int sizeX;
    private final int sizeY;

    private int maxCellId;

    private MooreNeighbourhood mooreNeighbourHood;
    private Cell[][] cells;


    public Space(int sizeX, int sizeY) {
        mooreNeighbourHood = new MooreNeighbourhood(new AbsorbentBoundaryCondition(sizeX, sizeY));

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        cells = new Cell[sizeY][sizeX];

        initializeCells();
    }


    public Space(Space otherSpace) {
        sizeX = otherSpace.getSizeX();
        sizeY = otherSpace.getSizeY();
        cells = new Cell[sizeY][sizeX];
        maxCellId = otherSpace.getMaxCellId();
        mooreNeighbourHood = otherSpace.getMooreNeighbourHood();
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                cells[i][j] = new Cell();
                cells[i][j].copyPropertiesFromOtherCell(otherSpace.getCells()[i][j]);
            }
        }
    }


    private void initializeCells() {
        maxCellId = 0;
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                cells[i][j] = new Cell();
            }
        }
    }


    public int getSizeX() {
        return sizeX;
    }


    public int getSizeY() {
        return sizeY;
    }


    public Cell[][] getCells() {
        return cells;
    }


    int getMaxCellId() {
        return maxCellId;
    }


    public void setMaxCellId(int maxCellId) {
        this.maxCellId = maxCellId;
    }


    public List<Cell> findNeighbours(Coords coords) {
        List<Coords> neighboursCoords = mooreNeighbourHood.findNeighboursCoords(coords);
        List<Cell> neighbours = new LinkedList<>();

        for (Coords c : neighboursCoords) {
            neighbours.add(getCell(c));
        }

        return neighbours;
    }


    public Cell getCell(Coords coords) {
        return cells[coords.getY()][coords.getX()];
    }


    public MooreNeighbourhood getMooreNeighbourHood() {
        return mooreNeighbourHood;
    }


    public void setMooreNeighbourHood(MooreNeighbourhood mooreNeighbourHood) {
        this.mooreNeighbourHood = mooreNeighbourHood;
    }

}
