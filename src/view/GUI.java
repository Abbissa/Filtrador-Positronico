package src.view;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.text.MaskFormatter;

import src.controller.Controller;

import java.awt.image.BufferedImage;
import java.text.ParseException;

public class GUI extends JFrame {

    private Controller controller;
    

    private ToolBar tools;
    private ImagesView iv;

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

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

    public GUI() {
        super("Titulo de ventana");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.controller = new Controller(this);

        this.setLayout(new BorderLayout());

        this.tools = new ToolBar(controller);
        this.iv = new ImagesView();
        this.add(tools, BorderLayout.WEST);

        this.add(iv, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        GUI v = new GUI();
        v.setVisible(true);
    }

    
}
