package src.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import src.controller.Controller;

public class GUI extends JFrame {

    private Controller controller;

    private ToolBar tools;
    private ImagesView iv;

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
        super("Título de ventana");
        setSize(900, 550);
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
