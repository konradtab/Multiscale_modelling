package grainGrowth.model.generator.structures;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class DualPhaseGenerator extends StructureGenerator {

    private List<Integer> idsToDualPhaseConversion;


    public DualPhaseGenerator(Collection<Grain> grains) {
        idsToDualPhaseConversion = new LinkedList<>();
        this.grains = grains.stream()
                .filter(grain -> grain.getStructureType() == StructureType.DUAL_PHASE)
                .collect(Collectors.toCollection(LinkedList::new));
    }


    @Override
    public void generate() {
        determineCellsByCoordsToLock();
        setDualPhaseGrainsIds();
        blockCellsFromGrowing();
    }


    private void setDualPhaseGrainsIds() {
        grains.forEach(grain -> {
            if (grain.getId() != -2) {
                idsToDualPhaseConversion.add(grain.getId());
                grain.setId(-2);
            }
        });
        cellsByCoordsToLock.values().forEach(cell -> cell.setId(-2));
    }


    public List<Integer> getIdsToDualPhaseConversion() {
        return idsToDualPhaseConversion;
    }

}
