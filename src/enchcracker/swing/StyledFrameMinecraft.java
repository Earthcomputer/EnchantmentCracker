package enchcracker.swing;

import enchcracker.EnchCrackerWindow;
import enchcracker.MCFont;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class StyledFrameMinecraft extends JFrame {
    private BufferedImage btn1, btn2;
    private boolean held, over, heldTab;
    private StyledBorder b;
    private String[] cardTitles;

    public StyledFrameMinecraft(CardLayout cards, String[] cardNames, String[] cardTitles) {
        this.cardTitles = cardTitles;
        try {
            BufferedImage closeButton = ImageIO.read(EnchCrackerWindow.getFile("buttonred.png"));
            if (closeButton.getWidth() != closeButton.getHeight() * 2)
                throw new IllegalArgumentException("Button width must be double button height.");
            int h = closeButton.getHeight();
            btn1 = new BufferedImage(h, h, BufferedImage.TYPE_INT_ARGB);
            btn1.createGraphics().drawImage(closeButton, 0, 0, null);
            btn2 = new BufferedImage(h, h, BufferedImage.TYPE_INT_ARGB);
            btn2.createGraphics().drawImage(closeButton, -h, 0, null);

            setUndecorated(true);
            setBackground(new Color(255, 255, 255, 0));
            getRootPane().setOpaque(false);
            b = new StyledBorder(ImageIO.read(EnchCrackerWindow.getFile("mcframe.png")), ImageIO.read(EnchCrackerWindow.getFile("tabs.png")),
                                 new String[]{"experience_bottle", "enchanted_book", "cookie"}, h);
            getRootPane().setBorder(b);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // allows dragging
        final int[] dragX = new int[1];
        final int[] dragY = new int[1];
        getRootPane().addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (held) {
                    boolean old = over;
                    over = isOverBtn(e.getPoint());
                    if (old != over) repaint();
                }
                else if (!heldTab) setLocation(e.getXOnScreen() - dragX[0], e.getYOnScreen() - dragY[0]);
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                over = isOverBtn(e.getPoint());
            }
        });
        getRootPane().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {
                dragX[0] = e.getX();
                dragY[0] = e.getY();
                if (isOverBtn(e.getPoint())) {
                    held = true;
                    repaint();
                }
                else if (isOverTab(e.getPoint())) heldTab = true;
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (held && over) System.exit(0);
                if (heldTab) {
                    Point p = e.getPoint();
                    if (isOverTab(p)) {
                        int tab = p.x / b.tabW;
                        if (tab >= 0 && tab < cardTitles.length) {
                            cards.show(StyledFrameMinecraft.this.getContentPane(), cardNames[tab]);
                            b.setTab(tab);
                        }
                    }
                }
                held = false;
                heldTab = false;
                repaint();
            }
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        getRootPane().setToolTipText("Ilmumbo");
    }

    private boolean isOverBtn(Point p) {
        return (p.x >= (getWidth()-btn1.getWidth()-b.w) && p.x < (getWidth()-b.w) &&
                p.y >= (b.h+b.tabH) && p.y < (b.h+b.tabH+btn1.getHeight()));
    }

    private boolean isOverTab(Point p) {
        return p.y < b.tabH;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage((held && over) ? btn2 : btn1, getWidth() - btn1.getWidth() - b.w, b.h+b.tabH, null);
        g.setFont(MCFont.getFont(cardTitles[b.tab]));
        g.setColor(Color.BLACK);
        g.drawString(cardTitles[b.tab], 10, 5 + g.getFontMetrics().getMaxAscent() + b.tabH);
    }

    @Override
    protected JRootPane createRootPane() {
        JRootPane pane = new JRootPane() {
            @Override
            public String getToolTipText(MouseEvent event) {
                if (isOverTab(event.getPoint())) {
                    int index = event.getPoint().x / b.tabW;
                    if (index >= 0 && index < cardTitles.length)
                        return cardTitles[index];
                }
                return null;
            }

            @Override
            public Point getToolTipLocation(MouseEvent mouseEvent) {
                return new Point(b.tabW, b.tabH + b.frameH + 6);
            }
        };
        pane.setOpaque(true);
        return pane;
    }

    private class StyledBorder implements Border {
        private BufferedImage[][] edges;
        private BufferedImage[] tabImages;
        private BufferedImage[] tabIconList;
        private final int w;
        private final int h;
        final int frameH, tabH, tabW;
        private int tab;

        public void setTab(int tab) {
            this.tab = tab;
            repaint();
        }

        StyledBorder(BufferedImage frameEdge, BufferedImage tabs, String[] tabIcons, int frameH) {
            tabIconList = new BufferedImage[3];
            for (int a = 0; a < 3; a++) {
                try {
                    BufferedImage img = ImageIO.read(EnchCrackerWindow.getFile(tabIcons[a]+".png"));
                    int w = img.getWidth(), h = img.getHeight();
                    tabIconList[a] = new BufferedImage(w*2, h*2, BufferedImage.TYPE_INT_ARGB);
                    tabIconList[a].createGraphics().drawImage(img, 0, 0, w*2, h*2, 0, 0, w, h, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }

            this.frameH = frameH;
            if (frameEdge.getWidth() % 2 != 1 || frameEdge.getHeight() % 2 != 1) throw new IllegalArgumentException("Image width and height must be odd.");
            w = frameEdge.getWidth() / 2;
            h = frameEdge.getHeight() / 2;
            edges = new BufferedImage[3][3];

            edges[0][0] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            edges[0][0].createGraphics().drawImage(frameEdge, 0, 0, w, h, 0, 0, w, h, null);
            edges[0][1] = new BufferedImage(w, 1, BufferedImage.TYPE_INT_ARGB);
            edges[0][1].createGraphics().drawImage(frameEdge, 0, 0, w, 1, 0, h, w, h+1, null);
            edges[0][2] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            edges[0][2].createGraphics().drawImage(frameEdge, 0, 0, w, h, 0, h+1, w, h*2+1, null);

            edges[1][0] = new BufferedImage(1, h, BufferedImage.TYPE_INT_ARGB);
            edges[1][0].createGraphics().drawImage(frameEdge, 0, 0, w, h, w, 0, w+1, h, null);
            edges[1][1] = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            edges[1][1].createGraphics().drawImage(frameEdge, 0, 0, 1, 1, w, h, w+1, h+1, null);
            edges[1][2] = new BufferedImage(1, h, BufferedImage.TYPE_INT_ARGB);
            edges[1][2].createGraphics().drawImage(frameEdge, 0, 0, w, h, w, h+1, w+1, h*2+1, null);

            edges[2][0] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            edges[2][0].createGraphics().drawImage(frameEdge, 0, 0, w, h, w+1, 0, w*2+1, h, null);
            edges[2][1] = new BufferedImage(w, 1, BufferedImage.TYPE_INT_ARGB);
            edges[2][1].createGraphics().drawImage(frameEdge, 0, 0, w, 1, w+1, h, w*2+1, h+1, null);
            edges[2][2] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            edges[2][2].createGraphics().drawImage(frameEdge, 0, 0, w, h, w+1, h+1, w*2+1, h*2+1, null);

            if (tabs.getHeight() % 3 != 0) throw new IllegalArgumentException("Tabs image height must be divisible by 3.");
            tabImages = new BufferedImage[3];
            tabH = tabs.getHeight()/3 - 10;
            for (int a = 0; a < 3; a++) {
                tabImages[a] = new BufferedImage(tabs.getWidth(), tabs.getHeight()/3, BufferedImage.TYPE_INT_ARGB);
                tabImages[a].createGraphics().drawImage(tabs, 0, -tabs.getHeight()/3*a, null);
            }
            tabW = tabs.getWidth() / 3;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D)g;

            g2.drawImage(edges[0][2], x, y+height-h, null);
            g2.drawImage(edges[2][0], x+width-w, y+tabH, null);
            g2.drawImage(edges[2][2], x+width-w, y+height-h, null);

            g2.drawImage(edges[1][0], x+w, y+tabH, x+width-w, y+h+tabH, 0, 0, 1, h, null);
            g2.drawImage(edges[1][2], x+w, y+height-h, x+width-w, y+height, 0, 0, 1, h, null);
            g2.drawImage(edges[0][1], x, y+h+tabH, x+w, y+height-h, 0, 0, w, 1, null);
            g2.drawImage(edges[2][1], x+width-w, y+h+tabH, x+width, y+height-h, 0, 0, w, 1, null);

            g2.drawImage(edges[1][1], x+w, y+h+tabH, x+width-w, y+height-h, 0, 0, 1, 1, null);

            g2.drawImage(tabImages[tab], x, y, null);
            for (int a = 0; a < 3; a++) g2.drawImage(tabIconList[a], a * 54 + 12, 12, null);
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(h+frameH+tabH, w, h, w);
        }
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
