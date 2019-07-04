package enchcracker.swing;

import enchcracker.EnchCrackerWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImagePanel extends JPanel {
    private BufferedImage backgroundImage;
    public ImagePanel(String img) {
        try {
            backgroundImage = ImageIO.read(EnchCrackerWindow.getFile(img+".png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        setOpaque(false);
        setBackground(new Color(255,255,255,0));
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, null);
        super.paint(g);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight());
    }

    @Override
    public Dimension getPreferredSize() {
        return getSize();
    }
}
