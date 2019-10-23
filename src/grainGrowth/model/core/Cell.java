package grainGrowth.model.core;


public class Cell {

    private int id;


    public Cell() {
        id = 0;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public void copyPropertiesFromOtherCell(Cell cell) {
        this.id = cell.id;
    }

}
