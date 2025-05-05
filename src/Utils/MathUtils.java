package src.Utils;

import java.util.List;

public class MathUtils {

    // example:
    // double mean = 7;
    // double newValue = 5;
    // int n = 30;
    // new mean = 7+(5 - 7) / 30 = 6.9333

    public static double incrementalMean(double mean, double newValue, int n) {
        return mean + (newValue - mean) / n;
    }

    // calculates the variance of a new value given the old mean and old variance
    public static double incrementalVariance(double mean, double newValue, double oldVariance, int n) {
        // n is the number of samples, not the number of values
        double variance = oldVariance;
        if (n > 2)
            variance = oldVariance * (n - 2);

        double value = variance + Math.pow(newValue - mean, 2);

        if (n > 2)
            return value / (n - 1);
        else if (n == 2)
            return value / n;
        else
            return value;

    }

    public static double mean(List<Double> values) {
        double sum = 0;
        for (int i = 0; i < values.size(); i++) {
            sum += values.get(i);
        }
        return sum / values.size();
    }

    public static double variance(List<Double> values) {
        double mean = mean(values);
        double sum = 0;
        for (int i = 0; i < values.size(); i++) {
            sum += Math.pow(values.get(i) - mean, 2);
        }
        return sum / (values.size() - 1);
    }

    // dot product of two vectors
    public static double dotProduct(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    public static int calcularColor(double threshold, double phi, boolean invertir, double col) {
        int color = 255;
        if (col < threshold) {
            color = (int) (127.5 * (1 + Math.tanh(phi * (col - threshold))));

        }
        if (invertir) {
            color = 255 - color;
        }
        return color;
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

}
