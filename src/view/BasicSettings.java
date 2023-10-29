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
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import src.controller.Controller;
import src.controller.FileManager;

import java.awt.GridLayout;

public class BasicSettings extends JToolBar {

    private static final long serialVersionUID = 1L;

    private static final Color DEFAULT_COLOR = new Color(255, 255, 230);

    private static final double VARIANCE_DEFAULT = 0.6;
    private static final double VARIANCE_SCALAR_DEFAULT = 1.6;
    private static final int RADIUS_DEFAULT = 10;
    private static final double THRESHOLD_DEFAULT = 66.3;
    private static final double SCALAR_DEFAULT = 16;
    private static final double PHI_DEFAULT = 0.02;

    private static final double VARIANCE_MIN = 0;
    private static final double VARIANCE_MAX = 10;
    private static final double VARIANCE_SCALAR_MIN = 0;
    private static final double VARIANCE_SCALAR_MAX = 100;
    private static final int RADIUS_MIN = 0;
    private static final int RADIUS_MAX = 40;
    private static final double THRESHOLD_MIN = 0;
    private static final double THRESHOLD_MAX = 256;
    private static final double SCALAR_MIN = 2;
    private static final double SCALAR_MAX = 100;
    private static final double PHI_MIN = -1;
    private static final double PHI_MAX = 1;

    //varianza, scalar de varianza, scalar y phi con distribución exponencial

    private Controller controller;

    private JLabel variance = new JLabel("Variance: ");
    private JLabel variance_scalar = new JLabel("Variance scalar: ");
    private JLabel radiusLabel = new JLabel("Radius: ");
    private JLabel threshold = new JLabel("Threshold: ");
    private JLabel scalar = new JLabel("Scalar: ");
    private JLabel phi = new JLabel("Phi: ");

    //TODO: Convertir en sliders
    private JFormattedTextField varianceText = new JFormattedTextField(String.valueOf(VARIANCE_DEFAULT));
    private JFormattedTextField variance_scalarText = new JFormattedTextField(String.valueOf(VARIANCE_SCALAR_DEFAULT));

    private JFormattedTextField radiusText = new JFormattedTextField(RADIUS_DEFAULT);
    private JSlider radiusInput = new JSlider(RADIUS_MIN, RADIUS_MAX, RADIUS_DEFAULT);//Ejemplo de como se hace un slider

    private JFormattedTextField thresholdText = new JFormattedTextField(String.valueOf(THRESHOLD_DEFAULT));
    private JFormattedTextField scalarText = new JFormattedTextField(String.valueOf(SCALAR_DEFAULT));
    private JFormattedTextField phiText = new JFormattedTextField(String.valueOf(PHI_DEFAULT));

    //Botones set default
    private JButton radiusSetDefaultButton = new JButton("Reset");

    //Botones generales
    private JButton elegirFichero = new JButton("Seleccionar imagen");
    private JButton generateImageButton = new JButton("Generar");
    private JButton guardarImagen = new JButton("Guardar");

    public BasicSettings(Controller controller) {
        this.setBackground(DEFAULT_COLOR);
        initTools();
        this.controller = controller;
    }

    private void initTools() {
        GridLayout grid = new GridLayout(0, 1);

        grid.setHgap(10);
        grid.setVgap(10);
        this.setLayout(grid);

        this.add(variance);
        this.add(varianceText);

        this.add(variance_scalar);
        this.add(variance_scalarText);

        initRadius();

        this.add(threshold);
        this.add(thresholdText);

        this.add(scalar);
        this.add(scalarText);

        this.add(phi);
        this.add(phiText);

        this.add(elegirFichero);
        this.add(generateImageButton);
        this.add(guardarImagen);

        initDefaultButtonsListeners();

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
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                controller.generateDoG();
            }

        });
    }

    private void initRadius() {
        JPanel radiusPanel = new JPanel();
        radiusPanel.setBackground(DEFAULT_COLOR);
        radiusPanel.setLayout(new GridLayout(1, 2));
        radiusPanel.add(radiusLabel);
        JPanel radiusSubPanel = new JPanel();
            radiusSubPanel.setBackground(DEFAULT_COLOR);
            radiusSubPanel.setLayout(new GridLayout(1, 0));
            radiusSubPanel.add(radiusText);
            radiusSubPanel.add(radiusSetDefaultButton);
        radiusPanel.add(radiusSubPanel);
        this.add(radiusPanel);

        radiusInput.setPreferredSize(new Dimension(300, 50));
        //radiusInput.setMajorTickSpacing(10);
        //radiusInput.setMinorTickSpacing(1);
        //radiusInput.setPaintTicks(true);
        //radiusInput.setPaintLabels(true);
        radiusInput.setBackground(DEFAULT_COLOR);

        this.add(radiusInput);

        //Listeners
        radiusText.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    radiusInput.setValue(Integer.parseInt(radiusText.getText()));
                }
                catch (NumberFormatException e1) {
                    JFrame jFrame = new JFrame();
                    JOptionPane.showMessageDialog(jFrame, "El radio debe ser un número entero", "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

        });

        radiusInput.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                radiusText.setText(String.valueOf(radiusInput.getValue()));
            }
        });
    }

    private void initDefaultButtonsListeners() {
        radiusSetDefaultButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setDefaultRadius();
            }

        });
    }

    // Getters y setters
    public JFormattedTextField getVarianceText() {
        return varianceText;
    }

    public JFormattedTextField getVariance_scalarText() {
        return variance_scalarText;
    }

    public int getRadiusValue() {
        return radiusInput.getValue();
    }

    public void setDefaultRadius() {
        //radiusText.setText(String.valueOf(RADIUS_DEFAULT));//No es necesario: el listener del slider (radiusInput) ya lo hace
        radiusInput.setValue(RADIUS_DEFAULT);
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