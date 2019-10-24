package grainGrowth.model.core;

public abstract class Inclusions {
    private InclusionType type = InclusionType.Square;
    private int size = 4;
    private int number = 0;
    private Space space;

    public Inclusions(InclusionType inclusionType, int size, int number, Space space){
        this.type = inclusionType;
        this.size = size;
        this.number = number;
        this.space = space;
    }

}
