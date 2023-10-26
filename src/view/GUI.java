package src.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import src.controller.Controller;

public class GUI extends JFrame {

    private static final String APP_NAME = "Filtrador Positrónico";

    private Controller controller;

    private ToolBar tools;
    private ImagesView iv;

    private ColorView cv;

    public ToolBar getTools() {
        return tools;
    }

    public void setTools(ToolBar tools) {
        this.tools = tools;
    }

    public ImagesView getIv() {
        return iv;
    }

    public void setIv(ImagesView iv) {
        this.iv = iv;
    }

    public ColorView getCv() {
        return cv;
    }

    public void setCv(ColorView cv) {
        this.cv = cv;
    }

    public GUI() {
        super(APP_NAME);
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.controller = new Controller(this);

        this.setLayout(new BorderLayout());

        this.tools = new ToolBar(controller);
        this.iv = new ImagesView();
        this.cv = new ColorView(controller);
        cv.setFloatable(false);
        this.add(cv, BorderLayout.EAST);

        this.add(tools, BorderLayout.WEST);

        this.add(iv, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        GUI v = new GUI();
        v.setVisible(true);
    }

}
