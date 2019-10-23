package grainGrowth.controller;

import grainGrowth.model.GrainGrowth;
import grainGrowth.model.core.Coords;
import grainGrowth.model.core.NucleonsGenerator;
import grainGrowth.model.core.Space;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;


public class MainViewController implements Initializable {

    @FXML
    private Canvas canvas;
    private final int cellSize = 2;
    @FXML
    private TextField xSizeTextField;
    @FXML
    private TextField ySizeTextField;
    @FXML
    private TextField nucleonsNumberTextField;
    @FXML
    private Button initializeButton;
    @FXML
    private Button addGrainsButton;
    @FXML
    private Button growthButton;
    @FXML
    private MenuBar menuBar;

    private int xSize;
    private int ySize;
    private int nucleonsNumber;
    private final Map<Integer, Color> colorById = new HashMap<>();
    private Space space;
    private FileChooser fileChooser;

    public void initializeEmptySpace() {
        xSize = Integer.parseInt(xSizeTextField.getText());
        ySize = Integer.parseInt(ySizeTextField.getText());
        space = new Space(xSize, ySize);

        canvas.setWidth(xSize * cellSize);
        canvas.setHeight(ySize * cellSize);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    public void generateNucleons() {
        nucleonsNumber = Integer.parseInt(nucleonsNumberTextField.getText());
        NucleonsGenerator.putNucleonsRandomly(nucleonsNumber, space);
        draw();
    }


    public void performGrainGrowth() {
        disableNodes();
        GrainGrowth grainGrowth = new GrainGrowth(space);
        grainGrowth.simulateGrainGrowth();
        draw();
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("txt", "*.txt"),
                new FileChooser.ExtensionFilter("png", "*.png")
        );
        fileChooser.setInitialDirectory(new File("."));

        xSize = 500;
        ySize = 500;

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

        initializeEmptySpace();

        nucleonsNumber = 200;
        nucleonsNumberTextField.setText(String.valueOf(nucleonsNumber));

        nucleonsNumberTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!nucleonsNumberTextField.getText().matches("[1-9][0-9]+")) {
                    nucleonsNumberTextField.setText(String.valueOf(nucleonsNumber));
                } else {
                    int newNucleonsNumber = Integer.parseInt(nucleonsNumberTextField.getText());
                    if (newNucleonsNumber > xSize * ySize) {
                        nucleonsNumberTextField.setText(String.valueOf(nucleonsNumber));
                    }
                }
            }
        });
    }


    void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (int i = 0; i < ySize; i++) {
            for (int j = 0; j < xSize; j++) {
                int id = space.getCells()[i][j].getId();
                if (id != 0) {
                    if (!colorById.containsKey(id)) {
                        Random random = new Random();
                        Color newColor = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
                        while (colorById.containsValue(newColor)) {
                            newColor = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
                        }
                        colorById.put(id, newColor);
                    }
                    gc.setFill(colorById.get(id));
                    gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                }
            }
        }
        enableNodes();
    }


    private void disableNodes() {
        nucleonsNumberTextField.setDisable(true);
        xSizeTextField.setDisable(true);
        ySizeTextField.setDisable(true);
        initializeButton.setDisable(true);
        addGrainsButton.setDisable(true);
        growthButton.setDisable(true);
        menuBar.setDisable(true);
    }


    private void enableNodes() {
        nucleonsNumberTextField.setDisable(false);
        xSizeTextField.setDisable(false);
        ySizeTextField.setDisable(false);
        initializeButton.setDisable(false);
        addGrainsButton.setDisable(false);
        growthButton.setDisable(false);
        menuBar.setDisable(false);
    }


    public void loadSpace() {
        fileChooser.setTitle("Import");
        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (file != null) {
            try {
                space = loadSpace(file);
                xSize = space.getSizeX();
                ySize = space.getSizeY();
                canvas.setWidth(xSize * cellSize);
                canvas.setHeight(ySize * cellSize);
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                draw();
            } catch (IOException ignored) {
            }
        }
    }


    public void saveSpace() {
        fileChooser.setTitle("Export");
        File file = fileChooser.showSaveDialog(canvas.getScene().getWindow());
        if (file != null) {
            String extension;
            String filename = file.getName();
            int extensionPos = filename.lastIndexOf(46);
            int lastUnixPos = filename.lastIndexOf(47);
            int lastWindowsPos = filename.lastIndexOf(92);
            int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);
            int index =  lastSeparator > extensionPos ? -1 : extensionPos;
            extension = index == -1 ? "" : file.getName().substring(index + 1);

            try {
                if ("txt".equals(extension)) {
                    PrintWriter writer = new PrintWriter(file);
                    writer.println(space.getSizeX() + ";" + space.getSizeY());
                    for (int i = 0; i < space.getSizeY(); i++) {
                        for (int j = 0; j < space.getSizeX(); j++) {
                            writer.println(j + ";" + i + ";" + space.getCells()[i][j].getId());
                        }
                    }
                    writer.close();
                } else {
                    WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), extension, file);
                }
            } catch (IOException ignored) {
            }
        }
    }
    public static Space loadSpace(File file) throws IOException {
        String extension;
        String filename = file.getName();
        int extensionPos = filename.lastIndexOf(46);
        int lastUnixPos = filename.lastIndexOf(47);
        int lastWindowsPos = filename.lastIndexOf(92);
        int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);
        int index =  lastSeparator > extensionPos ? -1 : extensionPos;
        extension = index == -1 ? "" : file.getName().substring(index + 1);

        if ("txt".equals(extension)) {
            return prepareSpaceBasedOnTxtFile(file);
        } else {
            BufferedImage image = ImageIO.read(file);
            return prepareSpaceBasedOnImage(image);
        }
    }


    private static Space prepareSpaceBasedOnTxtFile(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        String line = bufferedReader.readLine();
        String[] numbers = line.split(";");
        Space newSpace = new Space(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));

        Coords coords;
        while ((line = bufferedReader.readLine()) != null) {
            numbers = line.split(";");
            coords = new Coords(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
            newSpace.getCell(coords).setId(Integer.parseInt(numbers[2]));
        }
        bufferedReader.close();
        return newSpace;
    }


    private static Space prepareSpaceBasedOnImage(BufferedImage image) {
        Map<Integer, Integer> idByRGB = new HashMap<>();
        idByRGB.put(-1, 0);
        int sizeX = image.getWidth() / 2;
        int sizeY = image.getHeight() / 2;
        Space newSpace = new Space(sizeX, sizeY);
        int idCounter = 1;
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                int rgb = image.getRGB(j * 2, i * 2);
                if (!idByRGB.containsKey(rgb)) {
                    idByRGB.put(rgb, idCounter);
                    idCounter++;
                }
                newSpace.getCells()[i][j].setId(idByRGB.get(rgb));
            }
        }
        return newSpace;
    }

}
