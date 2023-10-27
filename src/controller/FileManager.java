package src.controller;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileManager {

    private static final String FILE_DEFAULT_SRC_FOLDER = "sourceImg";
    private static final String FILE_DEST_FOLDER = "generatedImg";

    private static String FILE_STR; // foto.jpg
    private static String FILE_NAME; // foto
    private static File FILE; // File reference in the system (has the complete path as an attribute)

    public static File getFILE() {
        return FILE;
    }

    public static void setFILE(File fILE) {
        FILE = fILE;
    }

    public static boolean chooseFile() {
        JFileChooser chooser = new ThumbnailFileChooser(FILE_DEFAULT_SRC_FOLDER);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // Here things will be done
            FILE_STR = chooser.getSelectedFile().getName();
            FILE = chooser.getSelectedFile();
            if (!FILE.exists() || FILE.isDirectory()) {
                return false;
            }

            System.out.println("You chose to open this file: " +
                    chooser.getSelectedFile().getName());
            return true;
        } else {
            return false;
        }
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static void saveImage(Image imageEdit) {
        String format = "png";
        /*
         * Use the format name to initialise the file suffix.
         * Format names typically correspond to suffixes
         */
        File saveFile = new File("editedImage." + format);
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(saveFile);
        int rval = chooser.showSaveDialog(null);
        if (rval == JFileChooser.APPROVE_OPTION) {
            saveFile = chooser.getSelectedFile();
            /*
             * Write the filtered image in the selected format,
             * to the file chosen by the user.
             */
            try {
                BufferedImage bi = toBufferedImage(imageEdit);
                ImageIO.write(bi, format, saveFile);
            } catch (NullPointerException e) {
                e.printStackTrace();
                System.err.println("No image to save");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error saving image");
            }
        }
    }

    public static String createDirs() {
        // https://stackoverflow.com/questions/3634853/how-to-create-a-directory-in-java
        FILE_NAME = FILE_STR.split("\\.")[0];
        File dir = Path.of(FILE_DEST_FOLDER, FILE_NAME).toFile();
        dir.mkdirs();

        return FILE_NAME;
    }

    public static String createDir(String method) {
        File dir = Path.of(FILE_DEST_FOLDER, FILE_NAME, method).toFile();
        dir.mkdirs();

        return dir.getAbsolutePath();

    }

    public static String createDirNueva() {
        File dir = Path.of(FILE_DEST_FOLDER).toFile();
        dir.mkdirs();

        return dir.getAbsolutePath();

    }

    public static String getPath(String method) {
        return Path.of(FILE_DEST_FOLDER, FILE_NAME, method).toString();
    }
}
