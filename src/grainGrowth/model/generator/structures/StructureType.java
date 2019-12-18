package grainGrowth.model.generator.structures;

public enum StructureType {
    SUBSTRUCTURE("Substructure"),
    DUAL_PHASE("Dual phase"),
    GRAIN_BOUNDARY("Grain boundary");

    private final String text;

    StructureType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
