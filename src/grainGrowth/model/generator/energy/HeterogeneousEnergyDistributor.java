package grainGrowth.model.generator.energy;

import grainGrowth.model.core.Cell;
import grainGrowth.model.core.Space;

import java.util.Random;


public class HeterogeneousEnergyDistributor {

    public static void distribute(Space space, double energyInside, double energyOnEdges) {
        Random random = new Random();
        for (Cell cell : space.getCellsByCoords().values()) {
            if (random.nextBoolean()) {
                double energy = cell.isGrainBoundary() ?
                        energyOnEdges + energyOnEdges * random.nextDouble() * 0.1 :
                        energyInside + energyInside * random.nextDouble() * 0.1;
                cell.setEnergyH(energy);
            } else {
                double energy = cell.isGrainBoundary() ?
                        energyOnEdges - energyOnEdges * random.nextDouble() * 0.1 :
                        energyInside - energyInside * random.nextDouble() * 0.1;
                cell.setEnergyH(energy);
            }
        }
    }

}
