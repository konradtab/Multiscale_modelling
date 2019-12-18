package grainGrowth.model.core;


public class Cell {

    private int id;
    private double energyH;
    private boolean growable;
    private boolean grainBoundary;


    public Cell() {
        id = 0;
        energyH = 0.0;
        growable = true;
        grainBoundary = false;
    }

    public void copyPropertiesFromOtherCell(Cell cell) {
        this.id = cell.id;
        this.growable = cell.growable;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public double getEnergyH() {
        return energyH;
    }


    public void setEnergyH(double energyH) {
        this.energyH = energyH;
    }


    public boolean isGrowable() {
        return growable;
    }

    public void setGrowable(boolean growable) {
        this.growable = growable;
    }


    public boolean isGrainBoundary() {
        return grainBoundary;
    }


    public void setGrainBoundary(boolean grainBoundary) {
        this.grainBoundary = grainBoundary;
    }

}
