package src.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import src.controller.Controller;
import src.controller.FileManager;

import java.awt.GridLayout;

public class BasicSettings extends JToolBar {

    private static final long serialVersionUID = 1L;

    private static final Color DEFAULT_COLOR = new Color(255, 255, 230);

    private Controller controller;

    private JLabel variance = new JLabel("Variance: ");
    private JLabel variance_scalar = new JLabel("Variance scalar: ");
    private JLabel radius = new JLabel("Radius: ");
    private JLabel threshold = new JLabel("Threshold: ");
    private JLabel scalar = new JLabel("Scalar: ");
    private JLabel phi = new JLabel("Phi: ");

    //TODO: Convertir en sliders
    private JFormattedTextField varianceText = new JFormattedTextField("0.6");
    private JFormattedTextField variance_scalarText = new JFormattedTextField("1.6");
    //private JFormattedTextField radiusText = new JFormattedTextField("10");
    //Rango de 1 a 40, distribución lineal.
    private JSlider radiusInput = new JSlider(0, 40, 10);//Ejemplo de como se hace un slider
    private JFormattedTextField thresholdText = new JFormattedTextField("66.3");
    private JFormattedTextField scalarText = new JFormattedTextField("16");
    private JFormattedTextField phiText = new JFormattedTextField("0.02");

    private JButton elegirFichero = new JButton("Seleccionar imagen");
    private JButton generateImageButton = new JButton("Generar");
    private JButton guardarImagen = new JButton("Guardar");

    public BasicSettings(Controller controller) {
        this.setBackground(DEFAULT_COLOR);
        initTools();
        this.controller = controller;
    }

    private void initTools() {
        GridLayout grid = new GridLayout(9, 2);

        grid.setHgap(10);
        grid.setVgap(10);
        this.setLayout(grid);

        this.add(variance);
        this.add(varianceText);

        this.add(variance_scalar);
        this.add(variance_scalarText);

        this.add(radius);
        radiusInput.setPreferredSize(new Dimension(140, 50));
        radiusInput.setMajorTickSpacing(10);
        radiusInput.setMinorTickSpacing(2);
        radiusInput.setPaintTicks(true);
        radiusInput.setPaintLabels(true);
        this.add(radiusInput);

        this.add(threshold);
        this.add(thresholdText);

        this.add(scalar);
        this.add(scalarText);

        this.add(phi);
        this.add(phiText);

        this.add(elegirFichero);
        this.add(generateImageButton);
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
        generateImageButton.addActionListener(new ActionListener() {

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

    // Getters
    public JFormattedTextField getVarianceText() {
        return varianceText;
    }

    public JFormattedTextField getVariance_scalarText() {
        return variance_scalarText;
    }

    public JSlider getRadiusInput() {
        return radiusInput;
    }

    public JFormattedTextField getThresholdText() {
        return thresholdText;
    }

    public JFormattedTextField getScalarText() {
        return scalarText;
    }

    public JFormattedTextField getPhiText() {
        return phiText;
    }
}