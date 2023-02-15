package src.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Image;
import java.awt.Toolkit;

import src.view.GUI;

import javax.swing.ImageIcon;

public class Controller {

    GUI gui;

    int MAX_IMG_WEIGTH = 450;
    int MAX_IMG_HEIGHT = 450;

    public Controller(GUI gui) {

        this.gui = gui;
    }

    public void generateDoG(double var, double var_sca, int rad, double th, double scalar, double phi) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Image loading = Toolkit.getDefaultToolkit().getImage("Loading.gif");
                    gui.getIv().getImageEdit().setIcon(new ImageIcon(loading));

                    BufferedImage bf = javax.imageio.ImageIO.read(FileManager.getFILE());
                    BufferedImage res = Filter.DoG(bf, var, var_sca, rad, th, scalar, phi);

                    Image img = getImageResized(res, MAX_IMG_WEIGTH, MAX_IMG_HEIGHT);

                    gui.getIv().getImageEdit().setIcon(new ImageIcon(img));
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }).start();
    }

    public void chooseFile() {
        FileManager.chooseFile();
        FileManager.createDirs();

        try {
            BufferedImage res = javax.imageio.ImageIO.read(FileManager.getFILE());
            Image img = getImageResized(res, MAX_IMG_WEIGTH, MAX_IMG_HEIGHT);

            gui.getIv().getImageSrc().setIcon(new ImageIcon(img));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

}
