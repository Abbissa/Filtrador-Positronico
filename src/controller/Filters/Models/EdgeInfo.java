package src.controller.Filters.Models;

public class EdgeInfo {

    private double E; // E = gx^2
    private double F; // F = gx * gy
    private double G; // G = gy^2
    private double angle;
    private double[] vectorOfLeastChange = new double[2];

    public EdgeInfo(double E, double F, double G, double angle) {
        this.E = E;
        this.F = F;
        this.G = G;
        this.angle = angle;
    }

    public EdgeInfo(double E, double F, double G, double angle, boolean calcVector) {
        this(E, F, G, angle);
        if (!calcVector) {
            return;
        }

        double lambda1 = (E + G) / 2 + Math.sqrt((E - G) * (E - G) / 4 + F * F);
        // // eigen vectors
        double[] v1 = { lambda1 - E, -F };
        vectorOfLeastChange[0] = v1[0];
        vectorOfLeastChange[1] = v1[1];
        if (v1[0] < 0) {
            vectorOfLeastChange[0] = -v1[0];
        }
        // if length of vector is 0, set to 1
        if (v1[0] == 0 && v1[1] == 0) {
            vectorOfLeastChange[0] = 0;
            vectorOfLeastChange[1] = 1;
        }
        // // normalize vector
        // double norm = Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1]);
        // if (norm != 0) {
        // v1[0] /= norm;
        // v1[1] /= norm;
        // }
        // if ((v1[0] > 0 && v1[0] < 0.5) || (v1[0] < 0 && v1[0] > -0.5))
        // v1[0] = 0;
        // if ((v1[1] > 0 && v1[1] < 0.5) || (v1[1] < 0 && v1[1] > -0.5))
        // v1[1] = 0;
        // if (v1[0] > 0.5)
        // v1[0] = 1;
        // if (v1[0] < -0.5)
        // v1[0] = -1;
        // if (v1[1] > 0.5)
        // v1[1] = 1;
        // if (v1[1] < -0.5)
        // v1[1] = -1;

    }

    public double getX() {
        return vectorOfLeastChange[0];
    }

    public double getY() {
        return vectorOfLeastChange[1];
    }

    public double getE() {
        return E;
    }

    public double getF() {
        return F;
    }

    public double getG() {
        return G;
    }

    public double getAngle() {
        return angle;
    }

    public void setE(double E) {
        this.E = E;
    }

    public void setF(double F) {
        this.F = F;
    }

    public void setG(double G) {
        this.G = G;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    // public double[] getVectorOfLeastChange() {
    // return vectorOfLeastChange;
    // }

    // public void setVectorOfLeastChange(double[] vectorOfLeastChange) {
    // this.vectorOfLeastChange = vectorOfLeastChange;
    // }

}
