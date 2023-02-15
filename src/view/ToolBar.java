package src.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import src.controller.Controller;
import src.controller.FileManager;

import java.awt.GridLayout;

public class ToolBar extends JToolBar {

    private static final long serialVersionUID = 1L;

    private static final double MIN_SPEED = 0.5;
    private static final double MAX_SPEED = 10;
    private static final double SPEED_EXP = 3;// Speed exponentiation in the slider
    private static final Color DEFAULT_COLOR = new Color(255, 255, 230);

    private JButton stepBack;
    private JButton step;
    private JButton clear;
    private JButton playPause;
    private JLabel speedLabel = new JLabel("Speed:");
    private JSlider speedSlider;
    private JButton openColorChooser;
    private JButton load;
    private JToggleButton selectionMode;
    private JButton save;

    private String playLabel = "PLAY";
    private String pauseLabel = "PAUSE";

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
        JFormattedTextField varianceText = new JFormattedTextField();
        JFormattedTextField variance_scalarText = new JFormattedTextField();
        JFormattedTextField radiusText = new JFormattedTextField();
        JFormattedTextField thresholdText = new JFormattedTextField();
        JFormattedTextField scalarText = new JFormattedTextField();
        JFormattedTextField phiText = new JFormattedTextField();

        JButton elegirFichero = new JButton("Seleccionar imagen");
        JButton boton = new JButton("Generar");
        GridLayout gl = new GridLayout(10, 2);
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

    public void onRunningUpdate(boolean running) {
        if (running) {
            playPause.setText(pauseLabel);
            step.setEnabled(false);
            clear.setEnabled(false);
            // speedSlider.setEnabled(false);
            load.setEnabled(false);
            selectionMode.setEnabled(false);

            selectionMode.setSelected(false);
        } else {
            playPause.setText(playLabel);
            step.setEnabled(true);
            clear.setEnabled(true);
            speedSlider.setEnabled(true);
            load.setEnabled(true);
            selectionMode.setEnabled(true);
        }
    }

    public void onSelectionMode(int mode) {
        if (mode == 0) {
            save.setVisible(false);
            selectionMode.setSelected(false);

        } else {
            selectionMode.setSelected(true);
            playPause.setEnabled(false);
            step.setEnabled(false);
            clear.setEnabled(false);
            speedSlider.setEnabled(false);
            load.setEnabled(false);

            if (mode == 3) {
                save.setVisible(true);
            } else {
                save.setVisible(false);
            }
        }
    }
}