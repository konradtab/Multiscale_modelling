package grainGrowth.controller;


public enum SimulationType {
    GRAIN_GROWTH("Grain Growth"),
    SHAPE_CONTROL_GRAIN_GROWTH("Shape control Grain Growth"),
    MONTE_CARLO_GRAIN_GROWTH("Monte Carlo Grain Growth");

    private final String string;


    SimulationType(String string) {
        this.string = string;
    }


    @Override
    public String toString() {
        return string;
    }

}
