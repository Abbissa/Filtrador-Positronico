package src.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JPanel;

import src.controller.Filters.FilterInterface;
import src.controller.Filters.ThreadFilter;
import src.view.GUI;

public class Controller {

    GUI gui;
    private static Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private int MAX_IMG_WIDTH = 1000;
    private int MAX_IMG_HEIGHT = 1000;
    private FilterInterface filter;

    public Controller(GUI gui) {
        this.filter = new ThreadFilter();
        this.gui = gui;
    }

    public String mode = "DoG";
    public int n_colors = 8;
    public Color[] colors;

    public void generateDoG() {
        Logger.getLogger(Controller.class.getName()).info("Generating DoG");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    double var = Float.parseFloat(gui.getBasicSettings().getVarianceText().getText());
                    double var_sca = Float.parseFloat(gui.getBasicSettings().getVariance_scalarText().getText());
                    int rad = gui.getBasicSettings().getRadiusInput().getValue();
                    double th = Float.parseFloat(gui.getBasicSettings().getThresholdText().getText());
                    double scalar = Float.parseFloat(gui.getBasicSettings().getScalarText().getText());
                    double phi = Float.parseFloat(gui.getBasicSettings().getPhiText().getText());
                    double thColor = Double.parseDouble(gui.getColorSettings().getThresholdColorText().getText());

                    String defaultValue = (String) gui.getColorSettings().getBgList().getSelectedItem();
                    Color bgColor = gui.getColorSettings().getColor();
                    boolean invertir = gui.getColorSettings().getChecBoxInvertir().isSelected();
                    Image loading = Toolkit.getDefaultToolkit().getImage("Loading.gif");
                    gui.getImagesViewer().setImageEdit(loading);

                    BufferedImage bf = javax.imageio.ImageIO.read(FileManager.getFILE());
                    BufferedImage res = null;
                    if (mode.equals("DoG"))
                        res = filter.DoG(bf, var, var_sca, rad, th, scalar, phi);
                    else
                        res = filter.DoGGradient(bf, var, var_sca, rad, th, thColor, scalar, phi, colors, defaultValue,
                                bgColor, invertir);
                    Image img = getImageResized(res, MAX_IMG_WIDTH, MAX_IMG_HEIGHT);

                    gui.getImagesViewer().setImageEdit(img);
                } catch (IOException e1) {

                    e1.printStackTrace();
                }
            }
        }).start();
    }

    public void chooseFile() {
        boolean fileFound = FileManager.chooseFile();
        if (fileFound) {
            FileManager.createDirs();

            try {
                BufferedImage res = javax.imageio.ImageIO.read(FileManager.getFILE());
                Image img = getImageResized(res, MAX_IMG_WIDTH, MAX_IMG_HEIGHT);

                gui.getImagesViewer().clearImageEdit();
                gui.getImagesViewer().setImageSrc(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveImage() throws NullPointerException {
        Image img = gui.getImagesViewer().getImageEdit();
        if (img != null) {
            FileManager.saveImage(img);
        }
        else {
            throw new NullPointerException("No image to save");
        }
    }

    private Image getImageResized(BufferedImage imgBuf, int maxWidth, int maxHeight) throws IOException {
        int width = maxWidth;
        int height = (width * imgBuf.getHeight()) / imgBuf.getWidth(); // (width / res.getWidth()) * res.getHeight() but
                                                                       // would lose precission
        if (height > maxHeight) {
            height = maxHeight;
            width = (height * imgBuf.getWidth()) / imgBuf.getHeight();
        }
        return imgBuf.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
    }

    public Color[] generateColor(String selectedValue, int parseInt, float[] color, boolean useCustomColor) {
        LOGGER.info("Generating colors");
        Util util = Util.getInstance();
        this.colors = util.generatePalettes(selectedValue, parseInt, color, useCustomColor);
        gui.getColorSettings().getColorPanel().removeAll();
        GridLayout gl2 = new GridLayout();
        gl2.setColumns(4);
        gl2.setRows(parseInt / 4);
        gui.getColorSettings().getColorPanel().setLayout(gl2);

        for (int i = 0; i < colors.length; i++) {
            JPanel panel = new JPanel();
            panel.setPreferredSize(new Dimension(25, 50));

            panel.setBackground(colors[i]);
            gui.getColorSettings().getColorPanel().add(panel);

        }
        LOGGER.info("Colors generated");
        gui.getColorSettings().updateUI();

        gui.repaint();
        LOGGER.info("Colors repainted");
        return colors;
    }

}
