package enchcracker.swing;

import enchcracker.EnchCrackerWindow;
import enchcracker.MCFont;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ProgressButton extends JButton {
    private BufferedImage[][] images;
    private final int edgeW, edgeH;

    public ProgressButton(String img) {
        super("");
        BufferedImage imgData = null;
        try {
            imgData = ImageIO.read(EnchCrackerWindow.getFile(img+".png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        if (imgData.getWidth() % 2 == 0) throw new IllegalArgumentException("Image width must be odd.");
        if (imgData.getHeight() % 3 != 0) throw new IllegalArgumentException("Image height must be divisible by 3.");
        images = new BufferedImage[3][3];
        edgeW = imgData.getWidth() / 2;
        edgeH = imgData.getHeight() / 3;

        images[0][0] = new BufferedImage(edgeW, edgeH, BufferedImage.TYPE_INT_ARGB);
        images[0][0].createGraphics().drawImage(imgData, 0, 0, null);
        images[0][1] = new BufferedImage(1, edgeH, BufferedImage.TYPE_INT_ARGB);
        images[0][1].createGraphics().drawImage(imgData, -edgeW, 0, null);
        images[0][2] = new BufferedImage(edgeW, edgeH, BufferedImage.TYPE_INT_ARGB);
        images[0][2].createGraphics().drawImage(imgData, -edgeW-1, 0, null);

        images[1][0] = new BufferedImage(edgeW, edgeH, BufferedImage.TYPE_INT_ARGB);
        images[1][0].createGraphics().drawImage(imgData, 0, -edgeH, null);
        images[1][1] = new BufferedImage(1, edgeH, BufferedImage.TYPE_INT_ARGB);
        images[1][1].createGraphics().drawImage(imgData, -edgeW, -edgeH, null);
        images[1][2] = new BufferedImage(edgeW, edgeH, BufferedImage.TYPE_INT_ARGB);
        images[1][2].createGraphics().drawImage(imgData, -edgeW-1, -edgeH, null);

        images[2][0] = new BufferedImage(edgeW, edgeH, BufferedImage.TYPE_INT_ARGB);
        images[2][0].createGraphics().drawImage(imgData, 0, -edgeH*2, null);
        images[2][1] = new BufferedImage(1, edgeH, BufferedImage.TYPE_INT_ARGB);
        images[2][1].createGraphics().drawImage(imgData, -edgeW, -edgeH*2, null);
        images[2][2] = new BufferedImage(edgeW, edgeH, BufferedImage.TYPE_INT_ARGB);
        images[2][2].createGraphics().drawImage(imgData, -edgeW-1, -edgeH*2, null);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
                    doClick();
            }
        });
    }

    private float progress = -1;
    public void setProgress(float amount) {
        progress = amount;
        setEnabled(progress == -1);
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        if (!isEnabled() && progress == -1) setEnabled(true);
        BufferedImage[] imgs;
        if (isEnabled()) {
            if (getModel().isPressed()) imgs = images[1];
            else imgs = images[0];
        }
        else imgs = images[2];
        g.setColor(Color.WHITE);
        g.drawImage(imgs[0], 0, 0, null);
        g.drawImage(imgs[2], getWidth()-edgeW, 0, null);
        g.drawImage(imgs[1], edgeW, 0, getWidth()-edgeW, edgeH, 0, 0, 1, edgeH, null);
        g.setFont(MCFont.standardFont);
        if (isEnabled() || Float.isNaN(progress) || Float.isInfinite(progress)) {
            if (Float.isInfinite(progress)) {
                g.setColor(new Color(0, 80, 0));
                g.fillRect(2, 2, getWidth()-4, getHeight()-4);
            }
            Color color;
            if (isFocusOwner())
                color = Color.GREEN.darker().darker();
            else if (isEnabled() || Float.isNaN(progress))
                color = Color.BLACK;
            else
                color = Color.WHITE;
            g.setColor(color);
            String t = getText();
            drawButtonText(g, t);
        }
        else {
            g.setColor(new Color(0, 80, 0));
            g.fillRect(2, 2, Math.min(getWidth()-4, (int)((getWidth()-4)*progress)), getHeight()-4);
            g.setColor(Color.WHITE);
            String t = String.format(EnchCrackerWindow.translate("enchCrack.progress"), progress * 100);
            drawButtonText(g, t);
        }
    }

    private void drawButtonText(Graphics g, String t) {
        int w = g.getFontMetrics().stringWidth(t);
        if (w > getWidth() - 4) {
            g.setFont(MCFont.standardFont.deriveFont(MCFont.standardFont.getSize2D() * (getWidth() - 4) / w));
            w = g.getFontMetrics().stringWidth(t);
        }
        g.drawString(t, (getSize().width - w) / 2, (getHeight() - g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent()) / 2 + g.getFontMetrics().getMaxAscent());
    }

    @Override
    public Dimension getSize() {
        return new Dimension(super.getWidth(), edgeH);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, edgeH);
    }

}
