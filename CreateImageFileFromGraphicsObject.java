
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class CreateImageFileFromGraphicsObject {

    private static final int N_DIFFS = 5;
    private static final int R_DIFF = 5;
    private static final int SALTO = 1;

    private static final String FILE = "casa.jpeg";
    private static final String FILE_FOLDER = "sourceImg";
    private static final String FILE_DEST_FOLDER = "generatedImg";

    private static final boolean SHUFFLE = false;
    private static final int R_SHUFFLE = 50;

    private static final int BATCH_SIZE = 4;

    public static void main(String[] args) throws IOException {
        String FILENAME = createDirs(FILE_DEST_FOLDER + "\\" + FILE);
        BufferedImage bf = javax.imageio.ImageIO.read(new File(FILE_FOLDER + "\\" + FILE));

        BufferedImage res = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);

        blur1(bf, res);
        // blur2(bf, res);
        // contour(bf, res);
        saveImage(FILENAME, res);

        BufferedImage res2 = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);

        difference(bf, res, res2);

        saveImage(FILENAME + "\\diff", res2);

        differenceBW(bf, res, res2);

        saveImage(FILENAME + "\\diff", res2);

    }

    private static void difference(BufferedImage bf, BufferedImage res, BufferedImage res2) {
        for (int i = 0; i < bf.getHeight(); i++) {
            for (int j = 0; j < bf.getWidth(); j++) {

                int blue = Math.abs(bf.getRGB(j, i) & 0xff - res.getRGB(j, i) & 0xff);
                int green = Math.abs(((bf.getRGB(j, i) & 0xff00) >> 8) - ((res.getRGB(j, i) & 0xff00) >> 8));
                int red = Math.abs(((bf.getRGB(j, i) & 0xff0000) >> 16) - ((res.getRGB(j, i) & 0xff0000) >> 16));

                res2.setRGB(j, i, (red << 16 | green << 8 | blue));

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

    private static void blur2(BufferedImage bf, BufferedImage res) {

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

    private static void blur1(BufferedImage bf, BufferedImage res) {
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

    private static void shuffle(BufferedImage bf, BufferedImage res) {
        if (SHUFFLE) {
            Random generator = new Random();
            int n = R_SHUFFLE;

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