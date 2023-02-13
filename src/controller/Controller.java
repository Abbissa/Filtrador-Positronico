package src.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Image;

import src.view.GUI;

import javax.swing.ImageIcon;

public class Controller {

    GUI gui;

    public Controller(GUI gui) {

        this.gui = gui;
    }

    public void generateDoG(double var, double var_sca, int rad, double th, double scalar, double phi) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedImage bf;
                BufferedImage res;
                try {

                    bf = javax.imageio.ImageIO.read(FileManager.getFILE());
                    res = Filter.DoG(bf, var, var_sca, rad, th, scalar, phi);
                    Image image = res;
                    if (res.getWidth() > 1200 || res.getHeight() > 1200)
                        image = res.getScaledInstance(res.getWidth() / 2, res.getHeight() / 2,
                                BufferedImage.SCALE_SMOOTH);

                    gui.getIv().getImageEdit().setIcon(new ImageIcon(image));

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
        Image img;
        BufferedImage res;
        try {
            res = javax.imageio.ImageIO.read(FileManager.getFILE());
            img = res;
            if (res.getWidth() > 1200 || res.getHeight() > 1200)
                img = res.getScaledInstance(res.getWidth() / 2, res.getHeight() / 2,
                        BufferedImage.SCALE_SMOOTH);

            gui.getIv().getImageSrc().setIcon(new ImageIcon(img));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
