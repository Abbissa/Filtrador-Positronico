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
                    double var = Float.parseFloat(gui.getTools().getVarianceText().getText());
                    double var_sca = Float.parseFloat(gui.getTools().getVariance_scalarText().getText());
                    int rad = Integer.parseInt(gui.getTools().getRadiusText().getText());
                    double th = Float.parseFloat(gui.getTools().getThresholdText().getText());
                    double scalar = Float.parseFloat(gui.getTools().getScalarText().getText());
                    double phi = Float.parseFloat(gui.getTools().getPhiText().getText());
                    double thColor = Double.parseDouble(gui.getCv().getThresholdColorText().getText());

                    String defaultValue = gui.getCv().getBgList().getSelectedValue();
                    Color bgColor = gui.getCv().getColor();
                    boolean invertir = gui.getCv().getChecBoxInvertir().isSelected();
                    Image loading = Toolkit.getDefaultToolkit().getImage("Loading.gif");
                    gui.getIv().setImageEdit(loading);

                    BufferedImage bf = javax.imageio.ImageIO.read(FileManager.getFILE());
                    BufferedImage res = null;
                    if (mode.equals("DoG"))
                        res = filter.DoG(bf, var, var_sca, rad, th, scalar, phi);
                    else
                        res = filter.DoGGradient(bf, var, var_sca, rad, th, thColor, scalar, phi, colors, defaultValue,
                                bgColor, invertir);
                    Image img = getImageResized(res, MAX_IMG_WIDTH, MAX_IMG_HEIGHT);

                    gui.getIv().setImageEdit(img);
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

                gui.getIv().clearImageEdit();
                gui.getIv().setImageSrc(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveImage() throws NullPointerException {
        Image img = gui.getIv().getImageEdit();
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
        gui.getCv().getColorPanel().removeAll();
        GridLayout gl2 = new GridLayout();
        gl2.setColumns(4);
        gl2.setRows(parseInt / 4);
        gui.getCv().getColorPanel().setLayout(gl2);

        for (int i = 0; i < colors.length; i++) {
            JPanel panel = new JPanel();
            panel.setPreferredSize(new Dimension(25, 50));

            panel.setBackground(colors[i]);
            gui.getCv().getColorPanel().add(panel);

        }
        LOGGER.info("Colors generated");
        gui.getCv().updateUI();

        gui.repaint();
        LOGGER.info("Colors repainted");
        return colors;
    }

}
