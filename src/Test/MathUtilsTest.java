package src.Test;

import java.util.ArrayList;
import java.util.List;

import src.Utils.MathUtils;

public class MathUtilsTest {

    public static void main(String[] args) {

        // list of 30 random numbers

        List<Double> numbers = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            numbers.add(Math.random() * 100); // random number between 0 and 10
        }
        double incrementalMean = 0;
        double incrementalVariance = 0;
        for (int i = 0; i < numbers.size(); i++) {
            incrementalMean = MathUtils.incrementalMean(incrementalMean, numbers.get(i), i + 1);
            incrementalVariance = MathUtils.incrementalVariance(incrementalMean, numbers.get(i),
                    incrementalVariance, i + 1);
            // calculate the mean of the numbers up to i
            double mean = MathUtils.mean(numbers.subList(0, i + 1));
            double variance = MathUtils.variance(numbers.subList(0, i + 1));
            // print incramental and normal mean and variance
            System.out.println("N: " + (i + 1) + " Number: " + numbers.get(i));
            System.out
                    .println(
                            "\tIncremental Mean: " + incrementalMean + " Incremental Variance: " + incrementalVariance);
            System.out.println("\tMean: " + mean + " Variance: " + variance);
        }
        // calculate the mean and variance of the list of numbers
        int acum = 0;
        for (int i = 0; i < numbers.size(); i++) {
            acum += numbers.get(i);

        }
        double mean = acum / numbers.size();
        double variance = 0;
        for (int i = 0; i < numbers.size(); i++) {
            variance += Math.pow(numbers.get(i) - mean, 2);
        }
        variance = variance / numbers.size() - 1;

        System.out.println("Mean: " + mean + " Variance: " + variance);
        System.out.println("Incremental Mean: " + incrementalMean + " Incremental Variance: " + incrementalVariance);
        // check if the incremental mean and variance are equal to the mean and variance
        // of the list of numbers
        if (Math.abs(incrementalMean - mean) < 0.0001 && Math.abs(incrementalVariance - variance) < 0.0001) {
            System.out.println("Test passed");
        } else {
            System.out.println("Test failed");
        }

    }
}
