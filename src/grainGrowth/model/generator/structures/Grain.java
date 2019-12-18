package grainGrowth.model.generator.structures;

import grainGrowth.model.core.Cell;
import grainGrowth.model.core.Coords;
import grainGrowth.model.core.Space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Grain {

    private int id;
    private Map<Coords, Cell> cellsByCoords;
    private StructureType structureType;


    public Grain(Space space, int id, StructureType structureType) {
        this.id = id;
        cellsByCoords = new HashMap<>();
        this.structureType = structureType;
        findGrainCells(space);
    }


    private void findGrainCells(Space space) {
        space.getCellsByCoords().forEach((coords, cell) -> {
            if (cell.getId() == id) {
                cellsByCoords.put(coords, cell);
            }
        });
    }


    public Map<Coords, Cell> getCellsByCoords() {
        return cellsByCoords;
    }

    public List<Cell> getCells() {
        return new ArrayList<>();
    }


    public StructureType getStructureType() {
        return structureType;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

}
