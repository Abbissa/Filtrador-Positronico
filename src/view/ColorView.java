package src.view;

import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
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
import javax.swing.text.JTextComponent;

import src.controller.Controller;

public class ColorView extends JToolBar {

    private static final long serialVersionUID = 1L;

    private Controller controller;
    private JPanel colorPanel;
    private JButton colorChooser;
    private JButton defaultValue;
    private Color color;
    private float[] hsb;
    private String bgMode;
    private JFormattedTextField n_colors;
    private JList<String> list;
    private JList<String> bgList;
    JTextComponent thresholdColorText;

    public JList<String> getBgList() {
        return bgList;
    }

    public void setBgList(JList<String> bgList) {
        this.bgList = bgList;
    }

    private JCheckBox checBox;

    private JCheckBox checBoxInvertir;

    public JCheckBox getChecBoxInvertir() {
        return checBoxInvertir;
    }

    public void setChecBoxInvertir(JCheckBox checBoxInvertir) {
        this.checBoxInvertir = checBoxInvertir;
    }

    public ColorView(Controller controller) {
        super();
        hsb = new float[3];
        this.controller = controller;

        setGL();

        nColores();
        JLabel thresholdColor = new JLabel("Threshold color:  ");
        thresholdColorText = new JFormattedTextField("20");

        this.add(thresholdColor);
        this.add(thresholdColorText);
        checBoxInvertir = new JCheckBox("Invertir colores");
        this.add(checBoxInvertir);

        colorMode();

        colorBG();

        colorPersonalizado();

        botonGenerarColores();

        this.add(new JLabel("Colores: "));
        this.colorPanel = new JPanel();
        GridLayout gl2 = new GridLayout();
        gl2.setColumns(4);

        colorPanel.setLayout(gl2);
        this.add(colorPanel);

    }

    private void botonGenerarColores() {
        Button boton = new Button("Generar");
        boton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Color[] colors = controller.generateColor(list.getSelectedValue(),
                        Integer.parseInt(n_colors.getText()), hsb, checBox.isSelected());

                controller.mode = list.getSelectedValue();
                controller.n_colors = Integer.parseInt(n_colors.getText());
                controller.colors = colors;
            }

        });
        this.add(boton);
    }

    private void colorPersonalizado() {
        checBox = new JCheckBox("Usar color personalizado");
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
    }

    private void colorBG() {
        String[] bgModes = { "color", "original", "DoG", "Color DoG" };
        bgList = new JList<String>(bgModes);
        bgList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JLabel bgMode = new JLabel("Background mode: ");
        this.add(bgMode);
        this.add(new JScrollPane(bgList));

        defaultValue = new JButton("Seleccionar para el fondo");

        defaultValue.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                color = JColorChooser.showDialog(null, "Selecciona  un color", Color.BLACK);
                defaultValue.setBackground(color);

            }
        });
        this.add(defaultValue);
    }

    private void colorMode() {
        String[] colorModes = { "DoG", "monochromatic", "complementary", "analagous", "triadic complementary",
                "tetradic complementary" };
        JLabel colorMode = new JLabel("Color mode: ");
        list = new JList<String>(colorModes);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(1);

        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                controller.mode = list.getSelectedValue();
            }
        });
        this.add(colorMode);
        this.add(new JScrollPane(list));
    }

    private void nColores() {
        JLabel n_colorsText = new JLabel("Nº colors: ");
        n_colors = new JFormattedTextField("8");

        this.add(n_colorsText);
        this.add(n_colors);
    }

    private void setGL() {
        GridLayout gl = new GridLayout(20, 2);
        gl.setHgap(10);
        gl.setVgap(10);
        this.setLayout(gl);

    }

    // Getters and setters
    public Container getColorPanel() {
        return this.colorPanel;
    }

    public void setColorPanel(JPanel colorPanel) {
        this.colorPanel = colorPanel;
    }

    public JButton getColorChooser() {
        return colorChooser;
    }

    public void setColorChooser(JButton colorChooser) {
        this.colorChooser = colorChooser;
    }

    public JButton getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(JButton defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float[] getHsb() {
        return hsb;
    }

    public void setHsb(float[] hsb) {
        this.hsb = hsb;
    }

    public String getBgMode() {
        return bgMode;
    }

    public void setBgMode(String bgMode) {
        this.bgMode = bgMode;
    }

    public JFormattedTextField getN_colors() {
        return n_colors;
    }

    public void setN_colors(JFormattedTextField n_colors) {
        this.n_colors = n_colors;
    }

    public JCheckBox getChecBox() {
        return checBox;
    }

    public void setChecBox(JCheckBox checBox) {
        this.checBox = checBox;
    }

    public JTextComponent getThresholdColorText() {
        return thresholdColorText;
    }

}
