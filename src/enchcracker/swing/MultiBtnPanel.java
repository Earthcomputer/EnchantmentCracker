package enchcracker.swing;

import enchcracker.EnchCrackerWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MultiBtnPanel extends JPanel {
    public String id; // used for ench buttons

    private BufferedImage backgroundImage;
    private BufferedImage[] buttons;
    private int btnW, numBtn;
    public MultiBtnPanel(String file, int c, int maxV) {
        try {
            BufferedImage img = ImageIO.read(EnchCrackerWindow.getFile(file+".png"));
            numBtn = maxV+2;
            btnW = img.getWidth() / c;
            int h = img.getHeight() / 2;
            backgroundImage = new BufferedImage(btnW * numBtn, h, BufferedImage.TYPE_INT_ARGB);
            backgroundImage.createGraphics().drawImage(img, 0, 0, null);
            buttons = new BufferedImage[c];
            for (int a = 0; a < numBtn; a++) {
                buttons[a] = new BufferedImage(btnW, h, BufferedImage.TYPE_INT_ARGB);
                buttons[a].createGraphics().drawImage(img, -a*btnW, -h, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        setOpaque(false);
        setBackground(new Color(255,255,255,0));

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {
                if (selection != -1) { // used for illegal combinations
                    int x = e.getX() * numBtn / getSize().width;
                    if (x < 0 || x >= numBtn) return;
                    selection = x;
                    repaint();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }

    private int selection = 1;
    public int getSelection() {
        return selection - 1;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, null);
        if (selection == -1) {
            g.setColor(new Color(0,0,0,127));
            g.fillRect(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());
        }
        else g.drawImage(buttons[selection], selection * btnW, 0, null);
        super.paint(g);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(btnW * numBtn, backgroundImage.getHeight());
    }

    @Override
    public Dimension getPreferredSize() {
        return getSize();
    }
}
