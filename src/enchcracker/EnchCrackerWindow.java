package enchcracker;

import enchcracker.cracker.*;
import enchcracker.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.text.*;

import static enchcracker.Items.*;

public class EnchCrackerWindow extends StyledFrameMinecraft {

	private static final ResourceBundle RES_BUNDLE = ResourceBundle.getBundle("i18n.EnchantmentCracker", new UTF8ResourceBundleControl());
	private static final NumberFormat DEC_FORMAT = DecimalFormat.getInstance();

	public static URL getFile(String name) {
		File f = new File("data/"+name);
		if (f.exists() && f.isFile()) {
			try {
				return f.toURI().toURL();
			} catch (MalformedURLException e) {
				// should not happen
			}
		}
		return Thread.currentThread().getContextClassLoader().getResource("data/"+name);
	}

	private static String verText() {
		try (Scanner scanner = new Scanner(getFile("version.txt").openStream())) {
			return "V" + scanner.next();
		} catch (IOException e) {
			return "[Unknown Version]";
		}
	}

	public static String translate(String key) {
		return RES_BUNDLE.getString(key).trim();
	}

	@SuppressWarnings("FieldCanBeLocal")
	private JPanel contentPane;
	private JTextField bookshelvesTextField;
	private JTextField slot1TextField;
	private JTextField slot2TextField;
	private JTextField slot3TextField;
	private ProgressButton progressBar;
	private JTextField xpSeed1TextField;
	private JTextField xpSeed2TextField;
	private JTextField levelTextField;

	private long playerSeed;
	private boolean foundPlayerSeed = false;

	private JLabel outDrop, outBook, outSlot;
	private Versions mcVersion = Versions.latest();

	private int timesNeeded = -2;
	private int chosenSlot = -1;

	private static AbstractSingleSeedCracker singleSeedCracker;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.setProperty("sun.java2d.uiScale", "1");
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			// Write to log
			Log.fatal("An unexpected error occurred", e);

			// Display message dialog
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(new JLabel("An unexpected error occurred!"));
			panel.add(new JLabel(e.toString()));
			panel.add(Box.createVerticalStrut(20));
			panel.add(new JLabel("Please report this on the GitHub page at:"));
			JLabel link = new JLabel(
					"<html><a href = \"https://github.com/Earthcomputer/EnchantmentCracker/issues\">https://github.com/Earthcomputer/EnchantmentCracker/issues</a></html>");
			link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			link.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					browse("https://github.com/Earthcomputer/EnchantmentCracker/issues");
				}
			});
			panel.add(link);
			panel.add(new JLabel("Please include the log file (enchcracker.log) in your bug report."));
			JOptionPane.showMessageDialog(null, panel, "Enchantment Crasher", JOptionPane.ERROR_MESSAGE);

			// And exit the program
			System.exit(1);
		});

		// Close the file logger after program has ended
		Runtime.getRuntime().addShutdownHook(new Thread(Log::cleanupLogging));

		printSystemDetails();

		// Note: Native cracker disabled as it is currently slower.
		// Initialize seed cracker
		//singleSeedCracker = new NativeSingleSeedCracker();
		//if (!singleSeedCracker.initCracker()) {
			singleSeedCracker = new JavaSingleSeedCracker();
			if (!singleSeedCracker.initCracker()) {
				return;
			}
		//}

		try {
			EnchCrackerWindow frame = new EnchCrackerWindow();
			frame.setVisible(true);
		} catch (Exception e) {
			Log.fatal("Exception creating frame", e);
		}
	}

	private static void printSystemDetails() {
		Log.info("Enchantment cracker version " + verText());
		Log.info("System details:");
		Log.info("OS = " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
		Log.info("Arch (either OS/Java) = " + System.getProperty("os.arch"));
		Log.info("Java = " + System.getProperty("java.version"));
		if (System.getProperties().containsKey("sun.arch.data.model")) {
			Log.info("Java arch = " + System.getProperty("sun.arch.data.model"));
		}
	}

	public String getRemainingSeedsText(int val) {
		String[] translations = {"enchCrack.remaining", "enchCrack.remaining.thousand", "enchCrack.remaining.million", "enchCrack.remaining.billion"};

		double factor = 1.0 / 1000;
		int n = val;
		int suffix = -1;
		while (n > 0) {
			n /= 1000;
			factor *= 1000;
			suffix++;
		}

		double significand = val / factor;
		int multiplier = 1;
		while (significand < 100) {
			significand *= 10;
			multiplier *= 10;
		}
		significand = Math.round(significand);
		significand /= multiplier;
		return String.format(translate(translations[suffix]), DEC_FORMAT.format(significand));
	}

	private static CardLayout cards = new CardLayout();
	final ImagePanel itemPicker;
	public EnchCrackerWindow() {
		super(
			cards,
			new String[]{"FindSeed", "Manip", "About"},
			new String[]{translate("tab.enchantmentCracker"), translate("tab.enchantmentCalculator"), translate("tab.about")}
		);

		setTitle(translate("program.name"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setOpaque(false);
		contentPane.setBackground(new Color(255,255,255,0));

		contentPane.setLayout(cards);
		setContentPane(contentPane);
		setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy() {
			{
				setImplicitDownCycleTraversal(false);
			}

			@Override
			protected boolean accept(Component component) {
				return (component instanceof JButton || component instanceof JTextField || component instanceof JComboBox) && super.accept(component);
			}
		});

		// --- Seed Cracker section

		ImagePanel findSeedPanel = new ImagePanel("pane1");
		findSeedPanel.setLayout(null);
		contentPane.add(findSeedPanel, "FindSeed");

		int resetW = 80;
		progressBar = new ProgressButton("button");
		progressBar.setText(translate("enchCrack.check"));
		progressBar.setBounds(0, findSeedPanel.getSize().height - progressBar.getPreferredSize().height, findSeedPanel.getSize().width - resetW - 6, progressBar.getPreferredSize().height);
		findSeedPanel.add(progressBar);

		DocumentFilter numberFilter = new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				if (!string.matches("\\d*")) return;
				if (fb.getDocument().getLength() + string.length() > 2) return;
				super.insertString(fb, offset, string, attr);
			}
			@Override
			public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
				if (!string.matches("\\d*")) return;
				if (fb.getDocument().getLength() - length + string.length() > 2) return;
				super.replace(fb, offset, length, string, attr);
			}
		};

		DocumentFilter levelNumberFilter = new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				if (!string.matches("\\d*")) return;
				if (fb.getDocument().getLength() + string.length() > 3) return;
				super.insertString(fb, offset, string, attr);
			}
			@Override
			public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
				if (!string.matches("\\d*")) return;
				if (fb.getDocument().getLength() - length + string.length() > 3) return;
				super.replace(fb, offset, length, string, attr);
			}
		};

		DocumentFilter hexFilter = new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				if (!string.matches("[\\da-fA-F]*")) return;
				if (fb.getDocument().getLength() + string.length() > 8) return;
				super.insertString(fb, offset, string, attr);
			}
			@Override
			public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
				if (!string.matches("[\\da-fA-F]*")) return;
				if (fb.getDocument().getLength() - length + string.length() > 8) return;
				super.replace(fb, offset, length, string, attr);
			}
		};

		bookshelvesTextField = new FixedTextField();
		bookshelvesTextField.setFont(MCFont.standardFont);
		((PlainDocument)bookshelvesTextField.getDocument()).setDocumentFilter(numberFilter);
		findSeedPanel.add(bookshelvesTextField);
		bookshelvesTextField.setBounds(225, 46, 30, 20);
		bookshelvesTextField.setToolTipText(translate("enchCrack.bookshelves.tooltip"));

		slot1TextField = new FixedTextField();
		slot1TextField.setFont(MCFont.standardFont);
		((PlainDocument)slot1TextField.getDocument()).setDocumentFilter(numberFilter);
		findSeedPanel.add(slot1TextField);
		slot1TextField.setBounds(290, 130, 30, 20);
		slot1TextField.setToolTipText(translate("enchCrack.xpCost1.tooltip"));

		slot2TextField = new FixedTextField();
		slot2TextField.setFont(MCFont.standardFont);
		((PlainDocument)slot2TextField.getDocument()).setDocumentFilter(numberFilter);
		findSeedPanel.add(slot2TextField);
		slot2TextField.setBounds(290, 168, 30, 20);
		slot2TextField.setToolTipText(translate("enchCrack.xpCost2.tooltip"));

		slot3TextField = new FixedTextField();
		slot3TextField.setFont(MCFont.standardFont);
		((PlainDocument)slot3TextField.getDocument()).setDocumentFilter(numberFilter);
		findSeedPanel.add(slot3TextField);
		slot3TextField.setBounds(290, 206, 30, 20);
		slot3TextField.setToolTipText(translate("enchCrack.xpCost3.tooltip"));

		progressBar.setToolTipText(translate("enchCrack.check.tooltip"));
		progressBar.addActionListener(event -> {
			int bookshelves, slot1, slot2, slot3;
			bookshelvesTextField.setBackground(Color.white);
			slot1TextField.setBackground(Color.white);
			slot2TextField.setBackground(Color.white);
			slot3TextField.setBackground(Color.white);
			try {
				bookshelves = Integer.parseInt(bookshelvesTextField.getText());
				slot1 = Integer.parseInt(slot1TextField.getText());
				slot2 = Integer.parseInt(slot2TextField.getText());
				slot3 = Integer.parseInt(slot3TextField.getText());
			} catch (NumberFormatException e) {
				Log.info("Add info failed, fields had invalid numbers");
				return;
			}

			if (bookshelves < 0 || bookshelves > 15) {
				Log.info("Add info failed, bookshelf count invalid");
				bookshelvesTextField.setBackground(new Color(1.0F, 0.3F, 0.0F));
				return;
			}

			if (slot1 < 0 || slot1 > 30) {
				Log.info("Add info failed, slot 1 count invalid");
				slot1TextField.setBackground(new Color(1.0F, 0.3F, 0.0F));
				return;
			}

			if (slot2 < 0 || slot2 > 30) {
				Log.info("Add info failed, slot 2 count invalid");
				slot2TextField.setBackground(new Color(1.0F, 0.3F, 0.0F));
				return;
			}

			if (slot3 < 0 || slot3 > 30) {
				Log.info("Add info failed, slot 3 count invalid");
				slot3TextField.setBackground(new Color(1.0F, 0.3F, 0.0F));
				return;
			}

			Log.info("Added info, b = " + bookshelves + ", s1 = " + slot1 + ", s2 = " + slot2 + ", s3 = " + slot3);

			singleSeedCracker.abortAndThen(() -> {
				// First time is different because otherwise we have to store all 2^32 initial seeds
				boolean firstTime = singleSeedCracker.isFirstTime();
				singleSeedCracker.setFirstTime(false);

				// Start brute-forcing thread
				Thread thread;
				if (firstTime) {
					thread = new Thread(() -> {
						progressBar.setProgress(0f);
						singleSeedCracker.firstInput(bookshelves, slot1, slot2, slot3);
						int possibleSeeds = singleSeedCracker.getPossibleSeeds();
						Log.info("Reduced possible seeds to " + possibleSeeds);
						singleSeedCracker.setRunning(false);
						switch (possibleSeeds) {
							case 0:
								progressBar.setText(translate("enchCrack.impossible"));
								progressBar.setProgress(Float.NaN);
								break;
							case 1:
								progressBar.setText(String.format(translate("enchCrack.result"), singleSeedCracker.getSeed()));
								progressBar.setProgress(0f);
								if (xpSeed1TextField.getText().isEmpty()) {
									xpSeed1TextField.setText(String.format("%08X", singleSeedCracker.getSeed()));
								} else if (xpSeed2TextField.getText().isEmpty()) {
									xpSeed2TextField.setText(String.format("%08X", singleSeedCracker.getSeed()));
								}
								break;
							default:
								progressBar.setText(getRemainingSeedsText(possibleSeeds));
								progressBar.setProgress(-1f);
								break;
						}
					});
				} else {
					thread = new Thread(() -> {
						singleSeedCracker.addInput(bookshelves, slot1, slot2, slot3);
						int possibleSeeds = singleSeedCracker.getPossibleSeeds();
						Log.info("Reduced possible seeds to " + possibleSeeds);
						singleSeedCracker.setRunning(false);
						switch (possibleSeeds) {
							case 0:
								progressBar.setText(translate("enchCrack.impossible"));
								progressBar.setProgress(Float.NaN);
								break;
							case 1:
								progressBar.setText(String.format(translate("enchCrack.result"), singleSeedCracker.getSeed()));
								progressBar.setProgress(Float.NaN);
								if (xpSeed1TextField.getText().isEmpty()) {
									xpSeed1TextField.setText(String.format("%08X", singleSeedCracker.getSeed()));
								} else if (xpSeed2TextField.getText().isEmpty()) {
									xpSeed2TextField.setText(String.format("%08X", singleSeedCracker.getSeed()));
								}
								break;
							default:
								progressBar.setText(getRemainingSeedsText(possibleSeeds));
								progressBar.setProgress(-1f);
								break;
						}
					});
				}
				thread.setDaemon(true);
				singleSeedCracker.setRunning(true);
				thread.start();

				// Start progress bar thread
				if (firstTime) {
					thread = new Thread(() -> {
						while (singleSeedCracker.isRunning()) {
							progressBar.setProgress((float)singleSeedCracker.getSeedsSearched() / 4294967296f);
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							}
						}
						bookshelvesTextField.setText("");
						slot1TextField.setText("");
						slot2TextField.setText("");
						slot3TextField.setText("");
					});
				} else {
					thread = new Thread(() -> {
						while (singleSeedCracker.isRunning()) {
							// need this check, as it's possible this line might be hit before seedsSearched is set back to 0
							if (singleSeedCracker.getSeedsSearched() <= singleSeedCracker.getPossibleSeeds()) {
								progressBar.setProgress((float) singleSeedCracker.getSeedsSearched() / (float) singleSeedCracker.getPossibleSeeds());
							}
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							}
						}
						bookshelvesTextField.setText("");
						slot1TextField.setText("");
						slot2TextField.setText("");
						slot3TextField.setText("");
					});
				}
				thread.setDaemon(true);
				thread.start();
			});
		});
		ProgressButton btnCalculate = new ProgressButton("button");

		JButton btnResetCracker = new ProgressButton("button");
		btnResetCracker.setText(translate("enchCrack.reset"));
		btnResetCracker.setBounds(findSeedPanel.getSize().width - resetW, findSeedPanel.getSize().height - progressBar.getPreferredSize().height, resetW, progressBar.getPreferredSize().height);
		findSeedPanel.add(btnResetCracker);
		btnResetCracker.setToolTipText(translate("enchCrack.reset.tooltip"));
		btnResetCracker.addActionListener(event -> {
			Log.info("Reset the cracker");
			singleSeedCracker.abortAndThen(() -> {
				singleSeedCracker.resetCracker();
				bookshelvesTextField.setText("");
				slot1TextField.setText("");
				slot2TextField.setText("");
				slot3TextField.setText("");
				progressBar.setText(translate("enchCrack.check"));
				progressBar.setProgress(-1f);
				btnCalculate.setText(translate("enchCrack.calculate"));
				btnCalculate.setProgress(-1f);
			});
		});

		JLabel xpl1 = new JLabel(translate("enchCrack.xpSeed1"));
		MCFont.setFontFor(xpl1);
		xpl1.setBounds(0, 0, 140, 20);
		findSeedPanel.add(xpl1);

		xpSeed1TextField = new FixedTextField();
		xpSeed1TextField.setFont(MCFont.standardFont);
		((PlainDocument)xpSeed1TextField.getDocument()).setDocumentFilter(hexFilter);
		xpSeed1TextField.setBounds(0, 20, 102, 20);
		findSeedPanel.add(xpSeed1TextField);
		xpSeed1TextField.setToolTipText(translate("enchCrack.xpSeed1.tooltip"));

		JLabel xpl2 = new JLabel(translate("enchCrack.xpSeed2"));
		MCFont.setFontFor(xpl2);
		xpl2.setBounds(0, 40, 140, 20);
		findSeedPanel.add(xpl2);

		xpSeed2TextField = new FixedTextField();
		xpSeed2TextField.setFont(MCFont.standardFont);
		((PlainDocument)xpSeed2TextField.getDocument()).setDocumentFilter(hexFilter);
		xpSeed2TextField.setBounds(0, 60, 102, 20);
		findSeedPanel.add(xpSeed2TextField);
		xpSeed2TextField.setToolTipText(translate("enchCrack.xpSeed2.tooltip"));

		btnCalculate.setText(translate("enchCrack.calculate"));
		btnCalculate.addActionListener(event -> {
			boolean found;
			int xpSeed1, xpSeed2;
			try {
				xpSeed1 = Integer.parseUnsignedInt(xpSeed1TextField.getText(), 16);
			} catch (NumberFormatException e) {
				Log.info("Calculate player seed failed, XP seed 1 invalid");
				return;
			}
			try {
				xpSeed2 = Integer.parseUnsignedInt(xpSeed2TextField.getText(), 16);
			} catch (NumberFormatException e) {
				Log.info("Calculate player seed failed, XP seed 2 invalid");
				return;
			}
			Log.info("Calculating player seed with " + Integer.toHexString(xpSeed1) + ", "
					+ Integer.toHexString(xpSeed2));
			// Brute force the low bits
			long seed1High = ((long) xpSeed1 << 16) & 0x0000_ffff_ffff_0000L;
			long seed2High = ((long) xpSeed2 << 16) & 0x0000_ffff_ffff_0000L;
			found = false;
			for (int seed1Low = 0; seed1Low < 65536; seed1Low++) {
				if ((((seed1High | seed1Low) * 0x5deece66dL + 0xb) & 0x0000_ffff_ffff_0000L) == seed2High) {
					playerSeed = ((seed1High | seed1Low) * 0x5deece66dL + 0xb) & 0x0000_ffff_ffff_ffffL;
					foundPlayerSeed = true;
					found = true;
					break;
				}
			}
			if (found) {
				Log.info("Played seed calculated as " + Long.toHexString(playerSeed));
				btnCalculate.setText(String.format("%012X", playerSeed));
				btnCalculate.setProgress(Float.POSITIVE_INFINITY);
			} else {
				Log.info("No player seed found");
				btnCalculate.setText("Fail!");
				btnCalculate.setProgress(Float.NaN);
			}
		});
		btnCalculate.setBounds(0, 84, 152, 22);
		findSeedPanel.add(btnCalculate);

		// --- Enchantment Calculator section

		JPanel enchList = new JPanel();
		enchList.setOpaque(false);
		enchList.setLayout(null);
		JScrollPane scrollPane = new JScrollPane(enchList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(6, 80, 328, 103);

		ImagePanel manipPane = new ImagePanel("pane2");
		contentPane.add(manipPane, "Manip");
		manipPane.setLayout(null);
		manipPane.add(scrollPane);

		final String[] itemToEnch = {null};
		String[][][] itemGrid = new String[][][]{
			{
				{NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS, BOW, FISHING_ROD, CROSSBOW},
				{NETHERITE_SWORD, NETHERITE_PICKAXE, NETHERITE_AXE, NETHERITE_SHOVEL, NETHERITE_HOE, TRIDENT, BOOK}
			},
			{
				{DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS, BOW, FISHING_ROD, CROSSBOW},
				{DIAMOND_SWORD, DIAMOND_PICKAXE, DIAMOND_AXE, DIAMOND_SHOVEL, DIAMOND_HOE, TRIDENT, BOOK}
			},
			{
				{GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS, BOW, FISHING_ROD, CROSSBOW},
				{GOLDEN_SWORD, GOLDEN_PICKAXE, GOLDEN_AXE, GOLDEN_SHOVEL, GOLDEN_HOE, TRIDENT, BOOK}
			},
			{
				{IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS, BOW, FISHING_ROD, CROSSBOW},
				{IRON_SWORD, IRON_PICKAXE, IRON_AXE, IRON_SHOVEL, IRON_HOE, TRIDENT, BOOK}
			},
			{
				{TURTLE_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, BOW, FISHING_ROD, CROSSBOW},
				{STONE_SWORD, STONE_PICKAXE, STONE_AXE, STONE_SHOVEL, STONE_HOE, TRIDENT, BOOK}
			},
			{
				{LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, BOW, FISHING_ROD, CROSSBOW},
				{WOODEN_SWORD, WOODEN_PICKAXE, WOODEN_AXE, WOODEN_SHOVEL, WOODEN_HOE, TRIDENT, BOOK}
			}
		};
		itemPicker = new ImagePanel("ench_items", 6) {
			private final Color good = new Color(0, 80, 0), bad = new Color(139, 139, 139);
			@Override
			public void paint(Graphics g) {
				int w = getWidth() / 7;
				int h = getHeight() / 2;
				for (int x = 0; x < 7; x++) {
					for (int y = 0; y < 2; y++) {
						g.setColor((itemGrid[itemPicker.curImg][y][x].equals(itemToEnch[0])) ? good : bad);
						g.fillRect(w*x, h*y, w, h);
					}
				}
				super.paint(g);
			}
		};
		itemPicker.setBounds(6,6,itemPicker.getSize().width,itemPicker.getSize().height);
		manipPane.add(itemPicker);
		itemPicker.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {
				int x = e.getX() * 7 / itemPicker.getSize().width;
				if (x < 0 || x > 6) return;
				int y = e.getY() * 2 / itemPicker.getSize().height;
				if (y < 0 || y > 1) return;
				itemToEnch[0] = itemGrid[itemPicker.curImg][y][x];
				int level = 30;
				int enchantability = Items.getEnchantability(itemToEnch[0]);
				level = level + 1 + (enchantability / 4) + (enchantability / 4);
				level += Math.round(level * 1.15f);
				if (level < 1) {
					level = 1;
				}
				ArrayList<Enchantments.EnchantmentInstance> fullList = new ArrayList<>();
				while (level > 0) {
					List<Enchantments.EnchantmentInstance> list = Enchantments.getHighestAllowedEnchantments(level, itemToEnch[0], false, mcVersion);
					for (Enchantments.EnchantmentInstance inst : list) {
						boolean contains = false;
						for (Enchantments.EnchantmentInstance inst2 : fullList) {
							if (inst.enchantment.equals(inst2.enchantment)) {
								contains = true;
								break;
							}
						}
						if (!contains) fullList.add(inst);
					}
					level-=5;
				}
				fullList.sort(Comparator.comparing(ench -> ench.enchantment));

				enchList.removeAll();
				for (int a = 0; a < fullList.size(); a++) {
					Enchantments.EnchantmentInstance inst = fullList.get(a);
					JLabel enchLabel = new JLabel(translate("ench." + inst.enchantment));
					MCFont.setFontFor(enchLabel);
					enchLabel.setBounds(2, a*26, 154, 24);
					enchList.add(enchLabel);

					int max = Enchantments.getMaxLevelInTable(inst.enchantment, itemToEnch[0]);
					MultiBtnPanel enchButton = (max == 1) ? new MultiBtnPanel("levelbtnshort", 3, 1) : new MultiBtnPanel("levelbtn", 7, max);
					enchButton.setBounds(156, a*26, enchButton.getSize().width, enchButton.getSize().height);
					enchList.add(enchButton);
					enchButton.id = inst.enchantment;
				}
				enchList.setPreferredSize(new Dimension(156 + 154, fullList.size()*26-4));
				enchList.invalidate();
				scrollPane.validate();
				repaint();
			}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		itemPicker.setToolTipText(translate("enchCalc.item.tooltip"));

		ImagePanel matPicker = new ImagePanel("ench_mats", 6);
		matPicker.setBounds(298,42,matPicker.getSize().width,matPicker.getSize().height);
		manipPane.add(matPicker);
		matPicker.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {
				do {
					itemPicker.curImg = (itemPicker.curImg + 1) % matPicker.getImageCount();
				} while (mcVersion.before(Materials.getIntroducedVersion(itemPicker.curImg)));
				itemPicker.repaint();
				matPicker.curImg = itemPicker.curImg;
				repaint();
			}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		matPicker.setToolTipText(translate("enchCalc.material.tooltip"));

		FixedTextField maxShelves = new FixedTextField();
		maxShelves.setText("15");
		maxShelves.setFont(MCFont.standardFont);
		((PlainDocument)maxShelves.getDocument()).setDocumentFilter(numberFilter);
		maxShelves.setBounds(268, 12, 30, 20);
		manipPane.add(maxShelves);
		maxShelves.setToolTipText(translate("enchCalc.maxBookshelves.tooltip"));

		ProgressButton findEnchantment = new ProgressButton("button");
		findEnchantment.setText(translate("enchCalc.calculate"));
		findEnchantment.setToolTipText(translate("enchCalc.calculate.tooltip"));
		ProgressButton btnDone = new ProgressButton("button");
		findEnchantment.addActionListener(event -> {
			if (!foundPlayerSeed) {
				JOptionPane.showMessageDialog(this, translate("enchCalc.playerSeedNotFound"), translate("program.name"), JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			long seed = playerSeed;
			if (itemToEnch[0] == null) return;
			int maxShelvesVal = 0;
			try {
				maxShelvesVal = Integer.parseInt(maxShelves.getText());
			}
			catch (NumberFormatException e) {
				Log.info("Max shelves invalid");
				return;
			}
			int playerLevel = 0;
			try {
				playerLevel = Integer.parseInt(levelTextField.getText());
			} catch (NumberFormatException e) {
				Log.info("Level invalid");
				return;
			}

			Log.info("Calculating items to throw");
			Log.info("Item: " + itemToEnch[0]);
			ArrayList<Enchantments.EnchantmentInstance> wantedEnch = new ArrayList<>();
			ArrayList<Enchantments.EnchantmentInstance> unwantedEnch = new ArrayList<>();
			for (Component c : enchList.getComponents()) {
				if (c instanceof MultiBtnPanel) {
					MultiBtnPanel btns = (MultiBtnPanel)c;
					if (btns.id != null) {
						int v = btns.getSelection();
						if (v != 0) {
							if (v == -1) unwantedEnch.add(new Enchantments.EnchantmentInstance(btns.id, 1));
							else wantedEnch.add(new Enchantments.EnchantmentInstance(btns.id, v));
						}
					}
				}
			}
			Log.info("Wanted list:");
			for (Enchantments.EnchantmentInstance inst : wantedEnch) {
				Log.info("  " + inst);
			}
			Log.info("Not wanted list:");
			for (Enchantments.EnchantmentInstance inst : unwantedEnch) {
				Log.info("  " + inst);
			}

			if (Items.getEnchantability(itemToEnch[0]) == 0) {
				return;
			}

			// -2: not found; -1: no dummy enchantment needed; >= 0: number of times needed
			// to throw out item before dummy enchantment
			int timesNeeded = -2;
			int bookshelvesNeeded = 0;
			int slot = 0;
			int[] enchantLevels = new int[3];

			outerLoop: for (int i = -1; i <= 64 * 32; i++) {
				int xpSeed;
				if (i == -1) {
					// XP seed will be the current seed, because there is no dummy enchant
					xpSeed = (int) (seed >>> 16);
				} else {
					// XP seed will be the current seed, advanced by one because of the dummy enchant
					xpSeed = (int) (((seed * 0x5deece66dL + 0xb) & 0x0000_ffff_ffff_ffffL) >>> 16);
				}

				Random rand = new Random();
				for (bookshelvesNeeded = 0; bookshelvesNeeded <= maxShelvesVal; bookshelvesNeeded++) {
					rand.setSeed(xpSeed);
					// Calculate all slot levels
					// Important they're done in a row like this because RNG is not reset in between
					for (slot = 0; slot < 3; slot++) {
						int level = Enchantments.calcEnchantmentTableLevel(rand, slot, bookshelvesNeeded, itemToEnch[0]);
						if (level < slot + 1) {
							level = 0;
						}
						enchantLevels[slot] = level;
					}

					slotLoop: for (slot = 0; slot < 3; slot++) {
						// Get enchantments (changes RNG seed)
						List<Enchantments.EnchantmentInstance> enchantments = Enchantments
								.getEnchantmentsInTable(rand, xpSeed, itemToEnch[0], slot, enchantLevels[slot], mcVersion);

						if (enchantLevels[slot] == 0) {
							continue slotLoop;
						} else if (i == -1) {
							if (playerLevel < enchantLevels[slot]) {
								continue slotLoop;
							}
						} else if (playerLevel < (enchantLevels[slot] + 1)) {
							continue slotLoop;
						}

						// Does this list contain all the enchantments we want?
						for (Enchantments.EnchantmentInstance inst : wantedEnch) {
							boolean found = false;
							for (Enchantments.EnchantmentInstance inst2 : enchantments) {
								if (!inst.enchantment.equals(inst2.enchantment)) continue;
								if (inst.level > inst2.level) continue slotLoop;
								found = true;
								break;
							}
							if (!found) continue slotLoop;
						}

						// Does this list contain none of the enchantments we don't want?
						for (Enchantments.EnchantmentInstance inst : unwantedEnch) {
							for (Enchantments.EnchantmentInstance inst2 : enchantments) {
								if (!inst.enchantment.equals(inst2.enchantment)) continue;
								continue slotLoop;
							}
						}

						timesNeeded = i;
						break outerLoop;
					}
				}

				// Simulate an item throw
				if (i != -1) {
					for (int j = 0; j < 4; j++) {
						seed = (seed * 0x5deece66dL + 0xb) & 0x0000_ffff_ffff_ffffL;
					}
				}
			}

			EnchCrackerWindow.this.timesNeeded = timesNeeded;
			if (timesNeeded == -2) {
				outDrop.setText(translate("enchCalc.impossible"));
				outBook.setText("-");
				outSlot.setText("-");
				Log.info("Impossible combination");
				btnDone.setProgress(Float.NaN);
			} else if (timesNeeded == -1) {
				outDrop.setText(translate("enchCalc.noDummy"));
				outBook.setText(""+bookshelvesNeeded);
				outSlot.setText(""+(slot + 1));
				Log.info("No dummy, b = " + bookshelvesNeeded + ", s = " + (slot + 1));
				btnDone.setProgress(-1);
			} else {
				if (timesNeeded > 63) outDrop.setText(String.format(translate("enchCalc.stackFormat"), timesNeeded / 64, timesNeeded % 64));
				else outDrop.setText(""+timesNeeded);
				outBook.setText(""+bookshelvesNeeded);
				outSlot.setText(""+(slot + 1));
				Log.info("Throw " + timesNeeded + " items, b = " + bookshelvesNeeded + ", s = " + (slot + 1));
				btnDone.setProgress(-1);
			}
			chosenSlot = slot;
		});
		findEnchantment.setBounds(6, 190, 264, 24);
		manipPane.add(findEnchantment);

		btnDone.setText(translate("enchCalc.done"));
		btnDone.setProgress(Float.NaN);
		btnDone.setToolTipText(translate("enchCalc.done.tooltip"));
		btnDone.addActionListener(event -> {
			Log.info("Enchanted and applied changes");
			if (timesNeeded == -2 || chosenSlot == -1) {
				// nothing happened, since it was impossible anyway
				return;
			}
			if (timesNeeded != -1) {
				// items thrown
				for (int i = 0; i < timesNeeded; i++) {
					for (int j = 0; j < 4; j++) {
						playerSeed = (playerSeed * 0x5deece66dL + 0xb) & 0x0000_ffff_ffff_ffffL;
					}
				}
				// dummy enchantment
				playerSeed = (playerSeed * 0x5deece66dL + 0xb) & 0x0000_ffff_ffff_ffffL;
			}
			// actual enchantment
			playerSeed = (playerSeed * 0x5deece66dL + 0xb) & 0x0000_ffff_ffff_ffffL;

			try {
				int playerLevel = Integer.parseInt(levelTextField.getText());
				if (timesNeeded != -1) {
					playerLevel--;
				}
				playerLevel -= (chosenSlot + 1);
				levelTextField.setText(Integer.toString(playerLevel));
			} catch (NumberFormatException e) {
				Log.info("Could not update player level text");
			}
			timesNeeded = -2;
			outDrop.setText("-");
			outBook.setText("-");
			outSlot.setText("-");
			btnDone.setProgress(Float.NaN);
		});
		btnDone.setBounds(276, 190, 58, 24);
		manipPane.add(btnDone);

		outDrop = new JLabel("-");
		outDrop.setFont(MCFont.standardFont);
		outDrop.setToolTipText(translate("enchCalc.throwCount.tooltip"));
		outDrop.setBounds(40, 232, 120, 20);
		manipPane.add(outDrop);

		outSlot = new JLabel("-");
		outSlot.setFont(MCFont.standardFont);
		outSlot.setToolTipText(translate("enchCalc.slot.tooltip"));
		outSlot.setBounds(204, 232, 64, 20);
		manipPane.add(outSlot);

		outBook = new JLabel("-");
		outBook.setFont(MCFont.standardFont);
		outBook.setToolTipText(translate("enchCalc.bookshelves.tooltip"));
		outBook.setBounds(292, 232, 120, 20);
		manipPane.add(outBook);

		JComboBox<Versions> versionDropDown = new JComboBox<>(Versions.values());
		versionDropDown.setSelectedItem(mcVersion);
		versionDropDown.addActionListener(event -> {
			Log.info("Changed Minecraft version to " + versionDropDown.getSelectedItem());
			Versions ver = (Versions) versionDropDown.getSelectedItem();
			if (ver != null)
				mcVersion = ver;
			while (mcVersion.before(Materials.getIntroducedVersion(matPicker.curImg))) {
				matPicker.curImg = (matPicker.curImg + 1) % matPicker.getImageCount();
			}
			itemPicker.curImg = matPicker.curImg;
			repaint();
		});
		versionDropDown.setFont(MCFont.standardFont);
		versionDropDown.setBounds(5, 270, 180, 20);
		manipPane.add(versionDropDown);

		JLabel lblLevel = new JLabel(translate("enchCalc.level") + " ");
		MCFont.setFontFor(lblLevel);
		lblLevel.setBounds(205, 270, 62, 20);
		manipPane.add(lblLevel);

        levelTextField = new FixedTextField();
        levelTextField.setFont(MCFont.standardFont);
        ((PlainDocument)levelTextField.getDocument()).setDocumentFilter(levelNumberFilter);
        manipPane.add(levelTextField);
        levelTextField.setText("999");
        levelTextField.setBounds(270, 270, 45, 20);
        levelTextField.setToolTipText(translate("enchCalc.level.tooltip"));

		// About section

		JPanel aboutPane = new JPanel();
		aboutPane.setOpaque(false);
		contentPane.add(aboutPane, "About");
		aboutPane.setLayout(new BoxLayout(aboutPane, BoxLayout.Y_AXIS));

		String[] aboutLines = String.format(translate("program.about"), verText()).split("\n");
		for (String line : aboutLines) {
			if (line.startsWith("LINK")) {
				String[] parts = line.split(" ");
				if (parts.length >= 3) {
					String url = parts[1];
					String text = Arrays.stream(parts).skip(2).collect(Collectors.joining(" "));
					JLabel label = new JLabel(String.format("<html><a href=\\\"%s\\\">%s</a></html>", url, text));
					label.setToolTipText(url);
					label.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							browse(url);
						}
					});
					label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					aboutPane.add(label);
					continue;
				}
			} else if (line.isEmpty()) {
				aboutPane.add(new JLabel("<html><br/></html>"));
				continue;
			}
			aboutPane.add(new JLabel("<html>" + line + "</html>"));
		}

		Insets i = getInsets();
		Insets i2 = rootPane.getBorder().getBorderInsets(this);
		setSize(i.left + i.right + findSeedPanel.getSize().width + i2.left + i2.right, i.top + i.bottom + findSeedPanel.getSize().height + i2.top + i2.bottom);
		setLocationRelativeTo(null);
	}

	private static void browse(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception e) {
			Log.warn("Error browsing to " + url, e);
		}
	}
}
