package src.controller.Filters;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface FilterInterface {

        public abstract BufferedImage DoG(BufferedImage bf,
                        double variance, double variance_scalar, int radius, double threshold,
                        double scalar, double phi, boolean invertir) throws IOException;

        public abstract BufferedImage DoGGradient(BufferedImage bf, double var, double var_sca, int rad, double th,
                        double thColor,
                        double scalar, double phi, Color[] colors, String defaultValue, Color color, boolean invertir)
                        throws IOException;

        public abstract BufferedImage badDoG(BufferedImage bf,
                        double variance, double variance_scalar, int radius, double threshold,
                        double scalar, double phi, boolean invertir) throws IOException;

        public abstract BufferedImage edgeTangentFlow(BufferedImage bf);

        public abstract BufferedImage kuwahara(BufferedImage bf, int radius) throws IOException;

        public abstract BufferedImage extendedDoG(BufferedImage bf) throws IOException;
}
