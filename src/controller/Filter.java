package src.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.io.*;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Filter {

    private static final int N_DIFFS = 5;
    private static final int R_DIFF = 10;
    private static final int SALTO = 1;

    private static final String CONFIG_FOLDER = ".config";
    private static final String COUNT_FILE_STR = "n.txt";

    private static final int R_SHUFFLE = 50;

    private static final int BATCH_SIZE = 4;
    private static final String FILE_DEST_FOLDER = null;
    private static final String FILE_NAME = null;

    // Parameters
    private static double variance = 0.6;
    private static double variance_scalar = 1.6;

    private static int radius = 10;

    // values around the midtone greyvalue of the greyvalue
    private static double threshold = 0.26 * 255; // controls the level above which luminance values will become white

    // values around 20
    private static double p = 16; // strength of edge sharpening

    // sensitive in low values, less sensitive with larger values
    private static double phi = 0.01; // sharpness of black and white transition

    private static void gaussianBlur(BufferedImage bf, BufferedImage res, double variance, int radius)
            throws IOException {

        String method = "gaussianBlur";
        File dir = Path.of(FILE_DEST_FOLDER + "/" + FILE_NAME, method).toFile();
        dir.mkdirs();

        String path = dir.getAbsolutePath();

        double[][] weights = constructWeights(variance, radius);

        for (int i = 0; i < bf.getWidth(); i++) {
            for (int j = 0; j < bf.getHeight(); j++) {

                double[][] reds = new double[radius][radius];
                double[][] greens = new double[radius][radius];
                double[][] blues = new double[radius][radius];

                for (int j2 = 0; j2 < weights.length; j2++) {
                    for (int k = 0; k < weights[j2].length; k++) {
                        int x = i + j2 - (weights.length / 2);
                        int y = j + k - (weights.length / 2);

                        double weight = weights[j2][k];

                        if (x < 0) {
                            x = Math.abs(x);
                        }
                        if (x > bf.getWidth() - 1) {
                            x = 2 * bf.getWidth() - x - 2;
                        }
                        if (y < 0) {
                            y = Math.abs(y);
                        }
                        if (y > bf.getHeight() - 1) {
                            y = 2 * bf.getHeight() - y - 2;
                        }
                        Color color = new Color(bf.getRGB(x, y));

                        reds[j2][k] = weight * color.getRed();
                        greens[j2][k] = weight * color.getGreen();
                        blues[j2][k] = weight * color.getBlue();

                    }
                }
                int red = getWeightedValue(reds);
                int green = getWeightedValue(greens);
                int blue = getWeightedValue(blues);

                res.setRGB(i, j,
                        new Color(red, green, blue).getRGB());

            }
        }
        String name = variance + "-" + radius;
        saveImage(path, name, res);
    }

    private static double[][] constructWeights(double variance, int radius) {
        double weights[][] = new double[radius][radius];
        double sum = 0;
        for (int i = 0; i < radius; i++) {
            for (int j = 0; j < radius; j++) {
                weights[i][j] = gaussianModel(i - radius / 2, j - radius / 2, variance);
                sum += weights[i][j];
            }

        }
        for (int i = 0; i < radius; i++) {
            for (int j = 0; j < radius; j++) {
                weights[i][j] /= sum;
            }
        }
        return weights;
    }

    private static int getWeightedValue(double[][] weightedColor) {
        double sum = 0;
        for (int i = 0; i < weightedColor.length; i++) {
            for (int j = 0; j < weightedColor.length; j++) {
                sum += weightedColor[i][j];
            }
        }
        return (int) sum;
    }

    private static double gaussianModel(int x, int y, double variance) {

        return (1 / (2 * Math.PI * Math.pow(variance, 2))
                * Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(variance, 2))));
    }

    public static BufferedImage DoG(BufferedImage bf,
            double variance, double variance_scalar, int radius, double threshold,
            double scalar, double phi) throws IOException {
        BufferedImage img1 = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage img2 = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);

        File i1 = new File(FILE_DEST_FOLDER + "/" + FILE_NAME + "/gaussianBlur/" + variance + "-" + radius + ".png");

        if (!i1.exists() || i1.isDirectory())
            gaussianBlur(bf, img1, variance, radius);
        else
            img1 = javax.imageio.ImageIO.read(i1);
        File i2 = new File(FILE_DEST_FOLDER + "/" + FILE_NAME + "/gaussianBlur/" + variance_scalar * variance + "-"
                + radius + ".png");
        if (!i2.exists() || i2.isDirectory())
            gaussianBlur(bf, img2, variance * variance_scalar, radius);
        else
            img2 = javax.imageio.ImageIO.read(i2);

        String method = "DoG";
        String path = FileManager.createDir(method);
        

        BufferedImage res = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < bf.getHeight(); i++) {
            for (int j = 0; j < bf.getWidth(); j++) {

                int blue = (int) Math
                        .abs((1 + scalar) * (img1.getRGB(j, i) & 0xff) - scalar * (img2.getRGB(j, i) & 0xff));
                int green = (int) Math
                        .abs((1 + scalar) * ((img1.getRGB(j, i) & 0xff00) >> 8)
                                - scalar * ((img2.getRGB(j, i) & 0xff00) >> 8));
                int red = (int) Math
                        .abs((1 + scalar) * ((img1.getRGB(j, i) & 0xff0000) >> 16)
                                - scalar * ((img2.getRGB(j, i) & 0xff0000) >> 16));

                double col = red + blue + green;

                col /= 3;
                int color = 255;
                if (col < threshold) {
                    color = (int) (127.5 * (1 + Math.tanh(phi * (col - threshold))));

                }
                res.setRGB(j, i, new Color(color, color, color).getRGB());

            }
        }
        String name = variance + "-" + variance_scalar + "-" + radius + "-" + threshold + "-" + scalar + "-" + phi;
        saveImage(path, name, res);

        return res;
    }

    private static BufferedImage colorDoG(BufferedImage bf,
            double variance, double variance_scalar, int radius,
            double threshold, double scalar, double phi) throws IOException {

        BufferedImage img1 = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage img2 = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);
        BufferedImage res = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);

        File i1 = new File(FILE_DEST_FOLDER + "/" + FILE_NAME + "/gaussianBlur/" + variance + "-" + radius + ".png");

        if (!i1.exists() || i1.isDirectory())
            gaussianBlur(bf, img1, variance, radius);
        else
            img1 = javax.imageio.ImageIO.read(i1);
        File i2 = new File(FILE_DEST_FOLDER + "/" + FILE_NAME + "/gaussianBlur/" + variance_scalar * variance + "-"
                + radius + ".png");
        if (!i2.exists() || i2.isDirectory())
            gaussianBlur(bf, img2, variance * variance_scalar, radius);
        else
            img2 = javax.imageio.ImageIO.read(i2);

        String method = "colorDoG";
        File dir = Path.of(FILE_DEST_FOLDER + "/" + FILE_NAME, method).toFile();
        dir.mkdirs();

        String path = dir.getAbsolutePath();

        for (int i = 0; i < bf.getHeight(); i++) {
            for (int j = 0; j < bf.getWidth(); j++) {

                int blue = (int) Math
                        .abs((1 + scalar) * (img1.getRGB(j, i) & 0xff) - scalar * (img2.getRGB(j, i) & 0xff));
                int green = (int) Math
                        .abs((1 + scalar) * ((img1.getRGB(j, i) & 0xff00) >> 8)
                                - scalar * ((img2.getRGB(j, i) & 0xff00) >> 8));
                int red = (int) Math
                        .abs((1 + scalar) * ((img1.getRGB(j, i) & 0xff0000) >> 16)
                                - scalar * ((img2.getRGB(j, i) & 0xff0000) >> 16));

                if (red < threshold)
                    red = (int) (127.5 * (1 + Math.tanh(phi * (red - threshold))));
                else
                    red = 255;
                if (green < threshold)
                    green = (int) (127.5 * (1 + Math.tanh(phi * (green - threshold))));
                else
                    green = 255;
                if (blue < threshold)
                    blue = (int) (127.5 * (1 + Math.tanh(phi * (blue - threshold))));
                else
                    blue = 255;

                res.setRGB(j, i, new Color(red, green, blue).getRGB());

            }
        }
        String name = variance + "-" + variance_scalar + "-" + radius + "-" + threshold + "-" + scalar + "-" + phi;

        saveImage(path, name, res);
        return res;

    }

    private static void differenceBW(BufferedImage img1, BufferedImage img2, BufferedImage res) throws IOException {

        String method = "differenceBW";
        File dir = Path.of(FILE_DEST_FOLDER + "/" + FILE_NAME, method).toFile();
        dir.mkdirs();

        String path = dir.getAbsolutePath();
        for (int i = 0; i < img1.getHeight(); i++) {
            for (int j = 0; j < img1.getWidth(); j++) {

                int blue = Math.abs(img1.getRGB(j, i) & 0xff - img2.getRGB(j, i) & 0xff);
                int green = Math.abs(((img1.getRGB(j, i) & 0xff00) >> 8) - ((img2.getRGB(j, i) & 0xff00) >> 8));
                int red = Math.abs(((img1.getRGB(j, i) & 0xff0000) >> 16) - ((img2.getRGB(j, i) & 0xff0000) >> 16));

                int color = (red + green + blue) / 3;
                res.setRGB(j, i, (color << 16 | color << 8 | color));

            }
        }
        saveImage(path, res);

    }

    // Añadir parametros a esta funcion
    private static void pixelify(BufferedImage bf, BufferedImage res) throws IOException {

        String method = "pixelify";
        File dir = Path.of(FILE_DEST_FOLDER + "/" + FILE_NAME, method).toFile();
        dir.mkdirs();

        String path = dir.getAbsolutePath();

        for (int i = 0; i < bf.getHeight(); i += 2 * BATCH_SIZE) {
            for (int j = 0; j < bf.getWidth(); j += 2 * BATCH_SIZE) {
                final int X = j;
                final int Y = i;
                new Thread(new Runnable() {
                    int x0 = X;
                    int y0 = Y;

                    @Override
                    public void run() {
                        int newBlue = 0;
                        int newGreen = 0;
                        int newRed = 0;
                        int n = 0;
                        for (int j2 = -BATCH_SIZE; j2 <= BATCH_SIZE; j2++) {
                            for (int k = -BATCH_SIZE; k <= BATCH_SIZE; k++) {

                                int x = x0 + k;
                                int y = y0 + j2;
                                if (x < 0 || x > bf.getWidth() - 1 || y < 0 || y > bf.getHeight() - 1)
                                    continue;
                                int color = bf.getRGB(x, y);
                                newBlue += color & 0xff;
                                newGreen += (color & 0xff00) >> 8;
                                newRed += (color & 0xff0000) >> 16;
                                n++;

                            }
                        }
                        int color = (newRed / n << 16 | newGreen / n << 8 | newBlue / n);
                        for (int j2 = -BATCH_SIZE; j2 <= BATCH_SIZE; j2++) {
                            for (int k = -BATCH_SIZE; k <= BATCH_SIZE; k++) {

                                int x = x0 + k;
                                int y = y0 + j2;
                                if (x < 0 || x > bf.getWidth() - 1 || y < 0 || y > bf.getHeight() - 1)
                                    continue;
                                res.setRGB(x, y, color);

                            }
                        }
                    }
                }).start();
            }
        }

        saveImage(path, "pixelify", res);

    }

    // Añadir parametros a esta funcion
    private static void pixelMean(BufferedImage bf, BufferedImage res) throws IOException {

        String method = "pixelMean";
        File dir = Path.of(FILE_DEST_FOLDER + "/" + FILE_NAME, method).toFile();
        dir.mkdirs();

        String path = dir.getAbsolutePath();

        for (int o = 0; o < N_DIFFS; o++) {

            for (int i = 0; i < bf.getHeight(); i++) {
                for (int j = 0; j < bf.getWidth(); j++) {

                    int newBlue = 0;
                    int newGreen = 0;
                    int newRed = 0;
                    int n = 0;
                    for (int j2 = -R_DIFF; j2 < R_DIFF + 1; j2 = j2 + SALTO) {
                        for (int k = -R_DIFF; k < R_DIFF + 1; k = k + SALTO) {
                            int x = j + k;
                            int y = i + j2;
                            if (x < 0 || x > bf.getWidth() - 1 || y < 0 || y > bf.getHeight() - 1)
                                continue;
                            int color = bf.getRGB(x, y);
                            newBlue += color & 0xff;
                            newGreen += (color & 0xff00) >> 8;
                            newRed += (color & 0xff0000) >> 16;
                            n++;

                        }
                    }
                    res.setRGB(j, i, (newRed / n << 16 | newGreen / n << 8 | newBlue / n));
                }
            }
            bf = res;
        }

        saveImage(path, "pixelMean", res);
    }

    private static void pixelShuffle(BufferedImage bf, BufferedImage res) throws IOException {

        String method = "pixelShuffle";
        File dir = Path.of(FILE_DEST_FOLDER + "/" + FILE_NAME, method).toFile();
        dir.mkdirs();

        String path = dir.getAbsolutePath();

        Random generator = new Random();

        for (int i = 0; i < bf.getHeight(); i++) {
            for (int j = 0; j < bf.getWidth(); j++) {
                int x = j + generator.nextInt(R_SHUFFLE) - R_SHUFFLE / 2;
                int y = i + generator.nextInt(R_SHUFFLE) - R_SHUFFLE / 2;
                if (x < 0 || x > bf.getWidth() - 1 || y < 0 || y > bf.getHeight() - 1)
                    continue;
                res.setRGB(j, i, bf.getRGB(x, y));
            }
        }
        saveImage(path, "pixelShuffle", res);
    }

    private static void saveImage(String path, String name, BufferedImage res) throws IOException {

        File file = Path.of(path, name + ".png").toFile();
        ImageIO.write(res, "png", file);

    }

    private static void saveImage(String path, BufferedImage res) throws IOException {
        int n = 0;
        new File(CONFIG_FOLDER).mkdir(); // Ensures that the folder exists
        File f = new File(CONFIG_FOLDER, COUNT_FILE_STR);
        if (!f.exists() || f.isDirectory()) { // Create the counter file if it doesn't exist
            try (PrintWriter pw = new PrintWriter(new FileWriter(Path.of(CONFIG_FOLDER, COUNT_FILE_STR).toString()))) {
                pw.println(n + 1);
            }
        }
        // Save the image with the corresponding number
        Scanner sc = new Scanner(new FileInputStream(Path.of(CONFIG_FOLDER, COUNT_FILE_STR).toString()));
        n = sc.nextInt();
        File file = Path.of(path, n + ".png").toFile();
        ImageIO.write(res, "png", file);

        // Sum 1 to the counter file
        try (PrintWriter pw = new PrintWriter(new FileWriter(Path.of(CONFIG_FOLDER, COUNT_FILE_STR).toString()))) {
            pw.println(n + 1);
        }
    }

    // Doesn't work
    private static BufferedImage contour(BufferedImage bf) throws IOException {

        String method = "contour";
        File dir = Path.of(FILE_DEST_FOLDER + "/" + FILE_NAME, method).toFile();
        dir.mkdirs();
        BufferedImage res = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);

        String path = dir.getAbsolutePath();

        for (int i = 0; i < bf.getHeight(); i++) {
            for (int j = 0; j < bf.getWidth(); j++) {

                int newBlue = 0;
                int newGreen = 0;
                int newRed = 0;
                int n = 0;

                int color = bf.getRGB(j, i);
                int blue = color & 0xff;
                int green = (color & 0xff00) >> 8;
                int red = (color & 0xff0000) >> 16;

                for (int j2 = -1; j2 <= 1; j2++) {
                    for (int k = -1; k <= 1; k++) {
                        int x = j + k;
                        int y = i + j2;
                        if (x < 0 || x > bf.getWidth() - 1 || y < 0 || y > bf.getHeight() - 1)
                            continue;
                        color = bf.getRGB(x, y);
                        newBlue += Math.abs(blue - (color & 0xff));
                        newGreen += Math.abs((green - (color & 0xff00)) >> 8);
                        newRed += Math.abs((red - (color & 0xff0000)) >> 16);
                        n++;

                    }
                }
                res.setRGB(j, i, (((newRed / n) << 16) | ((newGreen / n) << 8) | (newBlue / n)));
            }
        }
        saveImage(path, res);

        return res;

    }
}