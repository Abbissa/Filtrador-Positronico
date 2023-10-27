package src.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import src.controller.Controller;
import src.controller.FileManager;

import java.awt.GridLayout;

public class ToolBar extends JToolBar {

    public JFormattedTextField getVarianceText() {
        return varianceText;
    }

    public void setVarianceText(JFormattedTextField varianceText) {
        this.varianceText = varianceText;
    }

    public JFormattedTextField getVariance_scalarText() {
        return variance_scalarText;
    }

    public void setVariance_scalarText(JFormattedTextField variance_scalarText) {
        this.variance_scalarText = variance_scalarText;
    }

    public JFormattedTextField getRadiusText() {
        return radiusText;
    }

    public void setRadiusText(JFormattedTextField radiusText) {
        this.radiusText = radiusText;
    }

    public JFormattedTextField getThresholdText() {
        return thresholdText;
    }

    public void setThresholdText(JFormattedTextField thresholdText) {
        this.thresholdText = thresholdText;
    }

    public JFormattedTextField getScalarText() {
        return scalarText;
    }

    public void setScalarText(JFormattedTextField scalarText) {
        this.scalarText = scalarText;
    }

    public JFormattedTextField getPhiText() {
        return phiText;
    }

    public void setPhiText(JFormattedTextField phiText) {
        this.phiText = phiText;
    }

    private static final long serialVersionUID = 1L;

    private static final Color DEFAULT_COLOR = new Color(255, 255, 230);

    private Controller controller;

    private JLabel variance;
    private JLabel variance_scalar;
    private JLabel radius;
    private JLabel threshold;
    private JLabel scalar;
    private JLabel phi;

    private JFormattedTextField varianceText;
    private JFormattedTextField variance_scalarText;
    private JFormattedTextField radiusText;
    private JFormattedTextField thresholdText;
    private JFormattedTextField scalarText;
    private JFormattedTextField phiText;

    public ToolBar(Controller controller) {
        this.setBackground(DEFAULT_COLOR);
        initTools();
        this.controller = controller;
    }

    private void initTools() {
        variance = new JLabel("Variance: ");
        variance_scalar = new JLabel("Variance scalar: ");
        radius = new JLabel("Radius: ");
        threshold = new JLabel("Threshold: ");
        scalar = new JLabel("Scalar: ");
        phi = new JLabel("Phi: ");

        varianceText = new JFormattedTextField("0.6");
        variance_scalarText = new JFormattedTextField("1.6");
        radiusText = new JFormattedTextField("10");
        thresholdText = new JFormattedTextField("66.3");
        scalarText = new JFormattedTextField("16");
        phiText = new JFormattedTextField("0.02");

        JButton elegirFichero = new JButton("Seleccionar imagen");
        JButton boton = new JButton("Generar");
        GridLayout gl = new GridLayout(12, 2);
        gl.setHgap(10);
        gl.setVgap(10);
        this.setLayout(gl);
        this.add(variance);
        this.add(varianceText);

        this.add(variance_scalar);
        this.add(variance_scalarText);

        this.add(radius);
        this.add(radiusText);

        this.add(threshold);
        this.add(thresholdText);

        this.add(scalar);
        this.add(scalarText);

        this.add(phi);
        this.add(phiText);

        this.add(elegirFichero);
        this.add(boton);
        JButton guardarImagen = new JButton("Guardar");
        this.add(guardarImagen);

        guardarImagen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.saveImage();
                } catch (NullPointerException e1) {
                    JFrame jFrame = new JFrame();
                    JOptionPane.showMessageDialog(jFrame, "No hay imagen para guardar", "Aviso",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        });
        elegirFichero.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.chooseFile();
            }

        });
        boton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (FileManager.getFILE() == null) {
                    JFrame jFrame = new JFrame();
                    JOptionPane.showMessageDialog(jFrame, "Seleccione una imagen antes de generar una nueva", "Aviso",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                controller.generateDoG();
            }

        });
    }
}