package enchcracker.swing;

import java.awt.IllegalComponentStateException;
import java.awt.Rectangle;

import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

public class FixedTextField extends JTextField {

	private static final long serialVersionUID = 1L;

	public FixedTextField() {
	}

	public FixedTextField(String text) {
		super(text);
	}

	@Deprecated
	@Override
	public Rectangle modelToView(int pos) throws BadLocationException {
		// Hack-fix for #9 - Enchantment Cracker sometimes crashes if you go from second tab to first tab
		// We have to hack-fix because it's a Java bug (JDK-8179665)
		try {
			getLocationOnScreen();
		} catch (IllegalComponentStateException e) {
			throw new BadLocationException(null, 0);
		}
		return super.modelToView(pos);
	}

}
