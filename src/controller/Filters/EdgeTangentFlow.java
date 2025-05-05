package src.controller.Filters;

import java.awt.Color;
import java.awt.image.BufferedImage;

import src.Utils.ColorUtil;
import src.controller.Filters.Models.EdgeInfo;

public class EdgeTangentFlow {

    public static BufferedImage edgeTangentFlow(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        calculateETF(img, width, height);
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        return output;
    }

    public static EdgeInfo[][] calculateETF(BufferedImage img, int width, int height) {
        EdgeInfo[][] etf = new EdgeInfo[width][height];

        // Sobel operators for gradient calculation
        double[][] kernel0 = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
        double[][] kernel1 = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double gx = 0, gy = 0;
                // Apply Sobel operator
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        // Check bounds
                        if (x + j < 0 || x + j >= width || y + i < 0 || y + i >= height) {
                            continue;
                        }
                        int rgb = img.getRGB(x + j, y + i);
                        double gray = ColorUtil.getGray(rgb);

                        gx += gray * kernel0[i + 1][j + 1];
                        gy += gray * kernel1[i + 1][j + 1];

                    }
                }
                gx *= 1.0 / (4.0);
                gy *= 1.0 / (4.0);

                // Calculate gradient magnitude and direction
                double angle = Math.atan2(gy, gx);
                etf[x][y] = new EdgeInfo(gx * gx, gx * gy, gy * gy, angle);

            }
        }
        return etf;
    }

    public static Color fromETFtoColor(EdgeInfo edgeInfo) {
        try {

            double magnitude = Math.sqrt(edgeInfo.getE() + edgeInfo.getG());
            double angle = edgeInfo.getAngle();

            int r = (int) (magnitude * Math.cos(angle));

            int g = (int) (magnitude * Math.sin(angle));
            int b = 0; // Assuming blue is not used in this case
            int a = 255; // Assuming full opacity

            // Clamp values to 0-255 range
            r = Math.max(0, Math.min(255, r));
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));
            a = Math.max(0, Math.min(255, a));

            return new Color(r, g, b, a);
        } catch (Exception e) {
            e.printStackTrace();
            return new Color(0, 0, 0, 255); // Default to black if an error occurs
        }
    }

    public static EdgeInfo fromColorToETF(Color color) {
        double r = color.getRed(); // r = magnitude * cos(angle)
        double g = color.getGreen(); // g = magnitude * sin(angle)
        double b = color.getBlue(); // Assuming blue is not used in this case
        double a = color.getAlpha(); // Assuming full opacity

        // r = magnitude * cos(angle)
        // g = magnitude * sin(angle)
        // cos(angle)/ sin(angle) = r/g
        double angle = Math.atan2(g, r); // angle = atan(g/r)
        double magnitude = r / Math.cos(angle); // magnitude = r/cos(angle)

        // obtain the magnitude and angle from the color original magnitude

        return new EdgeInfo(magnitude, angle, a, 0);
    }

    public static BufferedImage fromETFtoBufferedImage(EdgeInfo[][] etf, int width, int height) {
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                EdgeInfo edgeInfo = etf[x][y];
                Color col = EdgeTangentFlow.fromETFtoColor(edgeInfo);

                output.setRGB(x, y, col.getRGB());
            }
        }
        return output;

    }

}
