
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

public class CreateImageFileFromGraphicsObject {

    private static final int N_DIFFS = 5;
    private static final int R_DIFF = 10;
    private static final int SALTO = 1;

    private static final String FILE = "imagen1.jpg";
    private static final String FILE_FOLDER = "sourceImg";
    private static final String FILE_DEST_FOLDER = "generatedImg";

    private static final int R_SHUFFLE = 50;

    private static final int BATCH_SIZE = 4;

    private static double variance = 0.6;
    private static double variance_scalar = 1.6;

    private static int radius = 10;

    // values around the midtone greyvalue of the greyvalue
    private static double threshold = 0.26 * 255; // controls the level above which luminance values will become white

    // values around 20
    private static double p = 16; // strength of edge sharpening

    // sensitive in low values, less sensitive with larger values
    private static double phi = 0.01; // sharpness of black and white transition

    public static void main(String[] args) throws IOException {
        String FILENAME = createDirs(FILE_DEST_FOLDER + "\\" + FILE);
        BufferedImage bf = javax.imageio.ImageIO.read(new File(FILE_FOLDER + "\\" + FILE));

        BufferedImage res = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);

        // blur1(bf, res);
        // blur2(bf, res);
        contour(bf, res);
        
        long init = System.nanoTime();
        BufferedImage a = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);
        gaussianBlur(bf, a, variance, radius);
        System.out.println((System.nanoTime() - init) / 1_000_000_000);

        saveImage(FILENAME, a);

        init = System.nanoTime();
        BufferedImage b = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);
        gaussianBlur(bf, b, variance * variance_scalar, radius);
        System.out.println((System.nanoTime() - init) / 1_000_000_000);

        saveImage(FILENAME, b);
        // difference(a, b, res, 100, 0.5, 0.01);
        // difference(a, b, res, 200, 0.5, 0.004);
        DoG(a, b, res, 200, 21, 0.04);
        saveImage(FILENAME, res);

        init = System.nanoTime();
        DoG(a, b, res, threshold, p, phi);

        System.out.println((System.nanoTime() - init) / 1_000_000_000);

        saveImage(FILENAME, res);

    }

    private static void gaussianBlur(BufferedImage bf, BufferedImage res, double variance, int radius) {

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

        System.out.println(sum);
        double max = 0;
        for (int i = 0; i < radius; i++) {
            for (int j = 0; j < radius; j++) {
                weights[i][j] /= sum;
                max = Math.max(max, weights[i][j]);
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

    private static void DoG(BufferedImage bf, BufferedImage res, BufferedImage res2, double threshold,
            double scalar, double phi) {
        for (int i = 0; i < bf.getHeight(); i++) {
            for (int j = 0; j < bf.getWidth(); j++) {

                int blue = (int) Math.abs((1 + scalar) * (bf.getRGB(j, i) & 0xff) - scalar * (res.getRGB(j, i) & 0xff));
                int green = (int) Math
                        .abs((1 + scalar) * ((bf.getRGB(j, i) & 0xff00) >> 8)
                                - scalar * ((res.getRGB(j, i) & 0xff00) >> 8));
                int red = (int) Math
                        .abs((1 + scalar) * ((bf.getRGB(j, i) & 0xff0000) >> 16)
                                - scalar * ((res.getRGB(j, i) & 0xff0000) >> 16));

                double col = red + blue + green;

                col /= 3;
                int color = 255;
                if (col < threshold) {
                    color = (int) (127.5 * (1 + Math.tanh(phi * (col - threshold))));

                }
                res2.setRGB(j, i, new Color(color, color, color).getRGB());

            }
        }
    }

    private static void colorDog(BufferedImage bf, BufferedImage res, BufferedImage res2, double threshold,
            double scalar, double phi) {
        for (int i = 0; i < bf.getHeight(); i++) {
            for (int j = 0; j < bf.getWidth(); j++) {

                int blue = (int) Math.abs((1 + scalar) * (bf.getRGB(j, i) & 0xff) - scalar * (res.getRGB(j, i) & 0xff));
                int green = (int) Math
                        .abs((1 + scalar) * ((bf.getRGB(j, i) & 0xff00) >> 8)
                                - scalar * ((res.getRGB(j, i) & 0xff00) >> 8));
                int red = (int) Math
                        .abs((1 + scalar) * ((bf.getRGB(j, i) & 0xff0000) >> 16)
                                - scalar * ((res.getRGB(j, i) & 0xff0000) >> 16));

                if (red < threshold)
                    red = (int) (127.5 * (1 + Math.tanh(phi * (red - threshold))));
                else
                    red = 255;
                if (green < threshold)
                    red = (int) (127.5 * (1 + Math.tanh(phi * (green - threshold))));
                else
                    green = 255;
                if (blue < threshold)
                    blue = (int) (127.5 * (1 + Math.tanh(phi * (blue - threshold))));
                else
                    blue = 255;

                res2.setRGB(j, i, new Color(red, green, blue).getRGB());

            }
        }

    }

    private static void differenceBW(BufferedImage bf, BufferedImage res, BufferedImage res2) {
        for (int i = 0; i < bf.getHeight(); i++) {
            for (int j = 0; j < bf.getWidth(); j++) {

                int blue = Math.abs(bf.getRGB(j, i) & 0xff - res.getRGB(j, i) & 0xff);
                int green = Math.abs(((bf.getRGB(j, i) & 0xff00) >> 8) - ((res.getRGB(j, i) & 0xff00) >> 8));
                int red = Math.abs(((bf.getRGB(j, i) & 0xff0000) >> 16) - ((res.getRGB(j, i) & 0xff0000) >> 16));

                int color = (red + green + blue) / 3;
                res2.setRGB(j, i, (color << 16 | color << 8 | color));

            }
        }
    }

    private static void pixelify(BufferedImage bf, BufferedImage res) {

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

    }

    private static void pixelMean(BufferedImage bf, BufferedImage res) {
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
    }

    private static void pixelShuffle(BufferedImage bf, BufferedImage res) {

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

    }

    private static String createDirs(String string) {
        String FILENAME = FILE.split("\\.")[0];
        File dir = new File(FILE_DEST_FOLDER + "\\" + FILENAME);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        dir = new File(FILE_DEST_FOLDER + "\\" + FILENAME + "\\diff");
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        return FILENAME;
    }

    private static void saveImage(String FILENAME, BufferedImage res) throws IOException {
        int n = 0;
        try (Scanner sc = new Scanner(new FileInputStream(".config\\n.txt"))) {
            n = sc.nextInt();
            File file = new File(FILE_DEST_FOLDER + "\\" +
                    FILENAME + "\\" + n + ".jpg");
            ImageIO.write(res, "jpg", file);

        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(".config\\n.txt"))) {
            pw.println(n + 1);
        }
    }

    // Doesn't work
    private static void contour(BufferedImage bf, BufferedImage res) {

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
                        newGreen += Math.abs(green - (color & 0xff00) >> 8);
                        newRed += Math.abs(red - ((color & 0xff0000) >> 16));
                        n++;

                    }
                }
                res.setRGB(j, i, ((newRed / n) << 16 | (newGreen / n) << 8 | newBlue / n));
            }
        }

    }
}