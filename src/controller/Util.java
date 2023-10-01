package src.controller;

/*Copyright (c) 2020 Björn Ottosson
Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE. */

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Util {

    private static final String CONFIG_FOLDER = ".config";
    private static final String COUNT_FILE_STR = "n.txt";

    private static Util instance;

    private Util() {

    }

    public static Util getInstance() {
        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }

    public void saveImage(String path, String name, BufferedImage res) throws IOException {

        File file = Path.of(path, name + ".png").toFile();
        ImageIO.write(res, "png", file);

    }

    public void saveImage(String path, BufferedImage res) throws IOException {
        int n = 0;
        new File(CONFIG_FOLDER).mkdir(); // Ensures that the folder exists
        File f = new File(CONFIG_FOLDER, COUNT_FILE_STR);
        if (!f.exists() || f.isDirectory()) { // Create the counter file if it doesn't exist
            try (PrintWriter pw = new PrintWriter(new FileWriter(Path.of(CONFIG_FOLDER, COUNT_FILE_STR).toString()))) {
                pw.println(n + 1);
            }
        }
        // Save the image with the corresponding number
        Scanner sc = new Scanner(new FileInputStream(Path.of(CONFIG_FOLDER, COUNT_FILE_STR).toString()));
        n = sc.nextInt();
        File file = Path.of(path, n + ".png").toFile();
        ImageIO.write(res, "png", file);

        // Sum 1 to the counter file
        try (PrintWriter pw = new PrintWriter(new FileWriter(Path.of(CONFIG_FOLDER, COUNT_FILE_STR).toString()))) {
            pw.println(n + 1);
        }
    }

    public int getWeightedValue(double[][] weightedColor) {
        double sum = 0;
        for (int i = 0; i < weightedColor.length; i++) {
            for (int j = 0; j < weightedColor.length; j++) {
                sum += weightedColor[i][j];
            }
        }
        return (int) sum;
    }

    public Color[] generatePalettes(String HUE_MODE, int COLOR_COUNT, float[] color, boolean useCustomColor) {
        PaletteSettings paletteSettings = new PaletteSettings(COLOR_COUNT, color, useCustomColor);
        return generateOKLCH(HUE_MODE, paletteSettings);
    }

    public double Lerp(double min, double max, double t) {
        return min + (max - min) * t;
    }

    private Color[] generateOKLCH(String HUE_MODE, PaletteSettings settings) {
        Color[] oklchColors = new Color[settings.colorCount];

        double hueBase = settings.hueBase * 2 * Math.PI;
        double hueContrast = Lerp(0.33, 1.0, settings.hueContrast);

        double chromaBase = Lerp(0.01, 0.1, settings.saturationBase);
        double chromaContrast = Lerp(0.075, 0.125 - chromaBase, settings.saturationContrast);
        double chromaFixed = Lerp(0.01, 0.125, settings.fixed);

        double lightnessBase = Lerp(0.3, 0.6, settings.luminanceBase);
        double lightnessContrast = Lerp(0.3, 1.0 - lightnessBase, settings.luminanceContrast);
        double lightnessFixed = Lerp(0.6, 0.9, settings.fixed);

        boolean chromaConstant = settings.saturationConstant;
        boolean lightnessConstant = !chromaConstant;

        if (HUE_MODE == "monochromatic") {
            chromaConstant = false;
            lightnessConstant = false;
        }

        for (int i = 0; i < settings.colorCount; ++i) {
            double linearIterator = (i) / ((double) (settings.colorCount - 1));

            double hueOffset = linearIterator * hueContrast * 2 * Math.PI + (Math.PI / 4);

            if (HUE_MODE == "monochromatic")
                hueOffset *= 0.0;
            if (HUE_MODE == "analagous")
                hueOffset *= 0.25;
            if (HUE_MODE == "complementary")
                hueOffset *= 0.33;
            if (HUE_MODE == "triadic complementary")
                hueOffset *= 0.66;
            if (HUE_MODE == "tetradic complementary")
                hueOffset *= 0.75;

            if (HUE_MODE != "monochromatic")
                hueOffset += (Math.random() * 2 - 1) * 0.01;

            double chroma = chromaBase + linearIterator * chromaContrast;
            double lightness = lightnessBase + linearIterator * lightnessContrast;

            if (chromaConstant)
                chroma = chromaFixed;
            if (lightnessConstant)
                lightness = lightnessFixed;

            double[] lab = oklch_to_oklab(lightness, chroma, hueBase + hueOffset);
            double[] rgb = oklab_to_linear_srgb(lab[0], lab[1], lab[2]);

            rgb[0] = Math.round(Math.max(0.0, Math.min(rgb[0], 1.0)) * 255);
            rgb[1] = Math.round(Math.max(0.0, Math.min(rgb[1], 1.0)) * 255);
            rgb[2] = Math.round(Math.max(0.0, Math.min(rgb[2], 1.0)) * 255);

            oklchColors[i] = new Color((int) rgb[0], (int) rgb[1], (int) rgb[2]);
        }

        return oklchColors;
    }

    double[] oklch_to_oklab(double L, double c, double h) {
        double[] oklab = { L, (c * Math.cos(h)), (c * Math.sin(h)) };
        return oklab;
    }

    double[] oklab_to_linear_srgb(double L, double a, double b) {
        double l_ = L + 0.3963377774 * a + 0.2158037573 * b;
        double m_ = L - 0.1055613458 * a - 0.0638541728 * b;
        double s_ = L - 0.0894841775 * a - 1.2914855480 * b;

        double l = l_ * l_ * l_;
        double m = m_ * m_ * m_;
        double s = s_ * s_ * s_;
        double[] arr = {
                (+4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s),
                (-1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s),
                (-0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s),
        };
        return arr;
    }

    double[] linear_srgb_to_oklab(double[] c) {
        double l = 0.4122214708f * c[0] + 0.5363325363f * c[1] + 0.0514459929f * c[2];
        double m = 0.2119034982f * c[0] + 0.6806995451f * c[1] + 0.1073969566f * c[2];
        double s = 0.0883024619f * c[0] + 0.2817188376f * c[1] + 0.6299787005f * c[2];

        double l_ = Math.pow(l, 1.0 / 3.0);
        double m_ = Math.pow(m, 1.0 / 3.0);
        double s_ = Math.pow(s, 1.0 / 3.0);
        double[] arr = { 0.2104542553f * l_ + 0.7936177850f * m_ - 0.0040720468f * s_,
                1.9779984951f * l_ - 2.4285922050f * m_ + 0.4505937099f * s_,
                0.0259040371f * l_ + 0.7827717662f * m_ - 0.8086757660f * s_
        };
        return arr;
    }

    double[] oklab_to_oklch(double[] c) {
        double l = c[0];
        double a = c[1];
        double b = c[2];

        double c_ = Math.sqrt(a * a + b * b);
        double h = Math.atan2(b, a);
        if (h < 0.0)
            h += 2 * Math.PI;

        double[] arr = { l, c_, h };
        return arr;
    }

    private class PaletteSettings {

        public final double hueBase;
        public final double hueContrast;
        public final double saturationBase;
        public final double saturationContrast;
        final double luminanceBase;
        final double luminanceContrast;
        final double fixed;
        final boolean saturationConstant;
        final int colorCount;

        public PaletteSettings(int colorCount, float[] color, boolean useColor) {
            if (useColor) {

                this.hueBase = color[0];
                this.saturationBase = color[1];
                this.luminanceBase = color[2];
                this.hueContrast = Math.random();
                this.saturationContrast = Math.random();
                this.luminanceContrast = Math.random();
                this.fixed = Math.random();
                this.saturationConstant = true;
                this.colorCount = colorCount;
                return;
            }
            this.hueBase = Math.random();
            this.hueContrast = Math.random();
            this.saturationBase = Math.random();
            this.saturationContrast = Math.random();
            this.luminanceBase = Math.random();
            this.luminanceContrast = Math.random();
            this.fixed = Math.random();
            this.saturationConstant = true;
            this.colorCount = colorCount;

        }

    }
}
