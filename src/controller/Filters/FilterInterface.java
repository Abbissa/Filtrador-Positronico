package src.controller.Filters;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface FilterInterface {

    public abstract BufferedImage DoG(BufferedImage bf,
            double variance, double variance_scalar, int radius, double threshold,
            double scalar, double phi) throws IOException;

    public abstract BufferedImage DoGGradient(BufferedImage bf, double var, double var_sca, int rad, double th,
            double scalar, double phi, Color[] colors) throws IOException;
}
