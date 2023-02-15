package src.view;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JToolBar;

public class ImagesView extends JToolBar {

    private JLabel imageSrc;
    private JLabel imageEdit;

    public JLabel getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(JLabel imageSrc) {
        this.imageSrc = imageSrc;
    }

    public JLabel getImageEdit() {
        return imageEdit;
    }

    public void setImageEdit(JLabel imageEdit) {
        this.imageEdit = imageEdit;
    }

    public ImagesView() {
        this.imageSrc = new JLabel(new ImageIcon());
        this.imageEdit = new JLabel(new ImageIcon());

        this.add(imageSrc);
        this.add(imageEdit);
    }
}
