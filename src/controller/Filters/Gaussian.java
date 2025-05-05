package src.controller.Filters;

public class Gaussian {

    public static double gaussian(double x, double y, double sigma) {
        return Math.exp(-(x * x + y * y) / (2 * sigma * sigma)) / (2 * Math.PI * sigma * sigma);
    }

    public static double gaussian(double x, double sigma) {
        return Math.exp(-(x * x) / (2 * sigma * sigma)) / (Math.sqrt(2 * Math.PI * sigma * sigma));
    }
}
