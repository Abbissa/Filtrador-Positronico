package src.view;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImagesViewer extends JPanel implements ComponentListener {

    private static final String ORIGINAL_IMAGE_STRING = "Original image";
    private static final String EDITED_IMAGE_STRING = "Edited image";
    private static final int RESIZE_THRESHOLD = 40;

    private JLabel imageSrcLabel;
    private JLabel imageEditLabel;

    private Image imageSrc;
    private Image imageEdit;

    public ImagesViewer() {
        this.imageSrcLabel = new JLabel(ORIGINAL_IMAGE_STRING, JLabel.CENTER);
        this.imageEditLabel = new JLabel(EDITED_IMAGE_STRING, JLabel.CENTER);
        this.addComponentListener(this);
        
        this.setLayout(new GridLayout(1, 2));
        this.add(imageSrcLabel);
        this.add(imageEditLabel);
    }

    public JLabel getImageSrcLabel() {
        return imageSrcLabel;
    }

    public void setImageSrc(Image imageSrc) {
        this.imageSrc = imageSrc;

        Image scaledImage = fitIconImage(imageSrc, imageEditLabel);
        ImageIcon icon = new ImageIcon(scaledImage);

        icon.setDescription(ORIGINAL_IMAGE_STRING);
        this.imageSrcLabel.setToolTipText(ORIGINAL_IMAGE_STRING);
        this.imageSrcLabel.setText("");

        this.imageSrcLabel.setIcon(icon);
    }

    public JLabel getImageEditLabel() {
        return imageEditLabel;
    }

    public Image getImageEdit() {
        return imageEdit;
    }

    public void setImageEdit(Image imageEdit) {
        this.imageEdit = imageEdit;
        
        Image scaledImage = fitIconImage(imageEdit, imageEditLabel);
        ImageIcon icon = new ImageIcon(scaledImage);

        icon.setDescription(EDITED_IMAGE_STRING);
        this.imageEditLabel.setToolTipText(EDITED_IMAGE_STRING);
        this.imageEditLabel.setText("");

        this.imageEditLabel.setIcon(icon);
    }

    public void clearImageSrc() {
        this.imageSrc = null;

        this.imageSrcLabel.setIcon(null);
        this.imageSrcLabel.setText(ORIGINAL_IMAGE_STRING);
    }

    public void clearImageEdit() {
        this.imageEdit = null;

        this.imageEditLabel.setIcon(null);
        this.imageEditLabel.setText(EDITED_IMAGE_STRING);
    }

    private Image fitIconImage(Image image, JLabel label) {
        Image scaledImage = null;
        if(label.getWidth()/image.getWidth(label) > label.getHeight()/image.getHeight(label)) {
            scaledImage = image.getScaledInstance(-1, Math.min(label.getHeight(), image.getHeight(label)), Image.SCALE_FAST);
        }
        else {
            scaledImage = image.getScaledInstance(Math.min(label.getWidth(), image.getWidth(label)), -1, Image.SCALE_FAST);
        }
        return scaledImage;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if(imageSrc != null && Math.abs(imageSrcLabel.getWidth()-imageSrcLabel.getIcon().getIconWidth()) > RESIZE_THRESHOLD) {
            Image scaledImageSrc = fitIconImage(imageSrc, imageSrcLabel);
            this.imageSrcLabel.setIcon(new ImageIcon(scaledImageSrc));
        }

        if(imageEdit != null && Math.abs(imageEditLabel.getWidth()-imageEditLabel.getIcon().getIconWidth()) > RESIZE_THRESHOLD) {
            Image scaledImageEdit = fitIconImage(imageEdit, imageEditLabel);
            this.imageEditLabel.setIcon(new ImageIcon(scaledImageEdit));
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}
