package enchcracker;

import javax.swing.*;
import java.awt.*;

public class MCFont {
    public static Font standardFont;
    public static Font backupFont = new Font(Font.DIALOG, Font.PLAIN, 16);

    static {
        try {
            standardFont = Font.createFont(Font.TRUETYPE_FONT, EnchCrackerWindow.getFile("Minecraftia-Regular.ttf").openStream()).deriveFont(16f);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static Font getFont(String text) {
        if (standardFont.canDisplayUpTo(text) == -1)
            return standardFont;
        else
            return backupFont;
    }

    public static void setFontFor(JLabel component) {
        component.setFont(getFont(component.getText()));
    }
}
