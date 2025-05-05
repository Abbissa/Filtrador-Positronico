package src.view;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

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

    private static final int DECIMALS = 2;// Number of decimals used for double values

    // varianza, scalar de varianza, scalar y phi con distribución exponencial

    private Controller controller;

    private JLabel variance = new JLabel("Variance: ");
    private JLabel variance_scalar = new JLabel("Variance scalar: ");
    private JLabel radiusLabel = new JLabel("Radius: ");
    private JLabel thresholdLabel = new JLabel("Threshold: ");
    private JLabel scalar = new JLabel("Scalar: ");
    private JLabel phi = new JLabel("Phi: ");

    // TODO: Convertir en sliders
    private JFormattedTextField varianceText = new JFormattedTextField(String.valueOf(VARIANCE_DEFAULT));// Exponencial
    private JFormattedTextField variance_scalarText = new JFormattedTextField(String.valueOf(VARIANCE_SCALAR_DEFAULT));// Exponencial

    private JLabel radiusValue = new JLabel(String.valueOf(RADIUS_DEFAULT));
    private JSlider radiusSlider = new JSlider(RADIUS_MIN, RADIUS_MAX, RADIUS_DEFAULT);

    private JLabel thresholdValue = new JLabel(String.valueOf(THRESHOLD_DEFAULT));
    private JSlider thresholdSlider = new JSlider((int) THRESHOLD_MIN * (int) Math.pow(10, DECIMALS),
            (int) THRESHOLD_MAX * (int) Math.pow(10, DECIMALS), (int) THRESHOLD_DEFAULT * (int) Math.pow(10, DECIMALS));

    private JFormattedTextField scalarText = new JFormattedTextField(String.valueOf(SCALAR_DEFAULT));// Exponencial
    private JFormattedTextField phiText = new JFormattedTextField(String.valueOf(PHI_DEFAULT));// Exponencial

    // Botones set default
    private JButton radiusSetDefaultButton = new JButton("Reset");
    private JButton thresholdSetDefaultButton = new JButton("Reset");

    private JCheckBox invertir = new JCheckBox("Invertir Perro");
    // Botones generales
    private JButton elegirFichero = new JButton("Seleccionar imagen");
    private JButton generateImageButton = new JButton("Generar");
    private JButton guardarImagen = new JButton("Guardar");

    public BasicSettings(Controller controller) {
        this.setBackground(DEFAULT_COLOR);
        initTools();
        this.controller = controller;
    }

    private void initTools() {
        this.setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS)); // Use BoxLayout for vertical
                                                                                       // alignment
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the panel

        addField(variance, varianceText);
        addField(variance_scalar, variance_scalarText);
        initFieldWithSlider(radiusLabel, radiusValue, radiusSlider, radiusSetDefaultButton, RADIUS_DEFAULT, RADIUS_MIN,
                RADIUS_MAX, true);
        initFieldWithSlider(thresholdLabel, thresholdValue, thresholdSlider, thresholdSetDefaultButton,
                (int) (THRESHOLD_DEFAULT * Math.pow(10, DECIMALS)),
                (int) (THRESHOLD_MIN * Math.pow(10, DECIMALS)),
                (int) (THRESHOLD_MAX * Math.pow(10, DECIMALS)), false);
        addField(scalar, scalarText);
        addField(phi, phiText);
        initDefaultButtonsListeners();
        this.add(javax.swing.Box.createVerticalStrut(10)); // Add vertical space
        this.add(invertir);
        initGeneralButtons();
    }

    private void addField(JLabel label, JFormattedTextField field) {
        JPanel variancePanel = new JPanel();
        variancePanel.setLayout(new GridLayout(1, 2, 5, 0)); // Horizontal layout with spacing
        variancePanel.setBackground(DEFAULT_COLOR);
        variancePanel.setPreferredSize(new Dimension(200, 30)); // Fixed size
        variancePanel.setMaximumSize(new Dimension(200, 30));
        variancePanel.add(label);
        variancePanel.add(field);
        variancePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Add vertical spacing
        this.add(variancePanel);
    }

    private void initFieldWithSlider(JLabel label, JLabel textField, JSlider slider, JButton resetButton,
            int defaultValue, int minValue, int maxValue, boolean isInteger) {
        JPanel panel = new JPanel();
        panel.setBackground(DEFAULT_COLOR);
        panel.setLayout(new GridLayout(1, 2, 5, 0));
        panel.setPreferredSize(new Dimension(200, 30)); // Fixed size
        panel.setMaximumSize(new Dimension(200, 30));
        panel.setBackground(DEFAULT_COLOR);

        panel.add(label);

        JPanel subPanel = new JPanel();
        subPanel.setBackground(DEFAULT_COLOR);
        subPanel.setLayout(new GridLayout(1, 2, 5, 0));
        subPanel.setPreferredSize(new Dimension(200, 30)); // Fixed size
        subPanel.setMaximumSize(new Dimension(200, 30));
        subPanel.setBackground(DEFAULT_COLOR);
        textField.setPreferredSize(new Dimension(30, 30)); // Fixed size
        textField.setMaximumSize(new Dimension(30, 30));
        textField.setBackground(DEFAULT_COLOR);

        subPanel.add(textField);
        resetButton.setPreferredSize(new Dimension(180, 30)); // Fixed size
        resetButton.setMaximumSize(new Dimension(180, 30));
        resetButton.setText("Reset");
        resetButton.setFocusable(false);
        resetButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        resetButton.setSize(new Dimension(180, 30)); // Fixed size
        subPanel.add(resetButton);
        panel.add(subPanel);
        this.add(panel);

        slider.setMinimum(minValue);
        slider.setMaximum(maxValue);
        slider.setValue(defaultValue);
        slider.setPreferredSize(new Dimension(300, 50));
        slider.setBackground(DEFAULT_COLOR);
        this.add(slider);

        DefaultFormatter formatter = new DefaultFormatter();
        formatter.setOverwriteMode(false);
        // textField.setFormatterFactory(new
        // javax.swing.text.DefaultFormatterFactory(formatter));

        // textField.setDropMode(DropMode.INSERT);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                try {
                    if (isInteger) {
                        slider.setValue(Integer.parseInt(textField.getText()));
                    } else {
                        slider.setValue((int) (Double.parseDouble(textField.getText().replace(',', '.'))
                                * Math.pow(10, DECIMALS)));
                    }
                } catch (NumberFormatException e1) {
                    JFrame jFrame = new JFrame();
                    JOptionPane.showMessageDialog(jFrame, "El valor debe ser un número válido", "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    textField.setText(isInteger ? String.valueOf(slider.getValue())
                            : String.valueOf((double) slider.getValue() / Math.pow(10, DECIMALS)));
                }
            }

            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
            }
        });

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                textField.setText(isInteger ? String.valueOf(slider.getValue())
                        : String.valueOf((double) slider.getValue() / Math.pow(10, DECIMALS)));
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slider.setValue(defaultValue);
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

        thresholdSetDefaultButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setDefaultThreshold();
            }
        });
    }

    private void initGeneralButtons() {
        JPanel generalButtonsPanel1 = new JPanel();
        generalButtonsPanel1.setBackground(DEFAULT_COLOR);
        generalButtonsPanel1.setLayout(new GridLayout(1, 2));
        generalButtonsPanel1.setPreferredSize(new Dimension(200, 30)); // Fixed size
        generalButtonsPanel1.setMaximumSize(new Dimension(200, 30));

        elegirFichero.setPreferredSize(new Dimension(100, 30)); // Fixed size
        elegirFichero.setMaximumSize(new Dimension(100, 30));
        generateImageButton.setPreferredSize(new Dimension(100, 30)); // Fixed size
        generateImageButton.setMaximumSize(new Dimension(100, 30));

        generalButtonsPanel1.add(elegirFichero);
        generalButtonsPanel1.add(generateImageButton);
        this.add(generalButtonsPanel1);
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
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                controller.generateDoG();
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
        return radiusSlider.getValue();
    }

    public void setDefaultRadius() {
        // radiusText.setText(String.valueOf(RADIUS_DEFAULT));//No es necesario: el
        // listener del slider (radiusInput) ya lo hace
        radiusSlider.setValue(RADIUS_DEFAULT);
    }

    public void setDefaultThreshold() {
        // thresholdText.setText(String.valueOf(THRESHOLD_DEFAULT));//No es necesario:
        // el listener del slider (thresholdInput) ya lo hace
        thresholdSlider.setValue((int) (THRESHOLD_DEFAULT * Math.pow(10, DECIMALS)));
    }

    public JFormattedTextField getScalarText() {
        return scalarText;
    }

    public JFormattedTextField getPhiText() {
        return phiText;
    }

    public String getThresholdValue() {
        return thresholdValue.getText();
    }

    public JCheckBox getInvertir() {
        return invertir;
    }

}
