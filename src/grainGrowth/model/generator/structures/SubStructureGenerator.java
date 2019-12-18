package grainGrowth.model.generator.structures;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;


public class SubStructureGenerator extends StructureGenerator {


    public SubStructureGenerator(Collection<Grain> grains) {
        this.grains = grains.stream()
                .filter(grain -> grain.getStructureType() == StructureType.SUBSTRUCTURE)
                .collect(Collectors.toCollection(LinkedList::new));
    }


    @Override
    public void generate() {
        determineCellsByCoordsToLock();
        blockCellsFromGrowing();
    }

}
