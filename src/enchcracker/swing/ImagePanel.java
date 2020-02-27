package enchcracker.swing;

import enchcracker.EnchCrackerWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImagePanel extends JPanel {
    private BufferedImage[] backgroundImage;
    public int curImg;
    public ImagePanel(String img) {
        try {
            backgroundImage = new BufferedImage[] { ImageIO.read(EnchCrackerWindow.getFile(img+".png")) };
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        setOpaque(false);
        setBackground(new Color(255,255,255,0));
    }
    public ImagePanel(String img, int c) {
        try {
            BufferedImage imgData = ImageIO.read(EnchCrackerWindow.getFile(img+".png"));
            int h = imgData.getHeight() / c;
            backgroundImage = new BufferedImage[c];
            for (int a = 0; a < c; a++) {
                backgroundImage[a] = new BufferedImage(imgData.getWidth(), h, BufferedImage.TYPE_INT_ARGB);
                backgroundImage[a].createGraphics().drawImage(imgData, 0, -h*a, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        setOpaque(false);
        setBackground(new Color(255,255,255,0));
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(backgroundImage[curImg], 0, 0, null);
        super.paint(g);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(backgroundImage[0].getWidth(), backgroundImage[0].getHeight());
    }

    @Override
    public Dimension getPreferredSize() {
        return getSize();
    }

    public int getImageCount() {
        return backgroundImage.length;
    }
}
