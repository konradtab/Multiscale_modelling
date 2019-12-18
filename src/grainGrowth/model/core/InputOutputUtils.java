package grainGrowth.model.core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class InputOutputUtils {

    private static final int EMPTY = -1;


    public static void saveSpace(Space space, File file) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(file);
        writer.println(space.getSizeX() + ";" + space.getSizeY());
        space.getCellsByCoords().forEach((coords, cell) ->
                writer.println(coords.getX() + ";" + coords.getY() + ";" + cell.getId())
        );
        writer.close();
    }


    public static Space loadSpace(File file) throws IOException {
        if ("txt".equals(getExtension(file.getName()))) {
            return prepareSpaceBasedOnTxtFile(new BufferedReader(new FileReader(file)));
        } else {
            BufferedImage image = ImageIO.read(file);
            return prepareSpaceBasedOnImage(image);
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


    private static Space prepareSpaceBasedOnTxtFile(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        String[] numbers = line.split(";");
        Space newSpace = new Space(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));

        Coords coords;
        while ((line = bufferedReader.readLine()) != null) {
            numbers = line.split(";");
            coords = Coords.coords(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
            Cell cell = newSpace.getCell(coords);
            cell.setId(Integer.parseInt(numbers[2]));
            if (cell.getId() < 0) {
                cell.setGrowable(false);
            }
        }
        bufferedReader.close();
        return newSpace;
    }


    private static Space prepareSpaceBasedOnImage(BufferedImage image) {
        Map<Integer, Integer> idByRGB = new HashMap<>();
        idByRGB.put(EMPTY, 0);
        idByRGB.put(Color.BLACK.getRGB(), -1);
        idByRGB.put(Color.MAGENTA.getRGB(), -2);
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
                Cell cell = newSpace.getCell(Coords.coords(j, i));
                cell.setId(idByRGB.get(rgb));
                if (cell.getId() < 0) {
                    cell.setGrowable(false);
                }
            }
        }
        return newSpace;
    }

}
