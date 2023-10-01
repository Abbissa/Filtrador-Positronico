package src.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import src.controller.Controller;
import src.controller.FileManager;

import java.awt.GridLayout;

public class ToolBar extends JToolBar {

    private static final long serialVersionUID = 1L;

    private static final Color DEFAULT_COLOR = new Color(255, 255, 230);

    private Controller controller;

    public ToolBar(Controller controller) {
        this.setBackground(DEFAULT_COLOR);
        initTools();
        this.controller = controller;
    }

    private void initTools() {
        JLabel variance = new JLabel("Variance: ");
        JLabel variance_scalar = new JLabel("Variance scalar: ");
        JLabel radius = new JLabel("Radius: ");
        JLabel threshold = new JLabel("Threshold: ");
        JLabel scalar = new JLabel("Scalar: ");
        JLabel phi = new JLabel("Phi: ");

        JFormattedTextField varianceText = new JFormattedTextField("0.6");
        JFormattedTextField variance_scalarText = new JFormattedTextField("1.6");
        JFormattedTextField radiusText = new JFormattedTextField("10");
        JFormattedTextField thresholdText = new JFormattedTextField("66.3");
        JFormattedTextField scalarText = new JFormattedTextField("16");
        JFormattedTextField phiText = new JFormattedTextField("0.02");

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

                Double var = Double.parseDouble(varianceText.getText());
                Double var_sca = Double.parseDouble(variance_scalarText.getText());
                Integer rad = Integer.parseInt(radiusText.getText());
                Double th = Double.parseDouble(thresholdText.getText());
                Double scalar = Double.parseDouble(scalarText.getText());
                Double phi = Double.parseDouble(phiText.getText());
                controller.generateDoG(var, var_sca, rad, th, scalar, phi);
            }

        });
    }
}