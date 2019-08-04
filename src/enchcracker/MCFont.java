package enchcracker;

import java.awt.*;

public class MCFont {
    public static Font standardFont;

    static {
        try {
            standardFont = Font.createFont(Font.TRUETYPE_FONT, EnchCrackerWindow.getFile("Minecraftia-Regular.ttf").openStream()).deriveFont(16f);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
