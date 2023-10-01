package src.controller.Filters;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import src.controller.FileManager;
import src.controller.Util;

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
            double scalar, double phi) throws IOException {

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
                if (col < threshold) {
                    color = (int) (127.5 * (1 + Math.tanh(phi * (col - threshold))));

                }
                res.setRGB(j, i, new Color(color, color, color).getRGB());

            }
        }
        LOGGER.info("DoG done");
        String name = variance + "-" + variance_scalar + "-" + radius + "-" + threshold + "-" + scalar + "-" + phi;
        util.saveImage(path, name, res);

        return res;
    }

    public BufferedImage DoGGradient(BufferedImage bf,
            double variance, double variance_scalar, int radius, double threshold,
            double scalar, double phi, Color[] colors) throws IOException {
        BufferedImage[] imgs = get2GaussianBlur(bf, variance, variance_scalar, radius, threshold, scalar, phi);
        BufferedImage img1 = imgs[0];
        BufferedImage img2 = imgs[1];
        String method = "DoGGradient";
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

                if (col > threshold) {
                    res.setRGB(j, i, bf.getRGB(j, i));
                    continue;
                }
                int color = (int) (127.5 * (1 + Math.tanh(phi * (col - threshold))));
                int max = (int) Math.max(127.5 * (1 + Math.tanh(phi * (0 - threshold))),
                        127.5 * (1 + Math.tanh(phi * (0))));

                double band = max / colors.length;
                color = (int) (color / band);

                if (color == 0) {
                    res.setRGB(j, i, colors[0].getRGB());
                    if (col % band > band / 2 && Math.random() > 0.75)
                        res.setRGB(j, i, colors[1].getRGB());
                } else if (color >= colors.length - 1) {
                    res.setRGB(j, i, colors[colors.length - 1].getRGB());
                    if (col % band < band / 2 && Math.random() > 0.75)
                        res.setRGB(j, i, colors[colors.length - 2].getRGB());
                } else {
                    res.setRGB(j, i, colors[color].getRGB());
                    if (col % band < band / 3 && Math.random() > 0.75)
                        res.setRGB(j, i, colors[color - 1].getRGB());
                    if (col % band > 2 * (band / 3) && Math.random() > 0.75)
                        res.setRGB(j, i, colors[color + 1].getRGB());

                }

            }
        }
        LOGGER.info("DoGGradient done");
        String name = variance + "-" + radius;
        util.saveImage(path, name, res);
        return res;
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
