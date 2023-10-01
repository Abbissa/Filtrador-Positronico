package src.view;

import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import src.controller.Controller;

public class ColorView extends JToolBar {

    private Controller controller;
    private JPanel colorPanel;
    private JButton colorChooser;
    public float[] hsb;

    public ColorView(Controller controller) {
        super();
        hsb = new float[3];
        this.controller = controller;

        String[] colorModes = { "DoG", "monochromatic", "complementary", "analagous", "triadic complementary",
                "tetradic complementary" };
        JList<String> list = new JList(colorModes);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JFormattedTextField n_colorsText = new JFormattedTextField("8");
        JLabel colorMode = new JLabel("Color mode: ");
        JLabel n_colors = new JLabel("Nº colors: ");
        GridLayout gl = new GridLayout(12, 2);
        gl.setHgap(10);
        gl.setVgap(10);
        this.setPreferredSize(new Dimension(300, HEIGHT));
        this.setLayout(gl);
        this.add(n_colors);
        this.add(n_colorsText);
        list.setSelectedIndex(1);
        this.add(colorMode);
        this.add(new JScrollPane(list));
        JCheckBox checBox = new JCheckBox("Usar color personalizado");
        colorChooser = new JButton("Seleccionar color");

        colorChooser.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Color color = JColorChooser.showDialog(null, "Selecciona  un color", Color.BLACK);

                colorChooser.setBackground(color);
                Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
            }
        });
        this.add(checBox);

        this.add(colorChooser);

        JLabel label = new JLabel("Color seleccionado");
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(50, 50));
        panel.setBackground(Color.BLACK);
        Button boton = new Button("Generar");
        this.add(boton);
        boton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Color[] colors = controller.generateColor(list.getSelectedValue(),
                        Integer.parseInt(n_colorsText.getText()), hsb, checBox.isSelected());

                controller.mode = list.getSelectedValue();
                controller.n_colors = Integer.parseInt(n_colorsText.getText());
                controller.colors = colors;
            }

        });

        this.add(new JLabel());
        this.colorPanel = new JPanel();
        GridLayout gl2 = new GridLayout(2, 4);
        gl.setHgap(10);
        gl.setVgap(10);

        colorPanel.setLayout(gl2);
        this.add(colorPanel);

    }

    public Container getColorPanel() {
        return this.colorPanel;
    }
}
