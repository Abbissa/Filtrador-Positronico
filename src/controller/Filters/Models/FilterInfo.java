package src.controller.Filters.Models;

public class FilterInfo {

    private boolean useFlow = true; // Whether or not to use the flow difference of gaussians or not.
    private double sigmaC = 0.1f; // Adjust standard deviation for blurring of the structure tensor. values range
                                  // from
                                  // 0.0f to 5.0f.
    private double sigmaM = 60.5f; // Adjust standard deviation for smoothing of the flow difference of gaussians.
                                   // values
                                   // range from 0.0f to 20.0f.
    private double[] lineIntegralStepSize = { 1.0f, 1.0f }; // Increase distance between smoothing samples for more
                                                            // painterly visuals.
    private boolean calcDiffBeforeConvolving = true; // Calculate Difference Before Smoothing
    private double sigmaE = 0.1f; // Adjust the deviation of the color buffer gaussian blurring. values range
                                  // from 0.0f to 10.0f.
    private double k = 1.4f; // Adjust scale between gaussian blur passes for the color buffer. values range
                             // from 0.0f to 5.0f.
    private double p = 0.5f; // Adjust sharpness of the two gaussian blurs to bring out edge lines. values
                             // range from 0.0f
                             // to 1.0f.
    private boolean smoothEdges = true; // Whether or not to apply anti aliasing to the edges of the image.
    private double sigmaA = 2.0f; // Adjust standard deviation for gaussian blurring of edge lines.
    private double[] antiAliasStepSize = { 1.0f, 1.0f }; // Increase distance between smoothing samples for more
                                                         // painterly visuals.

    private int thresholding = 0; // No Threshold, Tanh, Quantization, Soft Quantization
    private int thresholds = 3; // Adjust number of allowed difference values.
    private double thresholdingValue = 60.0f; // Adjust value at which difference is clamped to white.
    private double thresholdingValue2 = 0.0f; // Adjust value at which difference is clamped to white.
    private double threshold = 20.0f; // Adjust value at which difference is clamped to white.
    private double phi = 1.0f; // Adjust curve of hyperbolic tangent.

    private double termStrength = 2f; // Adjust scale of difference of gaussians output. values range from 0.0f to
                                      // 5.0f.
    private int blendMode = 2; // No Blend, Interpolate, Two Point Interpolate
    private double minColor = 0.1f; // Set minimum color.
    private double maxColor = .5f; // Set maximum color.
    private double blendStrength = .7f; // Adjust strength of color blending. values range from 0.0f to 1.0f.

    public boolean isUseFlow() {
        return useFlow;
    }

    public void setUseFlow(boolean useFlow) {
        this.useFlow = useFlow;
    }

    public double getSigmaC() {
        return sigmaC;
    }

    public void setSigmaC(double sigmaC) {
        this.sigmaC = sigmaC;
    }

    public double getSigmaM() {
        return sigmaM;
    }

    public void setSigmaM(double sigmaM) {
        this.sigmaM = sigmaM;
    }

    public double[] getLineIntegralStepSize() {
        return lineIntegralStepSize;
    }

    public void setLineIntegralStepSize(double[] lineIntegralStepSize) {
        this.lineIntegralStepSize = lineIntegralStepSize;
    }

    public boolean isCalcDiffBeforeConvolving() {
        return calcDiffBeforeConvolving;
    }

    public void setCalcDiffBeforeConvolving(boolean calcDiffBeforeConvolving) {
        this.calcDiffBeforeConvolving = calcDiffBeforeConvolving;
    }

    public double getSigmaE() {
        return sigmaE;
    }

    public void setSigmaE(double sigmaE) {
        this.sigmaE = sigmaE;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public boolean isSmoothEdges() {
        return smoothEdges;
    }

    public void setSmoothEdges(boolean smoothEdges) {
        this.smoothEdges = smoothEdges;
    }

    public double getSigmaA() {
        return sigmaA;
    }

    public void setSigmaA(float sigmaA) {
        this.sigmaA = sigmaA;
    }

    public double[] getAntiAliasStepSize() {
        return antiAliasStepSize;
    }

    public void setAntiAliasStepSize(double[] antiAliasStepSize) {
        this.antiAliasStepSize = antiAliasStepSize;
    }

    public int getThresholding() {
        return thresholding;
    }

    public void setThresholding(int thresholding) {
        this.thresholding = thresholding;
    }

    public int getThresholds() {
        return thresholds;
    }

    public void setThresholds(int thresholds) {
        this.thresholds = thresholds;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public double getPhi() {
        return phi;
    }

    public void setPhi(float phi) {
        this.phi = phi;
    }

    public double getThresholdingValue() {
        return thresholdingValue;
    }

    public void setThresholdingValue(float thresholdingValue) {
        this.thresholdingValue = thresholdingValue;
    }

    public double getThresholdingValue2() {
        return thresholdingValue2;
    }

    public void setThresholdingValue2(float thresholdingValue2) {
        this.thresholdingValue2 = thresholdingValue2;
    }

    public double getTermStrength() {
        return termStrength;
    }

    public void setTermStrength(int termStrength) {
        this.termStrength = termStrength;
    }

    public int getBlendMode() {
        return blendMode;
    }

    public void setBlendMode(int blendMode) {
        this.blendMode = blendMode;
    }

    public double getMinColor() {
        return minColor;
    }

    public void setMinColor(double minColor) {
        this.minColor = minColor;
    }

    public double getMaxColor() {
        return maxColor;
    }

    public void setMaxColor(double maxColor) {
        this.maxColor = maxColor;
    }

    public double getBlendStrength() {
        return blendStrength;
    }

    public void setBlendStrength(float blendStrength) {
        this.blendStrength = blendStrength;
    }

    // uniform bool _UseFlow<ui_category_closed=true;ui_category="Edge Tangent Flow
    // Settings";ui_label="Use Flow";ui_tooltip="Whether or not to use the flow
    // difference of gaussians or not.";>=true;
    // uniform float _SigmaC<ui_category_closed=true;ui_category="Edge Tangent Flow
    // Settings";ui_min=0.0f;ui_max=5.0f;ui_label="Tangent Flow
    // Deviation";ui_type="slider";ui_tooltip="Adjust standard deviation for
    // blurring of the structure tensor.";>=2.0f;
    // uniform float _SigmaM<ui_category_closed=true;ui_category="Edge Tangent Flow
    // Settings";ui_min=0.0f;ui_max=20.0f;ui_label="Line Integral
    // Deviation";ui_type="slider";ui_tooltip="Adjust standard deviation for
    // smoothing of the flow difference of gaussians.";>=2.0f;
    // uniform float2
    // _LineIntegralStepSize<ui_category_closed=true;ui_category="Edge Tangent Flow
    // Settings";ui_label="Line Convolution Step
    // Sizes";ui_type="drag";ui_tooltip="Increase distance between smoothing samples
    // for more painterly visuals.";>=1.0f;
    // uniform bool
    // _CalcDiffBeforeConvolving<ui_category_closed=true;ui_category="Edge Tangent
    // Flow Settings";ui_label="Calculate Difference Before Smoothing";>=true;
    // uniform float _SigmaE<ui_category_closed=true;ui_category="Difference Of
    // Gaussians Settings";ui_min=0.0f;ui_max=10.0f;ui_label="Difference Of
    // Gaussians Deviation";ui_type="slider";ui_tooltip="Adjust the deviation of the
    // color buffer gaussian blurring.";>=2.0f;
    // uniform float _K<ui_category_closed=true;ui_category="Difference Of Gaussians
    // Settings";ui_min=0.0f;ui_max=5.0f;ui_label="Deviation
    // Scale";ui_type="drag";ui_tooltip="Adjust scale between gaussian blur passes
    // for the color buffer.";>=1.6f;
    // uniform float _P<ui_category_closed=true;ui_category="Difference Of Gaussians
    // Settings";ui_min=0.0f;ui_max=100.0f;ui_label="Sharpness";ui_type="slider";ui_tooltip="Adjust
    // sharpness of the two gaussian blurs to bring out edge lines.";>=1.0f;
    // uniform bool _SmoothEdges<ui_category_closed=true;ui_category="Anti Aliasing
    // Settings";ui_label="Smooth Edges";ui_tooltip="Whether or not to apply anti
    // aliasing to the edges of the image.";>=true;
    // uniform float _SigmaA<ui_category_closed=true;ui_category="Anti Aliasing
    // Settings";ui_min=0.0f;ui_max=10.0f;ui_label="Edge Smooth
    // Deviation";ui_type="slider";ui_tooltip="Adjust standard deviation for
    // gaussian blurring of edge lines.";>=2.0f;
    // uniform float2 _AntiAliasStepSize<ui_category_closed=true;ui_category="Anti
    // Aliasing Settings";ui_label="Edge Smoothing Step
    // Sizes";ui_type="drag";ui_tooltip="Increase distance between smoothing samples
    // for more painterly visuals.";>=1.0f;
    // uniform int _Thresholding<ui_category_closed=true;ui_category="Threshold
    // Settings";ui_type="combo";ui_label="Threshold Mode";ui_items="No
    // Threshold\0""Tanh\0""Quantization\0""Soft Quantization\0";>=0;
    // uniform int _Thresholds<ui_category_closed=true;ui_category="Threshold
    // Settings";ui_min=1;ui_max=16;ui_label="Quantizer
    // Step";ui_type="slider";ui_tooltip="Adjust number of allowed difference
    // values.";>=1;
    // uniform float _Threshold<ui_category_closed=true;ui_category="Threshold
    // Settings";ui_min=0.0f;ui_max=100.0f;ui_label="White
    // Point";ui_type="slider";ui_tooltip="Adjust value at which difference is
    // clamped to white.";>=20.0f;
    // uniform float _Phi<ui_category_closed=true;ui_category="Threshold
    // Settings";ui_min=0.0f;ui_max=10.0f;ui_label="Soft
    // Threshold";ui_type="slider";ui_tooltip="Adjust curve of hyperbolic
    // tangent.";>=1.0f;

    // uniform bool _EnableHatching<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_label="Use Hatching";ui_tooltip="Whether or not to render cross
    // hatching.";>=false;

    // uniform
    // int _HatchTexture<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_type="combo";ui_label="Hatch Texture";ui_items="No
    // Texture\0""Texture 1\0""Texture 2\0""Texture 3\0""Texture 4\0""Custom
    // Texture\0";>=1;

    // uniform bool _ColoredPencilEnabled<ui_category_closed=true;ui_category="Cross
    // Hatch Settings";ui_label="Colored Pencil";ui_tooltip="Color the hatch
    // lines.";>=false;

    // uniform
    // float _BrightnessOffset<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_min=0.0f;ui_max=1.0f;ui_label="Brightness";ui_type="drag";ui_tooltip="Adjusts
    // brightness of color pencil lines.";>=0.5f;

    // uniform
    // float _Saturation<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_min=0.0f;ui_max=5.0f;ui_label="Saturation";ui_type="drag";ui_tooltip="Adjusts
    // saturation of color pencil lines to bring out more color.";>=1.0f;

    // uniform
    // float _HatchRes1<ui_spacing=5.0f;ui_category_closed=true;ui_category="Cross
    // Hatch Settings";ui_min=0.0f;ui_max=5.0f;ui_label="First Hatch
    // Resolution";ui_type="drag";ui_tooltip="Adjust the size of the first hatch
    // layer texture resolution.";>=1.0f;

    // uniform
    // float _HatchRotation1<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_min=-180.0f;ui_max=180.0f;ui_label="First Hatch
    // Rotation";ui_type="slider";ui_tooltip="Adjust the rotation of the first hatch
    // layer texture resolution.";>=1.0f;

    // uniform bool
    // _UseLayer2<ui_spacing=5.0f;ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_label="Layer 2";>=false;

    // uniform
    // float _Threshold2<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_min=0.0f;ui_max=100.0f;ui_label="Second White
    // Point";ui_type="slider";ui_tooltip="Adjust the white point of the second
    // hatching layer.";>=1.0f;

    // uniform
    // float _HatchRes2<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_min=0.0f;ui_max=5.0f;ui_label="Second Hatch
    // Resolution";ui_type="drag";ui_tooltip="Adjust the size of the second hatch
    // layer texture resolution.";>=1.0f;

    // uniform
    // float _HatchRotation2<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_min=-180.0f;ui_max=180.0f;ui_label="Second Hatch
    // Rotation";ui_type="slider";ui_tooltip="Adjust the rotation of the second
    // hatch layer texture resolution.";>=1.0f;

    // uniform bool
    // _UseLayer3<ui_spacing=5.0f;ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_label="Layer 3";>=false;

    // uniform
    // float _Threshold3<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_min=0.0f;ui_max=100.0f;ui_label="Third White
    // Point";ui_type="slider";ui_tooltip="Adjust the white point of the third
    // hatching layer.";>=1.0f;

    // uniform
    // float _HatchRes3<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_min=0.0f;ui_max=5.0f;ui_label="Third Hatch
    // Resolution";ui_type="drag";ui_tooltip="Adjust the size of the third hatch
    // layer texture resolution.";>=1.0f;

    // uniform
    // float _HatchRotation3<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_min=-180.0f;ui_max=180.0f;ui_label="Third Hatch
    // Rotation";ui_type="slider";ui_tooltip="Adjust the rotation of the third hatch
    // layer texture resolution.";>=1.0f;

    // uniform bool
    // _UseLayer4<ui_spacing=5.0f;ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_label="Layer 4";>=false;

    // uniform
    // float _Threshold4<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_min=0.0f;ui_max=100.0f;ui_label="Fourth White
    // Point";ui_type="slider";ui_tooltip="Adjust the white point of the fourth
    // hatching layer.";>=1.0f;

    // uniform
    // float _HatchRes4<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_min=0.0f;ui_max=5.0f;ui_label="Fourth Hatch
    // Resolution";ui_type="drag";ui_tooltip="Adjust the size of the fourth hatch
    // layer texture resolution.";>=1.0f;

    // uniform
    // float _HatchRotation4<ui_category_closed=true;ui_category="Cross Hatch
    // Settings";ui_min=-180.0f;ui_max=180.0f;ui_label="Fourth Hatch
    // Rotation";ui_type="slider";ui_tooltip="Adjust the rotation of the fourth
    // hatch layer texture resolution.";>=1.0f;

    // uniform
    // float _TermStrength<ui_category_closed=true;ui_category="Blend
    // Settings";ui_min=0.0f;ui_max=5.0f;ui_label="Term
    // Strength";ui_type="drag";ui_tooltip="Adjust scale of difference of gaussians
    // output.";>=1;

    // uniform
    // int _BlendMode<ui_category_closed=true;ui_category="Blend
    // Settings";ui_type="combo";ui_label="Blend Mode";ui_items="No
    // Blend\0""Interpolate\0""Two Point Interpolate\0";>=0;

    // uniform float3 _MinColor<ui_category_closed=true;ui_category="Blend
    // Settings";ui_min=0.0f;ui_max=1.0f;ui_label="Min
    // Color";ui_type="color";ui_tooltip="Set minimum color.";>=0.0f;

    // uniform float3 _MaxColor<ui_category_closed=true;ui_category="Blend
    // Settings";ui_min=0.0f;ui_max=1.0f;ui_label="Max
    // Color";ui_type="color";ui_tooltip="Set maximum color.";>=1.0f;

    // uniform
    // float _BlendStrength<ui_category_closed=true;ui_category="Blend
    // Settings";ui_min=0.0f;ui_max=1.0f;ui_label="Blend
    // Strength";ui_type="drag";ui_tooltip="Adjust strength of color blending.";>=1;

}
