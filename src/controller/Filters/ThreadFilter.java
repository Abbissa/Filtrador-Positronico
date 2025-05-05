package src.controller.Filters;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import src.controller.FileManager;
import src.controller.Util;
import src.controller.Filters.Models.FilterInfo;

public class ThreadFilter implements FilterInterface {

    private static Logger LOGGER = Logger.getLogger(Filter.class.getName());

    private static int N_THREADS = 8;

    private static final Util util = Util.getInstance();

    private static BufferedImage gaussianBlur(BufferedImage bf, double variance, int radius)
            throws IOException {
        BufferedImage res = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);
        String method = "gaussianBlur";
        String path = FileManager.createDir(method);

        double[][] weights = constructWeights(variance, radius);

        Thread[] threads = new Thread[N_THREADS];
        int iterations = res.getWidth() * res.getHeight() / N_THREADS;
        int index = 0;
        for (int i = 0; i < N_THREADS - 1; i++) {
            threads[i] = new Thread(new GaussianBlurThread(bf, res, radius, index, iterations, weights));
            threads[i].start();
            index += iterations;
        }

        threads[N_THREADS - 1] = new Thread(new GaussianBlurThread(bf, res, radius, index,
                res.getWidth() * res.getHeight() - index - 1, weights));
        threads[N_THREADS - 1].start();
        for (int i = 0; i < N_THREADS; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String name = variance + "-" + radius;
        util.saveImage(path, name, res);
        return res;

    }

    public BufferedImage DoG(BufferedImage bf,
            double variance, double variance_scalar, int radius, double threshold,
            double scalar, double phi, boolean invertir) throws IOException {

        BufferedImage[] imgs = get2GaussianBlur(bf, variance, variance_scalar, radius, threshold, scalar, phi);

        BufferedImage img1 = imgs[0];
        BufferedImage img2 = imgs[1];
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
                color = calcularColor(threshold, phi, invertir, col, color);
                res.setRGB(j, i, new Color(color, color, color).getRGB());

            }
        }
        LOGGER.info("DoG done");
        String name = variance + "-" + variance_scalar + "-" + radius + "-" + threshold + "-" + scalar + "-" + phi;
        util.saveImage(path, name, res);

        return res;
    }

    public BufferedImage extendedDoG(BufferedImage bf) throws IOException {
        FilterInfo filterInfo = new FilterInfo();

        return ExtendedDog.applyFilter(bf, bf, filterInfo);
    }

    public BufferedImage badDoG(BufferedImage bf,
            double variance, double variance_scalar, int radius, double threshold,
            double scalar, double phi, boolean invertir) throws IOException {

        BufferedImage[] imgs = get2GaussianBlur(bf, variance, variance_scalar, radius, threshold, scalar, phi);
        int minRed = 255;
        int minGreen = 255;
        int minBlue = 255;
        int maxRed = 0;
        int maxGreen = 0;
        int maxBlue = 0;
        BufferedImage img1 = imgs[0];
        BufferedImage img2 = imgs[1];
        String method = "DoG";
        String path = FileManager.createDir(method);

        BufferedImage res = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < bf.getHeight(); i++) {
            for (int j = 0; j < bf.getWidth(); j++) {
                // System.out.println("i: " + i + " j: " + j);

                int blue = (int) Math
                        .abs((1 + scalar) * (img1.getRGB(j, i) & 0xff) - scalar * (img2.getRGB(j, i) & 0xff));
                int green = (int) Math
                        .abs((1 + scalar) * (img1.getRGB(j, i) >> 8 & 0xff)
                                - scalar * (img2.getRGB(j, i) >> 8 & 0xff));
                int red = (int) Math
                        .abs((1 + scalar) * (img1.getRGB(j, i) >> 16 & 0xff)
                                - scalar * (img2.getRGB(j, i) >> 16 & 0xff));
                // if (i == 6 && j == 282) {
                // System.out.println("blue: " + blue + " green: " + green + " red: " + red);
                // }(rgb )
                red = calcularColor(threshold, phi, invertir, red, red);
                green = calcularColor(threshold, phi, invertir, green, green);
                blue = calcularColor(threshold, phi, invertir, blue, blue);
                minRed = Math.min(minRed, red);
                minGreen = Math.min(minGreen, green);
                minBlue = Math.min(minBlue, blue);
                maxRed = Math.max(maxRed, red);
                maxGreen = Math.max(maxGreen, green);
                maxBlue = Math.max(maxBlue, blue);

                // normalize the colors
                red = Math.min(255, red);
                green = Math.min(255, green);
                blue = Math.min(255, blue);
                try {

                    res.setRGB(j, i, new Color(red, green, blue).getRGB());
                } catch (Exception e) {
                    System.out.println("a");
                }

            }
        }
        LOGGER.info("DoG done");
        String name = variance + "-" + variance_scalar + "-" + radius + "-" + threshold + "-" + scalar + "-" + phi;
        util.saveImage(path, name, res);
        System.out.println("minRed: " + minRed + " minGreen: " + minGreen + " minBlue: " + minBlue);
        System.out.println("maxRed: " + maxRed + " maxGreen: " + maxGreen + " maxBlue: " + maxBlue);

        return res;
    }

    private int calcularColor(double threshold, double phi, boolean invertir, double col, int color) {

        if (col < threshold) {
            color = (int) (127.5 * (1 + Math.tanh(phi * (col - threshold))));

        }
        if (invertir) {
            color = 255 - color;
        }
        return color;
    }

    public BufferedImage kuwahara(BufferedImage bf, int radius) {
        BufferedImage res = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < bf.getHeight(); i++) {
            System.out.println("Row: " + i);
            for (int j = 0; j < bf.getWidth(); j++) {
                ArrayList<ArrayList<Color>> sectorColors = new ArrayList<ArrayList<Color>>();

                for (int sector = 0; sector < 8; sector++) {
                    sectorColors.add(new ArrayList<Color>());
                }

                for (int j2 = -radius + 1; j2 < radius; j2++) {
                    for (int k = -radius + 1; k < radius; k++) {

                        int x = j + j2 - (radius / 2);
                        int y = i + k - (radius / 2);

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

                        for (int sector = 0; sector < 8; sector++) {

                            double lowerBoundary = (2 * sector) * Math.PI / 16;
                            double upperBoundary = (2 * sector + 2) * Math.PI / 16;
                            // calculate coordinates respective to the center of the image
                            double j2_ = j2;
                            double k_ = k;

                            double angle = ((Math.atan2(k_, j2_) + Math.PI) / 2);
                            if ((angle >= lowerBoundary && angle <= upperBoundary)
                                    || (angle % Math.PI >= lowerBoundary && angle
                                            % Math.PI <= upperBoundary)) {
                                // System.out.println("Coordenadas: \n\tX: " + j2_ + "\tY: " + k_ + "\tSector: "
                                // + sector
                                // + "\tAngle: " + angle + "\tLower: " + lowerBoundary + "\tUpper: "
                                // + upperBoundary);
                                sectorColors.get(sector).add(color);

                            }
                        }
                    }
                }
                // for (int sector = 0; sector < 8; sector++) {
                // System.out.println("Sector: " + sector + " Size: " +
                // sectorColors.get(sector).size());
                // }

                double sectorRedMeans[] = new double[8];
                double sectorGreenMeans[] = new double[8];
                double sectorBlueMeans[] = new double[8];

                for (int sector = 0; sector < 8; sector++) {
                    int acumRed = 0;
                    int acumGreen = 0;
                    int acumBlue = 0;
                    if (sectorColors.get(sector).size() > 0) {
                        for (int s = 0; s < sectorColors.get(sector).size(); s++) {
                            Color c = sectorColors.get(sector).get(s);
                            acumRed += c.getRed();
                            acumGreen += c.getGreen();
                            acumBlue += c.getBlue();
                        }
                        sectorRedMeans[sector] = acumRed / sectorColors.get(sector).size();
                        sectorGreenMeans[sector] = acumGreen / sectorColors.get(sector).size();
                        sectorBlueMeans[sector] = acumBlue / sectorColors.get(sector).size();
                    }
                }
                double sectorRedVariances[] = new double[8];
                double sectorGreenVariances[] = new double[8];
                double sectorBlueVariances[] = new double[8];
                // calculate the variance of the colors per sector
                for (int sector = 0; sector < 8; sector++) {
                    int acumRed = 0;
                    int acumGreen = 0;
                    int acumBlue = 0;
                    if (sectorColors.get(sector).size() > 0) {
                        for (int s = 0; s < sectorColors.get(sector).size(); s++) {
                            Color c = sectorColors.get(sector).get(s);
                            acumRed += Math.pow(c.getRed() - sectorRedMeans[sector], 2);
                            acumGreen += Math.pow(c.getGreen() - sectorGreenMeans[sector], 2);
                            acumBlue += Math.pow(c.getBlue() - sectorBlueMeans[sector], 2);

                        }
                        sectorRedVariances[sector] = acumRed / sectorColors.get(sector).size() - 1;
                        sectorGreenVariances[sector] = acumGreen / sectorColors.get(sector).size() - 1;
                        sectorBlueVariances[sector] = acumBlue / sectorColors.get(sector).size() - 1;
                    }

                }
                double red = 0;
                double green = 0;
                double blue = 0;
                double redWeight = 0;
                double greenWeight = 0;
                double blueWeight = 0;

                for (int sector = 0; sector < 8; sector++) {
                    red += sectorRedMeans[sector] * sectorRedVariances[sector];
                    green += sectorGreenMeans[sector] * sectorGreenVariances[sector];
                    blue += sectorBlueMeans[sector] * sectorBlueVariances[sector];
                    redWeight += sectorRedVariances[sector];
                    greenWeight += sectorGreenVariances[sector];
                    blueWeight += sectorBlueVariances[sector];

                }
                red /= redWeight;
                green /= greenWeight;
                blue /= blueWeight;
                int redInt = (int) Math.min(255, Math.max(0, red));
                int greenInt = (int) Math.min(255, Math.max(0, green));
                int blueInt = (int) Math.min(255, Math.max(0, blue));
                try {

                    res.setRGB(j, i, new Color(redInt, greenInt, blueInt).getRGB());

                } catch (Exception e) {
                    System.out.println(res.getWidth() + " " + res.getHeight());
                    System.out.println("i: " + i + " j: " + j + " red: " + redInt + " green: " + greenInt
                            + " blue: " + blueInt);
                    Logger.getLogger("Kuwahara").info("i: " + i + " j: " + j + " red: " + redInt + " green: "
                            + greenInt
                            + " blue: " + blueInt);
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
        LOGGER.info("Kuwahara done");

        return res;
    }

    public BufferedImage edgeTangentFlowV5(BufferedImage img) {
        return EdgeTangentFlow.edgeTangentFlow(img);
    }

    public BufferedImage edgeTangentFlow(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        double maxRed = 0;
        double maxGreen = 0;
        double minRed = 255;
        double minGreen = 255;
        BufferedImage etfImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Sobel operators for gradient calculation
        double[][] kernel0 = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
        double[][] kernel1 = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                double gx = 0, gy = 0;
                // Apply Sobel operator
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int rgb = img.getRGB(x + j, y + i);
                        double gray = getGray(rgb);

                        gx += gray * kernel0[i + 1][j + 1];
                        gy += gray * kernel1[i + 1][j + 1];

                    }
                }
                gx *= 1.0 / 4.0;
                gy *= 1.0 / 4.0;

                // Calculate gradient magnitude and direction
                double magnitude = Math.sqrt(gx * gx + gy * gy);

                double angle = Math.atan2(gy, gx);

                // calculate the edge tangent flow so that vertical changes are represented in
                // red and // horizontal changes in green
                // if (magnitude <= 40) {
                // continue;
                // }
                // if (magnitude < 1) {
                // continue;
                // }
                // normalize from values -1442.5 to 1442.5 to 0-255
                // magnitude = (magnitude + 360.625) * (255.0 / 360.625);
                double redMagnitude = magnitude * Math.abs(Math.cos(angle));
                double greenMagnitude = magnitude * Math.abs(Math.sin(angle));

                // normalize from values -1633 to 1633 to 0-255
                // redMagnitude = (redMagnitude + 1442.5) * (255.0 / 2885.0);
                // greenMagnitude = (greenMagnitude + 1442.5) * (255.0 / 2885.0);

                // normalize the colors
                maxRed = Math.max(maxRed, redMagnitude);
                maxGreen = Math.max(maxGreen, greenMagnitude);
                minRed = Math.min(minRed, redMagnitude);
                minGreen = Math.min(minGreen, greenMagnitude);
                redMagnitude = Math.min(255, redMagnitude);
                greenMagnitude = Math.min(255, greenMagnitude);

                try {

                    etfImage.setRGB(x, y,
                            new Color((int) redMagnitude, (int) greenMagnitude, (int) 0).getRGB());

                } catch (Exception e) {

                    // + greenMagnitude);

                    // LOGGER.info("i: " + x + " j: " + y + " red: " + redMagnitude + " green: "
                    // + greenMagnitude);
                }

            }
        }

        LOGGER.info("Edge Tangent Flow calculated");

        System.out.println("maxRed: " + maxRed + " maxGreen: " + maxGreen);
        System.out.println("minRed: " + minRed + " minGreen: " + minGreen);
        return etfImage;

    }

    public BufferedImage edgeTangentFlowV3(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        double maxRed = 0;
        double maxGreen = 0;
        double minRed = 255;
        double minGreen = 255;
        BufferedImage etfImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Sobel operators for gradient calculation
        double[][] kernel0 = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
        double[][] kernel1 = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                double gx = 0, gy = 0;
                // Apply Sobel operator
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int rgb = img.getRGB(x + j, y + i);
                        double gray = getGray(rgb);

                        gx += gray * kernel0[i + 1][j + 1];
                        gy += gray * kernel1[i + 1][j + 1];

                    }
                }

                // Calculate gradient magnitude and direction
                double magnitude = Math.sqrt(gx * gx + gy * gy);

                double angle = Math.atan2(gy, gx);

                // calculate the edge tangent flow so that vertical changes are represented in
                // red and // horizontal changes in green
                // if (magnitude <= 40) {
                // continue;
                // }
                // if (magnitude < 1) {
                // continue;
                // }
                double redMagnitude = magnitude * Math.cos(angle);
                double greenMagnitude = magnitude * Math.sin(angle);

                // normalize from values -1633 to 1633 to 0-255
                redMagnitude = (redMagnitude + 1442.5) * (255.0 / 2885.0);
                greenMagnitude = (greenMagnitude + 1442.5) * (255.0 / 2885.0);

                // normalize the colors
                maxRed = Math.max(maxRed, redMagnitude);
                maxGreen = Math.max(maxGreen, greenMagnitude);
                minRed = Math.min(minRed, redMagnitude);
                minGreen = Math.min(minGreen, greenMagnitude);
                redMagnitude = Math.min(255, redMagnitude);
                greenMagnitude = Math.min(255, greenMagnitude);

                try {

                    etfImage.setRGB(x, y,
                            new Color((int) redMagnitude, (int) greenMagnitude, (int) 0).getRGB());

                } catch (Exception e) {

                    // + greenMagnitude);

                    // LOGGER.info("i: " + x + " j: " + y + " red: " + redMagnitude + " green: "
                    // + greenMagnitude);
                }

            }
        }

        LOGGER.info("Edge Tangent Flow calculated");
        System.out.println("maxRed: " + maxRed + " maxGreen: " + maxGreen);
        System.out.println("minRed: " + minRed + " minGreen: " + minGreen);
        return etfImage;

    }

    private double getGray(int rgb) {
        // int rgb = img.getRGB(x + j, y + i);

        double gray = ((rgb >> 16 & 0xff) * 0.2126 + (rgb >> 8 & 0xff) * 0.7152f + (rgb & 0xff) * 0.0722f);
        return gray;
    }

    // calculate edge tangent flow of the image
    public BufferedImage edgeTangentFlowV2(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage etfImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Sobel operators for gradient calculation
        int[][] sobelX = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
        int[][] sobelY = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0, gy = 0;
                int redGx = 0, greenGx = 0, blueGx = 0;
                int redGy = 0, greenGy = 0, blueGy = 0;
                // Apply Sobel operator
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int rgb = img.getRGB(x + j, y + i);
                        int red = (rgb >> 16 & 0xff);
                        int green = (rgb >> 8 & 0xff);
                        int blue = (rgb & 0xff);

                        redGx += red * sobelX[i + 1][j + 1];
                        greenGx += green * sobelX[i + 1][j + 1];
                        blueGx += blue * sobelX[i + 1][j + 1];
                        redGy += red * sobelY[i + 1][j + 1];
                        greenGy += green * sobelY[i + 1][j + 1];
                        blueGy += blue * sobelY[i + 1][j + 1];

                        int gray = (red + green + blue) / 3;
                        gx += gray * sobelX[i + 1][j + 1];
                        gy += gray * sobelY[i + 1][j + 1];

                    }
                }

                // Calculate gradient magnitude and direction
                double magnitude = Math.sqrt(gx * gx + gy * gy);
                double angle = Math.atan2(gy, gx);

                double redMagnitude = Math.sqrt(redGx * redGx + redGy * redGy);
                double greenMagnitude = Math.sqrt(greenGx * greenGx + greenGy * greenGy);
                double blueMagnitude = Math.sqrt(blueGx * blueGx + blueGy * blueGy);

                // double redMagnitude = Math.abs(redGx * Math.cos(angle) + redGy *
                // Math.sin(angle));
                // double greenMagnitude = Math.abs(greenGx * Math.cos(angle) + greenGy *
                // Math.sin(angle));
                // double blueMagnitude = Math.abs(blueGx * Math.cos(angle) + blueGy *
                // Math.sin(angle));

                // normalize the colors
                redMagnitude = Math.min(255, redMagnitude);
                greenMagnitude = Math.min(255, greenMagnitude);
                blueMagnitude = Math.min(255, blueMagnitude);
                etfImage.setRGB(x, y,
                        new Color((int) redMagnitude, (int) greenMagnitude, (int) blueMagnitude).getRGB());

                // Normalize and map to grayscale
                // int intensity = (int) Math.min(255, magnitude);
                // etfImage.setRGB(x, y, new Color(intensity, intensity, intensity).getRGB());
            }
        }

        LOGGER.info("Edge Tangent Flow calculated");
        return etfImage;

    }

    public BufferedImage edgeTangentFlowV1(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage etfImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Sobel operators for gradient calculation
        int[][] sobelX = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
        int[][] sobelY = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0, gy = 0;
                int redGx = 0, greenGx = 0, blueGx = 0;
                int redGy = 0, greenGy = 0, blueGy = 0;
                // Apply Sobel operator
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int rgb = img.getRGB(x + j, y + i);
                        int red = (rgb >> 16 & 0xff);
                        int green = (rgb >> 8 & 0xff);
                        int blue = (rgb & 0xff);

                        redGx += red * sobelX[i + 1][j + 1];
                        greenGx += green * sobelX[i + 1][j + 1];
                        blueGx += blue * sobelX[i + 1][j + 1];
                        redGy += red * sobelY[i + 1][j + 1];
                        greenGy += green * sobelY[i + 1][j + 1];
                        blueGy += blue * sobelY[i + 1][j + 1];

                        int gray = (red + green + blue) / 3;
                        gx += gray * sobelX[i + 1][j + 1];
                        gy += gray * sobelY[i + 1][j + 1];

                    }
                }

                // Calculate gradient magnitude and direction
                double magnitude = Math.sqrt(gx * gx + gy * gy);
                double angle = Math.atan2(gy, gx);

                // Normalize and map to grayscale
                int intensity = (int) Math.min(255, magnitude);
                etfImage.setRGB(x, y, new Color(intensity, intensity, intensity).getRGB());
            }
        }

        LOGGER.info("Edge Tangent Flow calculated");
        return etfImage;

    }

    public BufferedImage DoGGradient(BufferedImage bf,
            double variance, double variance_scalar, int radius, double threshold, double thresholdColor,
            double scalar, double phi, Color[] colors, String defaultValue, Color bgColor, boolean invertir)
            throws IOException {
        BufferedImage[] imgs = get2GaussianBlur(bf, variance, variance_scalar, radius, threshold, scalar, phi);
        BufferedImage img1 = imgs[0];
        BufferedImage img2 = imgs[1];
        String method = "DoGGradient";
        String path = FileManager.createDir(method);
        BufferedImage res = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < img2.getHeight(); i++) {
            for (int j = 0; j < img2.getWidth(); j++) {

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
                boolean cond;
                if (invertir)
                    cond = col < thresholdColor;
                else
                    cond = col > thresholdColor;

                if (cond) {

                    switch (defaultValue) {
                        case "color":
                            res.setRGB(j, i, bgColor.getRGB());
                            continue;
                        case "DoG":
                            int color = (int) (127.5 * (1 + Math.tanh(phi * (col - threshold))));
                            res.setRGB(j, i, new Color(color, color, color).getRGB());
                            continue;
                        case "original":
                            res.setRGB(j, i, bf.getRGB(j, i));
                            continue;
                        case "Color DoG":
                            getBand(threshold, phi, colors, res, i, j, col);
                            continue;

                    }

                }

                getBand(threshold, phi, colors, res, i, j, col);

            }
        }
        LOGGER.info("DoGGradient done");
        String name = variance + "-" + radius;
        util.saveImage(path, name, res);
        util.saveImage(FileManager.createDirNueva(), "nueva", res);
        return res;
    }

    private void getBand(double threshold, double phi, Color[] colors, BufferedImage res, int i, int j, double col) {
        int color = (int) (127.5 * (1 + Math.tanh(phi * (col - threshold))));
        int max = (int) Math.max(127.5 * (1 + Math.tanh(phi * (-threshold))),
                127.5 * (1 + Math.tanh(0)));

        double band = max / colors.length;
        color = (int) (color / band);

        if (color == 0) {
            res.setRGB(j, i, colors[0].getRGB());

        } else if (color >= colors.length - 1) {
            res.setRGB(j, i, colors[colors.length - 1].getRGB());

        } else {
            res.setRGB(j, i, colors[color].getRGB());

        }
    }

    private BufferedImage[] get2GaussianBlur(BufferedImage bf,
            double variance, double variance_scalar, int radius, double threshold,
            double scalar, double phi) throws IOException {

        BufferedImage[] imgs = new BufferedImage[2];
        String method = "gaussianBlur";
        File i1 = new File(FileManager.getPath(method) + "/" + variance + "-" + radius + ".png");

        GenGaussBlur genGB = new GenGaussBlur(bf, variance, radius);
        Thread th = new Thread(genGB);

        boolean b1 = !i1.exists() || i1.isDirectory();
        if (b1)
            th.start();
        else
            imgs[0] = javax.imageio.ImageIO.read(i1);

        File i2 = new File(FileManager.getPath(method) + "/" + variance_scalar * variance + "-"
                + radius + ".png");
        GenGaussBlur genGB2 = new GenGaussBlur(bf, variance * variance_scalar, radius);
        Thread th2 = new Thread(genGB2);
        boolean b2 = !i2.exists() || i2.isDirectory();
        if (b2)
            th2.start();
        else
            imgs[1] = javax.imageio.ImageIO.read(i2);
        if (b1)
            try {
                th.join();
                imgs[0] = genGB.getImg();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        if (b2)
            try {
                th2.join();
                imgs[1] = genGB2.getImg();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        return imgs;
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

    private static double gaussianModel(int x, int y, double variance) {

        return (1 / (2 * Math.PI * Math.pow(variance, 2))
                * Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(variance, 2))));
    }

    private static class GenGaussBlur implements Runnable {
        BufferedImage img;
        BufferedImage bf;
        double variance;
        int radius;

        GenGaussBlur(BufferedImage bf, double variance, int radius) {
            this.bf = bf;
            this.variance = variance;
            this.radius = radius;
        }

        @Override
        public void run() {
            try {
                img = gaussianBlur(bf, variance, radius);
                LOGGER.info("Gaussian blur 1 done");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public BufferedImage getImg() {
            return img;
        }
    }

    private static class GaussianBlurThread implements Runnable {

        BufferedImage bf;
        BufferedImage res;
        int radius;
        int index;
        int iterations;

        private double[][] weights;

        public GaussianBlurThread(BufferedImage bf, BufferedImage res, int radius, int index,
                int iterations,
                double[][] weights) {

            this.bf = bf;
            this.res = res;
            this.radius = radius;
            this.index = index;
            this.iterations = iterations;
            this.weights = weights;
        }

        public void run() {
            int it = 0;
            while (it <= iterations) {
                double[][] reds = new double[radius][radius];
                double[][] greens = new double[radius][radius];
                double[][] blues = new double[radius][radius];
                int i = index - index / bf.getWidth() * bf.getWidth();
                int j = index / bf.getWidth();

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
                int red = util.getWeightedValue(reds);
                int green = util.getWeightedValue(greens);
                int blue = util.getWeightedValue(blues);

                res.setRGB(i, j, new Color(red, green, blue).getRGB());
                it++;
                index++;
            }
        }
    }

}
