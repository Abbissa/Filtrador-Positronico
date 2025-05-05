package src.controller.Filters;

import java.awt.Color;
import java.awt.image.BufferedImage;

import src.Utils.ColorUtil;
import src.Utils.MathUtils;
import src.controller.Filters.Models.EdgeInfo;
import src.controller.Filters.Models.FilterInfo;

public class ExtendedDog {

    public static BufferedImage applyFilter(BufferedImage image, BufferedImage edgeTangentFlowImage,
            FilterInfo filterInfo) {
        // calcularte ETF
        if (edgeTangentFlowImage == null) {
            edgeTangentFlowImage = image;
        }

        EdgeInfo[][] etf = EdgeTangentFlow.calculateETF(edgeTangentFlowImage,
                edgeTangentFlowImage.getWidth(), edgeTangentFlowImage.getHeight());

        // horizontal blur on etf image
        etf = calculateHorizontalBlurTFM(etf, image.getWidth(), image.getHeight(),
                filterInfo);
        // vertical blur on etf image
        etf = calculateVerticalBlurTFM(
                etf, image.getWidth(), image.getHeight(),
                filterInfo);

        // horizontal blur on original image using the flow or not
        double[][][] horizontalBlurredImage = calculateHorizontalBlur(image,
                image.getWidth(), image.getHeight(), etf, filterInfo);

        // vertical blur on original image using the flow or not
        BufferedImage vertivalImage = calculateVerticalBlur(horizontalBlurredImage,
                image.getWidth(), image.getHeight(), etf, filterInfo);
        // anti aliasing

        // color blending
        vertivalImage = colorBlend(image, vertivalImage, filterInfo);

        return vertivalImage;
    }

    // private static void calculateHorizontalBlur(EdgeInfo[][] etf, int width, int
    // height, FilterInfo filterInfo) {
    // int kernelRadius = (int) Math.max(1, Math.floor(filterInfo.getSigmaC() *
    // 2.45f));
    // float kernelSum = 0.0f;

    // float[] col = new float[3];

    // for (int x = -kernelRadius; x <= kernelRadius; ++x) {
    // float3 c = tex2D(DifferenceOfGaussians,
    // uv + float2(x, 0) * float2(BUFFER_RCP_WIDTH, BUFFER_RCP_HEIGHT)).rgb;
    // float gauss = gaussian(_SigmaC, x);

    // col += c * gauss;
    // kernelSum += gauss;
    // }

    // return float4(col / kernelSum, 1.0f);
    // }

    private static EdgeInfo[][] calculateHorizontalBlurTFM(EdgeInfo[][] etf, int width, int height,
            FilterInfo filterInfo) {
        int kernelRadius = (int) Math.max(1, Math.floor(filterInfo.getSigmaC() * 2.45f));
        EdgeInfo[][] output = new EdgeInfo[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float kernelSum = 0.0f;

                float[] col = new float[3];

                for (int _x = -kernelRadius; _x <= kernelRadius; ++_x) {
                    // Check bounds
                    if (x + _x < 0 || x + _x >= width) {
                        continue;
                    }
                    EdgeInfo edgeInfo = etf[x + _x][y]; // Assuming etf is a 2D array of EdgeInfo

                    double gauss = Gaussian.gaussian(_x, filterInfo.getSigmaC());

                    col[0] += edgeInfo.getE() * gauss;
                    col[1] += edgeInfo.getF() * gauss;
                    col[2] += edgeInfo.getG() * gauss;
                    kernelSum += gauss;
                }

                // Normalize the color values
                col[0] /= kernelSum;
                col[1] /= kernelSum;
                col[2] /= kernelSum;

                EdgeInfo newEdgeInfo = new EdgeInfo(col[0], col[1], col[2],
                        Math.atan2(Math.sqrt(col[2]), Math.sqrt(col[0])));
                output[x][y] = newEdgeInfo; // Store the new EdgeInfo in the output array
            }
        }
        return output;

    }

    private static EdgeInfo[][] calculateVerticalBlurTFM(EdgeInfo[][] etf, int width, int height,
            FilterInfo filterInfo) {
        int kernelRadius = (int) Math.max(1, Math.floor(filterInfo.getSigmaC() * 2.45f));
        EdgeInfo[][] output = new EdgeInfo[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float kernelSum = 0.0f;

                float[] col = new float[3];

                for (int _y = -kernelRadius; _y <= kernelRadius; ++_y) {
                    // Check bounds
                    if (y + _y < 0 || y + _y >= height) {
                        continue;
                    }
                    EdgeInfo edgeInfo = etf[x][y + _y]; // Assuming etf is a 2D array of EdgeInfo

                    double gauss = Gaussian.gaussian(_y, filterInfo.getSigmaC());

                    col[0] += edgeInfo.getE() * gauss;
                    col[1] += edgeInfo.getF() * gauss;
                    col[2] += edgeInfo.getG() * gauss;
                    kernelSum += gauss;
                }

                // Normalize the color values
                col[0] /= kernelSum;
                col[1] /= kernelSum;
                col[2] /= kernelSum;

                EdgeInfo newEdgeInfo = new EdgeInfo(col[0], col[1], col[2],
                        Math.atan2(Math.sqrt(col[2]), Math.sqrt(col[0])), true);
                output[x][y] = newEdgeInfo; // Store the new EdgeInfo in the output array
            }
        }
        return output;
    }

    private static double[][][] calculateHorizontalBlur(BufferedImage image, int width, int height, EdgeInfo[][] etf,
            FilterInfo filterInfo) {
        double[][][] output = new double[width][height][3];
        int kernelRadius = filterInfo.getSigmaE() * 2 > 1 ? (int) filterInfo.getSigmaE() * 2 : 1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float[] kernelSum = { 1.0f, 1.0f };
                if (filterInfo.isUseFlow()) {

                    calcularBlurHorizontalConFlow(image, width, etf, filterInfo, output, kernelRadius, y, x, kernelSum);
                } else {
                    calcularBlurHorizontalSinFlow(image, filterInfo, output, kernelRadius, y, x);
                }

            }
        }

        return output;
    }

    private static void calcularBlurHorizontalSinFlow(BufferedImage image, FilterInfo filterInfo,
            double[][][] output, int kernelRadius, int y, int x) {
        Color color = new Color(image.getRGB(x, y));
        double green = color.getRed();
        double red = color.getRed();
        double[] kernelSum = { 0, 0 };

        for (int _x = -kernelRadius; _x <= kernelRadius; ++_x) {
            // Check bounds
            if (x + _x < 0 || x + _x >= image.getWidth()) {
                continue;
            }
            double c = ColorUtil.getGray(new Color(image.getRGB(x + _x, y)).getRGB());
            double gauss1 = Gaussian.gaussian(_x, filterInfo.getSigmaE());
            double gauss2 = Gaussian.gaussian(_x, filterInfo.getSigmaE() * filterInfo.getK());

            red += c * gauss1;
            kernelSum[0] += gauss1;

            green += c * gauss2;
            kernelSum[1] += gauss2;

        }
        output[x][y][0] = red / kernelSum[0];
        output[x][y][1] = green / kernelSum[1];
        output[x][y][2] = 255;
    }

    private static void calcularBlurHorizontalConFlow(BufferedImage image, int width, EdgeInfo[][] etf,
            FilterInfo filterInfo,
            double[][][] output, int kernelRadius, int y, int x, float[] kernelSum) {

        EdgeInfo edgeInfo = etf[x][y];
        Color color = new Color(image.getRGB(x, y)); // Assuming image is a BufferedImage
        double green = color.getRed();
        double red = color.getRed();

        for (int _x = 0; _x <= kernelRadius; ++_x) {
            // Check bounds
            if (x + _x < 0 || x + _x >= width) {
                continue;
            }
            double gauss1 = Gaussian.gaussian(_x, filterInfo.getSigmaE());
            double gauss2 = Gaussian.gaussian(_x, filterInfo.getSigmaE() * filterInfo.getK());

            // get red
            double c1 = 0;
            if (x - _x >= 0) {
                kernelSum[0] += gauss1;
                kernelSum[1] += gauss2;
                c1 = ColorUtil.getGray(new Color(image.getRGB(x - _x, y)).getRGB());
            }
            double c2 = 0;
            if (x + _x < width) {
                kernelSum[0] += gauss1;
                kernelSum[1] += gauss2;
                c2 = ColorUtil.getGray(new Color(image.getRGB(x + _x, y)).getRGB());
            }

            red += (c1 + c2) * gauss1;
            green += (c1 + c2) * gauss2;

        }
        // Normalize the color values
        red /= kernelSum[0];
        green /= kernelSum[1];
        // set color
        double blue = (1 + filterInfo.getP()) * red - green * filterInfo.getP();
        output[x][y][0] = red;
        output[x][y][1] = green;
        output[x][y][2] = blue;
    }

    private static BufferedImage calculateVerticalBlur(double[][][] horizontalBlurredImage,
            int width, int height,
            EdgeInfo[][] etf, FilterInfo filterInfo) {
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int kernelRadius = filterInfo.getSigmaM() * 2 > 1 ? (int) filterInfo.getSigmaM() * 2 : 1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] rgb = horizontalBlurredImage[x][y];
                // Assuming image is a BufferedImage
                double D = 0;
                if (filterInfo.isUseFlow()) {
                    D = calcularBlurVerticalConFlow(horizontalBlurredImage, width, height, filterInfo, output,
                            kernelRadius,
                            y, x, rgb);
                } else {
                    D = calcularBlurVerticalSinFlow(horizontalBlurredImage, filterInfo, output, kernelRadius, y, x,
                            rgb);
                }

                D = Math.max(0, Math.min(255, D));
                D /= 255.0f;
                if (filterInfo.getThresholding() == 0) {
                    output.setRGB(x, y, new Color((int) D, (int) D, (int) D).getRGB());

                } else if (filterInfo.getThresholding() == 1) {
                    if (D >= filterInfo.getThresholdingValue()) {
                        D = MathUtils.calcularColor(filterInfo.getThresholdingValue(), filterInfo.getPhi(), false,
                                D);
                        output.setRGB(x, y, new Color((int) D, (int) D, (int) D).getRGB());
                    } else {
                        output.setRGB(x, y, new Color(0, 0, 0).getRGB());
                    }

                } else if (filterInfo.getThresholding() == 2) {
                    double a = 1.0f / filterInfo.getThresholds();
                    double b = filterInfo.getThreshold() / 100.0f;
                    double _x = D / 100.0f;

                    D = (_x >= b) ? 1
                            : a * Math.floor(
                                    (Math.pow(Math.abs(_x), filterInfo.getPhi()) - (a * b / 2.0f)) / (a * b) + 0.5f);
                }
                D *= 255.0f;
                D = Math.max(0, Math.min(255, D));

                output.setRGB(x, y, new Color((int) D, (int) D, (int) D).getRGB());

            }
        }

        return output;
    }

    private static double calcularBlurVerticalSinFlow(double[][][] horizontalBlurredImage, FilterInfo filterInfo,
            BufferedImage output, int kernelRadius, int y, int x, double[] rgb) {

        double red = 0;
        double green = 0;
        double[] kernelSum = { 0, 0 };

        for (int y_ = -kernelRadius; y_ <= kernelRadius; ++y_) {
            // Check bounds
            if (y + y_ < 0 || y + y_ >= horizontalBlurredImage[0].length) {
                continue;
            }
            double[] c = horizontalBlurredImage[x][y + y_];
            double gauss1 = Gaussian.gaussian(y_, filterInfo.getSigmaE());
            double gauss2 = Gaussian.gaussian(y_, filterInfo.getSigmaE() * filterInfo.getK());

            red += c[0] * gauss1;
            kernelSum[0] += gauss1;

            green += c[0] * gauss2;
            kernelSum[1] += gauss2;
        }
        // Normalize the color values
        red /= Math.max(1, kernelSum[0]);
        green /= Math.max(1, kernelSum[1]);
        // set color
        return (1 + filterInfo.getP()) * red - green * filterInfo.getP();

    }

    private static double calcularBlurVerticalConFlow(double[][][] horizontalBlurredImage, int width, int height,
            FilterInfo filterInfo,
            BufferedImage output, int kernelRadius, int y, int x, double[] rgb) {
        double D;
        double[] G = new double[2];

        if (filterInfo.isCalcDiffBeforeConvolving()) {
            G[0] = rgb[2];
            G[1] = 0;
        } else {
            G[0] = rgb[0];
            G[1] = rgb[1];
        }
        float[] w = { 1, 1 };

        double[] stepSize = filterInfo.getLineIntegralStepSize();

        for (int i = 0; i < kernelRadius; i++) {
            // Check bounds
            int x_ = x + (int) (stepSize[0] * i);
            int y_ = y + (int) (stepSize[1] * i);

            if (x_ < 0 || x_ >= width || y_ < 0 || y_ >= height) {
                continue;
            }
            double[] rgb_ = horizontalBlurredImage[x_][y_];
            double gauss1 = Gaussian.gaussian(i, filterInfo.getSigmaM());
            if (filterInfo.isCalcDiffBeforeConvolving()) {
                G[0] += rgb_[2] * gauss1;
                w[0] += gauss1;
            } else {
                double gauss2 = Gaussian.gaussian(i, filterInfo.getSigmaM() * filterInfo.getK());
                G[0] += rgb_[0] * gauss1;
                w[0] += gauss1;

                G[1] += rgb_[1] * gauss2;
                w[1] += gauss2;
            }

        }
        for (int i = 0; i < kernelRadius; i++) {
            // Check bounds
            int x_ = x - (int) (stepSize[0] * i);
            int y_ = y - (int) (stepSize[1] * i);

            if (x_ < 0 || x_ >= width || y_ < 0 || y_ >= height) {
                continue;
            }
            double[] rgb_ = horizontalBlurredImage[x_][y_];
            double gauss1 = Gaussian.gaussian(i, filterInfo.getSigmaM());
            if (filterInfo.isCalcDiffBeforeConvolving()) {
                G[0] += rgb_[2] * gauss1;
                w[0] += gauss1;
            } else {
                double gauss2 = Gaussian.gaussian(i, filterInfo.getSigmaM() * filterInfo.getK());
                G[0] += rgb_[0] * gauss1;
                w[0] += gauss1;

                G[1] += rgb_[1] * gauss2;
                w[1] += gauss2;
            }

        }

        // Normalize the color values
        G[0] /= Math.max(1, w[0]);
        G[1] /= Math.max(1, w[1]);

        if (filterInfo.isCalcDiffBeforeConvolving()) {
            D = G[0];
        } else {
            D = (1 + filterInfo.getP()) * (G[0]) - filterInfo.getP() * (G[1]);
        }
        return D;
    }

    private static BufferedImage colorBlend(BufferedImage orig, BufferedImage vertivalImage, FilterInfo filterInfo) {
        int blendMode = filterInfo.getBlendMode();
        double blendStrength = filterInfo.getBlendStrength();
        double minColor = filterInfo.getMinColor();
        double maxColor = filterInfo.getMaxColor();

        BufferedImage output = new BufferedImage(vertivalImage.getWidth(), vertivalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < vertivalImage.getHeight(); y++) {
            for (int x = 0; x < vertivalImage.getWidth(); x++) {
                double colOrig = ColorUtil.getGray(new Color(orig.getRGB(x, y)).getRGB());
                double D = new Color(vertivalImage.getRGB(x, y)).getRed() / 255.0f * filterInfo.getTermStrength();
                double output_ = 0.0f;
                if (blendMode == 0)
                    output_ = MathUtils.lerp(minColor, maxColor, D);
                if (blendMode == 1)
                    output_ = MathUtils.lerp(minColor, colOrig, D);
                if (blendMode == 2) {
                    if (D < 0.5f)
                        output_ = MathUtils.lerp(minColor, colOrig, D * 2.0f);
                    else
                        output_ = MathUtils.lerp(colOrig, maxColor, (D - 0.5f) * 2.0f);
                }

                output_ = MathUtils.lerp(colOrig, output_, blendStrength);
                output_ *= 255.0f;
                output_ = Math.max(0, Math.min(255, output_));
                output.setRGB(x, y, new Color((int) output_, (int) output_, (int) output_).getRGB());
            }
        }
        return output;
    }

}
