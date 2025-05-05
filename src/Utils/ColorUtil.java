package src.Utils;

public class ColorUtil {

    public static double getGray(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return 0.299 * r + 0.587 * g + 0.114 * b;
    }

    public static double getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    public static double getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    public static double getBlue(int rgb) {
        return rgb & 0xFF;
    }

    public static int getRGB(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

}
