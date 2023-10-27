package src.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import src.controller.Controller;

public class GUI extends JFrame {

    private static final String APP_NAME = "Filtrador Positrónico";

    private Controller controller;

    private BasicSettings basicSettings;
    private ImagesViewer imagesViewer;

    private ColorSettings colorSettings;

    public GUI() {
        super(APP_NAME);
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.controller = new Controller(this);

        this.setLayout(new BorderLayout());

        this.basicSettings = new BasicSettings(controller);
        this.imagesViewer = new ImagesViewer();
        this.colorSettings = new ColorSettings(controller);
        colorSettings.setFloatable(false);

        this.add(basicSettings, BorderLayout.WEST);

        this.add(imagesViewer, BorderLayout.CENTER);

        this.add(colorSettings, BorderLayout.EAST);
    }

    public static void main(String[] args) {
        GUI v = new GUI();
        v.setVisible(true);
    }

    // Getters
    public BasicSettings getBasicSettings() {
        return basicSettings;
    }

    public ImagesViewer getImagesViewer() {
        return imagesViewer;
    }

    public ColorSettings getColorSettings() {
        return colorSettings;
    }
}
