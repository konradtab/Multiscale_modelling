package grainGrowth.controller;

import grainGrowth.model.core.Coords;
import grainGrowth.model.core.InputOutputUtils;
import grainGrowth.model.core.Space;
import grainGrowth.model.generator.energy.HeterogeneousEnergyDistributor;
import grainGrowth.model.generator.energy.HomogeneousEnergyDistributor;
import grainGrowth.model.generator.inclusions.InclusionType;
import grainGrowth.model.generator.inclusions.InclusionsGenerator;
import grainGrowth.model.generator.nucleons.NucleonsGenerator;
import grainGrowth.model.generator.structures.*;
import grainGrowth.model.simulation.GrainGrowth;
import grainGrowth.model.simulation.MonteCarloGrainGrowth;
import grainGrowth.model.simulation.ShapeControlGrainGrowth;
import grainGrowth.model.simulation.SimpleGrainGrowth;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class MainViewController implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private TextField xSizeTextField;
    @FXML
    private TextField ySizeTextField;
    @FXML
    private TextField nucleonsNumberTextField;
    @FXML
    private TextField inclusionsNumberTextField;
    @FXML
    private TextField inclusionSizeTextField;
    @FXML
    private TextField gbEnergyTextField;
    @FXML
    private TextField iterationsTextField;
    private final ObservableMap<Integer, Grain> selectedGrainsById = FXCollections.observableHashMap();
    @FXML
    private Label gbEnergyLabel;
    @FXML
    private Label iterationsLabel;
    private final Map<Integer, Color> colorById = new HashMap<>();
    private final int cellSize = 2;
    @FXML
    private TextField gbSizeTextField;
    @FXML
    private TextField energyInsideTextField;
    @FXML
    private TextField energyOnEdgesTextField;
    @FXML
    private Label numberOfNucleonsLabel;
    @FXML
    private Label energyOnEdgesLabel;
    @FXML
    private Label energyInsideLabel;
    @FXML
    private Button initializeButton;
    @FXML
    private Button generateNucleonsButton;
    @FXML
    private Button generateInclusionsButton;
    @FXML
    private Button performGrainGrowthButton;
    @FXML
    private Button toggleEnergyButton;
    @FXML
    private Button distributeEnergyButton;
    @FXML
    private Label gbPercentageLabel;
    @FXML
    private Button generateGrainBoundariesButton;
    @FXML
    private Button lockSelectedGrainsButton;
    @FXML
    private TextField probabilityTextField;
    @FXML
    private ComboBox<InclusionType> inclusionTypeComboBox;
    @FXML
    private ComboBox<SimulationType> simulationTypeComboBox;
    @FXML
    private ComboBox<StructureType> structureTypeComboBox;
    @FXML
    private ComboBox<EnergyDistributionType> energyDistributionComboBox;
    @FXML
    private Button clearGrainsButton;
    @FXML
    private MenuBar menuBar;
    private List<Control> controls;
    private FileChooser fileChooser;
    private Space space;
    private int xSize;
    private int ySize;
    private int inclusionSize;
    private int nucleonsNumber;
    private int inclusionsNumber;


    public void initializeEmptySpace() {
        xSize = Integer.parseInt(xSizeTextField.getText());
        ySize = Integer.parseInt(ySizeTextField.getText());
        space = new Space(xSize, ySize);
        adjustNodesToSpaceSize();
        selectedGrainsById.clear();
        gbPercentageLabel.setText("GB: 0%");
        clearCanvas();
    }


    private void adjustNodesToSpaceSize() {
        canvas.setWidth(xSize * cellSize);
        canvas.setHeight(ySize * cellSize);
    }


    private void clearCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    public void generateNucleons() {
        nucleonsNumber = Integer.parseInt(nucleonsNumberTextField.getText());

        switch (simulationTypeComboBox.getValue()) {
            case MONTE_CARLO_GRAIN_GROWTH:
                NucleonsGenerator.fillSpaceWithNumberOfUniqueIds(nucleonsNumber, space);
                break;
            default:
                NucleonsGenerator.putNucleonsRandomly(nucleonsNumber, space);
                break;
        }

        draw();
    }


    public void generateInclusions() {
        inclusionsNumber = Integer.parseInt(inclusionsNumberTextField.getText());
        inclusionSize = Integer.parseInt(inclusionSizeTextField.getText());
        InclusionsGenerator.TYPE = inclusionTypeComboBox.getValue();
        InclusionsGenerator.createInstance(inclusionsNumber, inclusionSize, space).putInclusionsRandomly();
        draw();
    }


    public void performGrainGrowth() {
        disableNodes();
        createGrainGrowthInstance().simulateGrainGrowth();
        draw();
    }


    private GrainGrowth createGrainGrowthInstance() {
        switch (simulationTypeComboBox.getValue()) {
            case SHAPE_CONTROL_GRAIN_GROWTH:
                double probability = (Double.parseDouble(probabilityTextField.getText())/100);
                return new ShapeControlGrainGrowth(space, probability);

            case MONTE_CARLO_GRAIN_GROWTH:
                int iterations = Integer.parseInt(iterationsTextField.getText());
                double gbEnergy = Double.parseDouble(gbEnergyTextField.getText());
                return new MonteCarloGrainGrowth(space, iterations, gbEnergy);

            default:
                return new SimpleGrainGrowth(space);
        }
    }


    void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (int i = 0; i < ySize; i++) {
            for (int j = 0; j < xSize; j++) {
                int id = space.getCell(Coords.coords(j, i)).getId();
                if (id != 0) {
                    if (!colorById.containsKey(id)) {
                        generateNewColor(id);
                    }
                    gc.setFill(colorById.get(id));
                    gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);

                    if (selectedGrainsById.containsKey(id)) {
                        gc.setFill(Color.rgb(255, 0, 0, 0.7));
                        gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                    }

                } else {
                    gc.clearRect(j * cellSize, i * cellSize, cellSize, cellSize);
                }
            }
        }
        enableNodes();
    }


    private void generateNewColor(int id) {
        Random random = new Random();

        int red = random.nextInt(100) + 30;
        int green = random.nextInt(220) + 30;
        int blue = random.nextInt(220) + 30;

        Color newColor = Color.rgb(red, green, blue);
        while (colorById.containsValue(newColor)) {
            red = random.nextInt(100) + 30;
            green = random.nextInt(220) + 30;
            blue = random.nextInt(220) + 30;
            newColor = Color.rgb(red, green, blue);
        }

        colorById.put(id, newColor);
    }


    private void disableNodes() {
        for (Control control : controls) {
            control.setDisable(true);
        }
    }


    private void enableNodes() {
        for (Control control : controls) {
            control.setDisable(false);
        }
    }


    public void loadSpace() {
        fileChooser.setTitle("Load space");
        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (file != null) {
            try {
                space = InputOutputUtils.loadSpace(file);
                xSize = space.getSizeX();
                ySize = space.getSizeY();
                adjustNodesToSpaceSize();
                clearCanvas();
                draw();
            } catch (IOException ignored) {
            }
        }
    }


    public void saveSpace() {
        fileChooser.setTitle("Save space");

        File file = fileChooser.showSaveDialog(canvas.getScene().getWindow());
        if (file != null) {
            String extension = getExtension(file.getName());
            try {
                if ("txt".equals(extension)) {
                    InputOutputUtils.saveSpace(space, file);
                } else {
                    WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), extension, file);
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        } else {
            int index = indexOfExtension(filename);
            return index == -1 ? "" : filename.substring(index + 1);
        }
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int extensionPos = filename.lastIndexOf(46);
            int lastSeparator = indexOfLastSeparator(filename);
            return lastSeparator > extensionPos ? -1 : extensionPos;
        }
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int lastUnixPos = filename.lastIndexOf(47);
            int lastWindowsPos = filename.lastIndexOf(92);
            return Math.max(lastUnixPos, lastWindowsPos);
        }
    }


    private void selectGrain(MouseEvent event) {
        int x = (int) Math.ceil(event.getX() / cellSize);
        int y = (int) Math.ceil(event.getY() / cellSize);

        int id = space.getCell(Coords.coords(x, y)).getId();

        if (id != 0) {
            if (selectedGrainsById.containsKey(id)) {
                selectedGrainsById.remove(id);
            } else {
                selectedGrainsById.put(id, new Grain(space, id, structureTypeComboBox.getValue()));
            }
            draw();
        }
    }


    public void lockStructures() {
        SubStructureGenerator subStructureGenerator = new SubStructureGenerator(selectedGrainsById.values());
        DualPhaseGenerator dualPhaseGenerator = new DualPhaseGenerator(selectedGrainsById.values());
        int size = Integer.parseInt(gbSizeTextField.getText());
        GrainBoundaryGenerator grainBoundaryGenerator = new GrainBoundaryGenerator(selectedGrainsById.values(), space, size);

        space.determineBorderCells();
        subStructureGenerator.generate();
        dualPhaseGenerator.generate();
        grainBoundaryGenerator.generate();
        space.resetBorderProperty();

        convertGrainIdsToDualPhaseIfNeeded(dualPhaseGenerator.getIdsToDualPhaseConversion());
        convertGrainIdsToInclusionsIfNeeded(grainBoundaryGenerator.getIdsToInclusionsConversion());

        clearNotSelectedGrains();

        String gbPercentage = String.valueOf(grainBoundaryGenerator.computeGbPercentage());
        gbPercentageLabel.setText("GB: " + gbPercentage + "%");

        draw();
    }


    private void convertGrainIdsToDualPhaseIfNeeded(List<Integer> idsToDualPhaseConversion) {
        if (!idsToDualPhaseConversion.isEmpty()) {
            selectedGrainsById.put(-2, new Grain(space, -2, StructureType.DUAL_PHASE));
            idsToDualPhaseConversion.forEach(selectedGrainsById::remove);
        }
    }


    private void convertGrainIdsToInclusionsIfNeeded(List<Integer> idsToInclusionsConversion) {
        if (!idsToInclusionsConversion.isEmpty()) {
            selectedGrainsById.put(-1, new Grain(space, -1, StructureType.GRAIN_BOUNDARY));
            idsToInclusionsConversion.forEach(selectedGrainsById::remove);
        }
    }


    public void generateGrainBoundaries() {
        int maxCellId = space.findMaxCellId();
        List<Grain> grains = new LinkedList<>();
        for (int i = 1; i <= maxCellId; i++) {
            grains.add(new Grain(space, i, StructureType.GRAIN_BOUNDARY));
        }
        space.determineBorderCells();
        int size = Integer.parseInt(gbSizeTextField.getText());
        GrainBoundaryGenerator grainBoundaryGenerator = new GrainBoundaryGenerator(grains, space, size);
        grainBoundaryGenerator.generateAllBoundaries();
        space.resetBorderProperty();

        convertGrainIdsToInclusionsIfNeeded(grainBoundaryGenerator.getIdsToInclusionsConversion());

        String gbPercentage = String.valueOf(grainBoundaryGenerator.computeGbPercentage());
        gbPercentageLabel.setText("GB: " + gbPercentage + "%");
        selectedGrainsById.remove(-1);
        draw();
    }


    public void clearGrains() {
        selectedGrainsById.put(-1, new Grain(space, -1, StructureType.GRAIN_BOUNDARY));
        clearNotSelectedGrains();
        draw();
    }


    private void clearNotSelectedGrains() {
        space.getCellsByCoords().values().stream()
                .filter(cell -> !selectedGrainsById.containsKey(cell.getId()))
                .forEach(cell -> {
                    cell.setId(0);
                    cell.setGrowable(true);
                });
        selectedGrainsById.remove(-1);
    }


    private boolean showingEnergy = false;

    public void distributeEnergy() {
        disableNodes();
        if (EnergyDistributionType.HETEROGENEOUS == energyDistributionComboBox.getValue()) {
            double energyInside = Double.parseDouble(energyInsideTextField.getText());
            double energyOnEdges = Double.parseDouble(energyOnEdgesTextField.getText());
            //System.out.println(energyOnEdges);
            HeterogeneousEnergyDistributor.distribute(space, energyInside, energyOnEdges);

        } else {
            double energyValue = Double.parseDouble(energyInsideTextField.getText());
            HomogeneousEnergyDistributor.distribute(space, energyValue);
        }
        enableNodes();
    }

    public void toggleEnergyDistribution() {
        if (showingEnergy) {
            draw();
            toggleEnergyButton.setText("Show");
            showingEnergy = false;
        } else {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            int min = (int)Math.floor(space.getCell(Coords.coords(0, 0)).getEnergyH());
            int max = (int)Math.floor(space.getCell(Coords.coords(0, 0)).getEnergyH());
            for (int i = 0; i < ySize; i++) {
                for (int j = 0; j < xSize; j++) {
                    if(min > space.getCell(Coords.coords(j, i)).getEnergyH()){
                        min = (int)Math.floor(space.getCell(Coords.coords(j, i)).getEnergyH());
                    }
                    if(max<space.getCell(Coords.coords(j, i)).getEnergyH()){
                        max = (int)Math.floor(space.getCell(Coords.coords(j, i)).getEnergyH());
                    }
                }
            }
            for (int i = 0; i < ySize; i++) {
                for (int j = 0; j < xSize; j++) {
                    double cellEnergy = space.getCell(Coords.coords(j, i)).getEnergyH();
                    double fraction = (cellEnergy - min) / (max - min);
                    gc.setFill(Color.BLUE.interpolate(Color.GREEN, fraction));
                    gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                }
            }

            toggleEnergyButton.setText("Hide");
            showingEnergy = true;
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nucleonsNumber = 100;
        colorById.put(-1, Color.BLACK);
        colorById.put(-2, Color.MAGENTA);
        addAllControlsToList();
        initializeFileChooser();
        initializeSizeTextFields();
        initializeEmptySpace();
        initializeGeneratorTextField(nucleonsNumberTextField, nucleonsNumber);
        initializeEnergyDistributionTextFields();
        initializeInclusionControls();
        initializeMonteCarloControls();
        initializeSimulationTypeComboBox();
        initializeStructureTypeComboBox();
        initializeEnergyDistributionComboBox();
        initializeGrainSelectionModules();
    }

    private void initializeGrainSelectionModules() {
        canvas.setOnMouseClicked(this::selectGrain);
    }


    private void initializeSimulationTypeComboBox() {
        simulationTypeComboBox.setItems(FXCollections.observableArrayList(SimulationType.values()));
        simulationTypeComboBox.getSelectionModel().selectFirst();
    }


    private void initializeStructureTypeComboBox() {
        structureTypeComboBox.setItems(FXCollections.observableArrayList(StructureType.values()));
        structureTypeComboBox.getSelectionModel().selectFirst();
    }


    private void initializeEnergyDistributionComboBox() {
        energyDistributionComboBox.setItems(FXCollections.observableArrayList(EnergyDistributionType.values()));
        energyDistributionComboBox.getSelectionModel().selectFirst();
        energyDistributionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == EnergyDistributionType.HETEROGENEOUS) {
                energyOnEdgesTextField.setManaged(true);
                energyOnEdgesLabel.setManaged(true);
                energyInsideLabel.setText("Energy Inside");
            } else {
                energyOnEdgesTextField.setManaged(false);
                energyOnEdgesLabel.setManaged(false);
                energyInsideLabel.setText("Energy Value");
            }
        });
    }



    private void initializeMonteCarloControls() {
        iterationsTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!iterationsTextField.getText().matches("[1-9][0-9]+")) {
                    iterationsTextField.setText(String.valueOf(20));
                }
            }
        });
        gbEnergyTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!gbEnergyTextField.getText().matches("0\\.[1-9][0-9]*|1\\.0")) {
                    gbEnergyTextField.setText(String.valueOf(0.5));
                }
            }
        });

        iterationsTextField.visibleProperty().bind(iterationsTextField.managedProperty());
        gbEnergyTextField.visibleProperty().bind(gbEnergyTextField.managedProperty());
        iterationsLabel.visibleProperty().bind(iterationsLabel.managedProperty());
        gbEnergyLabel.visibleProperty().bind(gbEnergyLabel.managedProperty());
    }


    private void initializeInclusionControls() {
        inclusionTypeComboBox.setItems(FXCollections.observableArrayList(InclusionType.values()));
        inclusionTypeComboBox.getSelectionModel().selectFirst();

        inclusionsNumber = 50;
        initializeGeneratorTextField(inclusionsNumberTextField, inclusionsNumber);

        inclusionSize = 5;
        inclusionSizeTextField.setText(String.valueOf(inclusionSize));
        inclusionSizeTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!inclusionSizeTextField.getText().matches("[1-9][0-9]?")) {
                    inclusionSizeTextField.setText(String.valueOf(inclusionSize));
                }
            }
        });
    }


    private void initializeSizeTextFields() {
        xSize = 400;
        ySize = 400;

        xSizeTextField.setText(String.valueOf(xSize));
        ySizeTextField.setText(String.valueOf(ySize));

        xSizeTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!xSizeTextField.getText().matches("[1-9][0-9]{0,2}|1000")) {
                    xSizeTextField.setText(String.valueOf(xSize));
                }
            }
        });

        ySizeTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!ySizeTextField.getText().matches("[1-9][0-9]{0,2}|1000")) {
                    ySizeTextField.setText(String.valueOf(ySize));
                }
            }
        });

        gbSizeTextField.setText("1");
        gbSizeTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!gbSizeTextField.getText().matches("[1-9][0-9]?|100")) {
                    gbSizeTextField.setText(String.valueOf(1));
                }
            }
        });
    }


    private void initializeFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        fileChooser.setInitialDirectory(new File("."));
    }


    private void initializeGeneratorTextField(TextField generatorTextField, int number) {
        generatorTextField.setText(String.valueOf(number));

        generatorTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!generatorTextField.getText().matches("[1-9][0-9]{0,9}")) {
                    generatorTextField.setText(String.valueOf(number));
                } else {
                    int newNucleonsNumber = Integer.parseInt(generatorTextField.getText());
                    if (newNucleonsNumber > xSize * ySize) {
                        generatorTextField.setText(String.valueOf(number));
                    }
                }
            }
        });
    }


    private void initializeEnergyDistributionTextFields() {
        energyInsideTextField.setText("2");
        energyOnEdgesTextField.setText("7");

        energyInsideTextField.visibleProperty().bind(energyInsideTextField.managedProperty());
        energyOnEdgesTextField.visibleProperty().bind(energyOnEdgesTextField.managedProperty());
        energyOnEdgesLabel.visibleProperty().bind(energyOnEdgesLabel.managedProperty());

        energyInsideTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!energyInsideTextField.getText().matches("\\d+")) {
                    energyInsideTextField.setText("2");
                }
            }
        });

        energyOnEdgesTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!energyOnEdgesTextField.getText().matches("\\d+")) {
                    energyOnEdgesTextField.setText("7");
                }
            }
        });
    }


    private void addAllControlsToList() {
        controls = new LinkedList<>(Arrays.asList(
                nucleonsNumberTextField,
                energyOnEdgesTextField,
                energyInsideTextField,
                inclusionsNumberTextField,
                inclusionSizeTextField,
                inclusionTypeComboBox,
                xSizeTextField,
                ySizeTextField,
                initializeButton,
                gbSizeTextField,
                clearGrainsButton,
                generateNucleonsButton,
                generateInclusionsButton,
                performGrainGrowthButton,
                distributeEnergyButton,
                toggleEnergyButton,
                menuBar,
                simulationTypeComboBox,
                structureTypeComboBox,
                energyDistributionComboBox,
                lockSelectedGrainsButton,
                generateGrainBoundariesButton,
                iterationsTextField,
                gbEnergyTextField
        ));
    }

}
