package src.controller;

import java.io.File;
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
