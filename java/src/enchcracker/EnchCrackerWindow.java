package enchcracker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

@SuppressWarnings("all")
/**
 * This file is mostly generated
 */
public class EnchCrackerWindow extends JFrame {

	private JPanel contentPane;
	private JTextField bookshelvesTextField;
	private JTextField slot1TextField;
	private JTextField slot2TextField;
	private JTextField slot3TextField;
	private JProgressBar progressBar;
	private JTextField xpSeedOutput;
	private JTextField xpSeed1TextField;
	private JTextField xpSeed2TextField;

	private long playerSeed;
	private JLabel playerSeedOutput;
	private JTextField itemTextField;
	private JLabel manipulateOutput;
	private JTextField enchantmentTextField;

	private int timesNeeded = -2;

	private DefaultListModel<Enchantments.EnchantmentInstance> wantedListModel;
	private DefaultListModel<Enchantments.EnchantmentInstance> unwantedListModel;
	private JTextField forcePlayerSeedTextField;

	private static AbstractSingleSeedCracker singleSeedCracker;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

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

		// Initialize seed cracker
		singleSeedCracker = new NativeSingleSeedCracker();
		if (!singleSeedCracker.initCracker()) {
			singleSeedCracker = new JavaSingleSeedCracker();
			if (!singleSeedCracker.initCracker()) {
				return;
			}
		}

		// Start program
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EnchCrackerWindow frame = new EnchCrackerWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					Log.fatal("Exception creating frame", e);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EnchCrackerWindow() {
		setTitle("Enchantment Cracker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 565, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);

		JPanel panel_6 = new JPanel();
		tabbedPane.addTab("XP seed", null, panel_6, null);
		panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.X_AXIS));

		JPanel viewPanel = new JPanel();
		tabbedPane.addTab("Loaded Data", null, viewPanel, null);
		viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.X_AXIS));

		JPanel dataPanel1 = new JPanel();
		dataPanel1.setLayout(new BoxLayout(dataPanel1, BoxLayout.Y_AXIS));

		JPanel dataPanel2 = new JPanel();
		dataPanel2.setLayout(new BoxLayout(dataPanel2, BoxLayout.Y_AXIS));

		JSplitPane enchDataPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dataPanel1, dataPanel2);
		viewPanel.add(enchDataPanel);
		enchDataPanel.setDividerLocation(232);

		JPanel panel_4 = new JPanel();
		panel_6.add(panel_4);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel_4.add(panel);

		JLabel lblNumberOfBookshelves = new JLabel("Number of bookshelves:");
		lblNumberOfBookshelves.setToolTipText("The number of bookshelves exposed to the enchanting table");
		panel.add(lblNumberOfBookshelves);

		bookshelvesTextField = new FixedTextField();
		panel.add(bookshelvesTextField);
		bookshelvesTextField.setColumns(10);

		JPanel panel_1 = new JPanel();
		panel_4.add(panel_1);

		JLabel lblSlot = new JLabel("Slot 1:");
		lblSlot.setToolTipText("The number on the right of the top slot");
		panel_1.add(lblSlot);

		slot1TextField = new FixedTextField();
		panel_1.add(slot1TextField);
		slot1TextField.setColumns(10);

		JPanel panel_2 = new JPanel();
		panel_4.add(panel_2);

		JLabel lblSlot_1 = new JLabel("Slot 2:");
		lblSlot_1.setToolTipText("The number on the right of the middle slot");
		panel_2.add(lblSlot_1);

		slot2TextField = new FixedTextField();
		panel_2.add(slot2TextField);
		slot2TextField.setColumns(10);

		JPanel panel_3 = new JPanel();
		panel_4.add(panel_3);

		JLabel lblSlot_2 = new JLabel("Slot 3:");
		lblSlot_2.setToolTipText("The number on the right of the bottom slot");
		panel_3.add(lblSlot_2);

		slot3TextField = new FixedTextField();
		panel_3.add(slot3TextField);
		slot3TextField.setColumns(10);

		JPanel panel_5 = new JPanel();
		panel_6.add(panel_5);
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.Y_AXIS));

		JPanel panel_19 = new JPanel();
		panel_5.add(panel_19);

		JButton btnAddInfo = new JButton("Add Info");
		panel_19.add(btnAddInfo);
		btnAddInfo.setToolTipText("Use this information to narrow down the possible XP seeds");
		ArrayList<EnchData> enchData = new ArrayList<EnchData>();
		btnAddInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
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

				EnchData ench = new EnchData(bookshelves, slot1, slot2, slot3);
				enchData.add(ench);
				JLabel enchString = new JLabel(ench.toString());
				if (dataPanel1.getComponentCount() < 8) {
					dataPanel1.add(enchString);
				} else {
					dataPanel2.add(enchString);
				}

				singleSeedCracker.abortAndThen(() -> {
					// First time is different because otherwise we have to store all 2^32 initial seeds
					boolean firstTime = singleSeedCracker.isFirstTime();
					singleSeedCracker.setFirstTime(false);

					// Start brute-forcing thread
					Thread thread;
					if (firstTime) {
						thread = new Thread(() -> {
							singleSeedCracker.firstInput(bookshelves, slot1, slot2, slot3);
							int possibleSeeds = singleSeedCracker.getPossibleSeeds();
							Log.info("Reduced possible seeds to " + possibleSeeds);
							switch (possibleSeeds) {
							case 0:
								xpSeedOutput.setText("No possible seeds");
								break;
							case 1:
								xpSeedOutput.setText(String.format("XP seed: %08X", singleSeedCracker.getSeed()));
								if (xpSeed1TextField.getText().isEmpty()) {
									xpSeed1TextField.setText(String.format("%08X", singleSeedCracker.getSeed()));
								} else if (xpSeed2TextField.getText().isEmpty()) {
									xpSeed2TextField.setText(String.format("%08X", singleSeedCracker.getSeed()));
								}
								break;
							default:
								xpSeedOutput.setText("Possible seeds: " + possibleSeeds);
								break;
							}
							singleSeedCracker.setRunning(false);
						});
					} else {
						thread = new Thread(() -> {
							singleSeedCracker.addInput(bookshelves, slot1, slot2, slot3);
							int possibleSeeds = singleSeedCracker.getPossibleSeeds();
							Log.info("Reduced possible seeds to " + possibleSeeds);
							switch (possibleSeeds) {
							case 0:
								xpSeedOutput.setText("No possible seeds");
								break;
							case 1:
								xpSeedOutput.setText(String.format("XP seed: %08X", singleSeedCracker.getSeed()));
								if (xpSeed1TextField.getText().isEmpty()) {
									xpSeed1TextField.setText(String.format("%08X", singleSeedCracker.getSeed()));
								} else if (xpSeed2TextField.getText().isEmpty()) {
									xpSeed2TextField.setText(String.format("%08X", singleSeedCracker.getSeed()));
								}
								break;
							default:
								xpSeedOutput.setText("Possible seeds: " + possibleSeeds);
								break;
							}
							singleSeedCracker.setRunning(false);
						});
					}
					thread.setDaemon(true);
					singleSeedCracker.setRunning(true);
					thread.start();

					// Start progress bar thread
					if (firstTime) {
						progressBar.setMaximum(1 << 16);
						progressBar.setStringPainted(true);
						thread = new Thread(() -> {
							while (singleSeedCracker.isRunning()) {
								long seedsSearched = singleSeedCracker.getSeedsSearched();
								progressBar.setValue((int) (seedsSearched >>> 16));
								progressBar.setString("Seeds searched: " + seedsSearched + " / 4294967296");
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									Thread.currentThread().interrupt();
								}
							}
							progressBar.setStringPainted(false);
							progressBar.setValue(0);
						});
					} else {
						progressBar.setValue(0);
						progressBar.setMaximum(singleSeedCracker.getPossibleSeeds());
						progressBar.setStringPainted(true);
						thread = new Thread(() -> {
							while (singleSeedCracker.isRunning()) {
								long seedsSearched = singleSeedCracker.getSeedsSearched();
								progressBar.setValue((int) seedsSearched);
								progressBar.setString(
										"Seeds searched: " + seedsSearched + " / " + progressBar.getMaximum());
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									Thread.currentThread().interrupt();
								}
							}
							progressBar.setStringPainted(false);
							progressBar.setValue(0);
						});
					}
					thread.setDaemon(true);
					thread.start();
				});
			}
		});
		btnAddInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel view = new JPanel();
		panel_5.add(view);

		JButton btnViewData = new JButton("View Data");
		view.add(btnViewData);
		btnViewData.setToolTipText("View all currently loaded data");
		btnViewData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Log.info("View button pressed");
				tabbedPane.setSelectedIndex(1);
			}
		});
		btnViewData.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel panel_18 = new JPanel();
		panel_5.add(panel_18);

		JButton btnResetCracker = new JButton("Reset Cracker");
		panel_18.add(btnResetCracker);
		btnResetCracker.setToolTipText("Reset the XP seed cracker so a new XP seed can be cracked");
		btnResetCracker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Log.info("Reset the cracker");
				singleSeedCracker.abortAndThen(() -> {
					singleSeedCracker.setFirstTime(true);
					singleSeedCracker.resetCracker();
					dataPanel1.removeAll();
					dataPanel2.removeAll();
					dataPanel1.updateUI();
					dataPanel2.updateUI();
					bookshelvesTextField.setText("");
					slot1TextField.setText("");
					slot2TextField.setText("");
					slot3TextField.setText("");
					xpSeedOutput.setText("XP seed: unknown");
				});
			}
		});
		btnResetCracker.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel panel_17 = new JPanel();
		panel_5.add(panel_17);
		panel_17.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		xpSeedOutput = new FixedTextField("XP seed: unknown");
		xpSeedOutput.setFont(new Font("Dialog", Font.BOLD, 12));
		xpSeedOutput.setEditable(false);
		xpSeedOutput.setBackground(null);
		xpSeedOutput.setBorder(null);
		xpSeedOutput.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_17.add(xpSeedOutput);

		JPanel panel_7 = new JPanel();
		tabbedPane.addTab("Player seed", null, panel_7, null);
		panel_7.setLayout(new BoxLayout(panel_7, BoxLayout.Y_AXIS));

		JPanel panel_8 = new JPanel();
		panel_7.add(panel_8);

		JLabel lblXpSeed = new JLabel("XP seed 1:");
		lblXpSeed.setToolTipText("The first consecutive XP seed");
		panel_8.add(lblXpSeed);

		xpSeed1TextField = new FixedTextField();
		panel_8.add(xpSeed1TextField);
		xpSeed1TextField.setColumns(10);

		JPanel panel_9 = new JPanel();
		panel_7.add(panel_9);

		JLabel lblXpSeed_1 = new JLabel("XP seed 2:");
		lblXpSeed_1.setToolTipText("The second consecutive XP seed");
		panel_9.add(lblXpSeed_1);

		xpSeed2TextField = new FixedTextField();
		panel_9.add(xpSeed2TextField);
		xpSeed2TextField.setColumns(10);

		JButton btnCalculate = new JButton("Calculate");
		btnCalculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean found;
				forcePlayerSeedTextField.setBackground(Color.white);
				xpSeed1TextField.setBackground(Color.white);
				xpSeed2TextField.setBackground(Color.white);

				if (!forcePlayerSeedTextField.getText().isEmpty()) {
					try {
						playerSeed = Long.parseUnsignedLong(forcePlayerSeedTextField.getText(), 16)
								& 0x0000_ffff_ffff_ffffL;
					} catch (NumberFormatException e) {
						Log.info("Calculate player seed failed, invalid force player seed");
						forcePlayerSeedTextField.setBackground(new Color(1.0F, 0.3F, 0.0F));
						return;
					}
					Log.info("Forced player seed");
					found = true;
				} else {

					int xpSeed1, xpSeed2;
					try {
						xpSeed1 = Integer.parseUnsignedInt(xpSeed1TextField.getText(), 16);
					} catch (NumberFormatException e) {
						Log.info("Calculate player seed failed, XP seed 1 invalid");
						xpSeed1TextField.setBackground(new Color(1.0F, 0.3F, 0.0F));
						return;
					}

					try {
						xpSeed2 = Integer.parseUnsignedInt(xpSeed2TextField.getText(), 16);
					} catch (NumberFormatException e) {
						Log.info("Calculate player seed failed, XP seed 2 invalid");
						xpSeed2TextField.setBackground(new Color(1.0F, 0.3F, 0.0F));
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
							found = true;
							break;
						}
					}
				}

				if (found) {
					Log.info("Played seed calculated as " + Long.toHexString(playerSeed));
					playerSeedOutput.setText(String.format("Player seed set to %012X", playerSeed));
				} else {
					Log.info("No player seed found");
					playerSeedOutput.setText("Player seed not found");
				}
			}
		});

		JPanel panel_15 = new JPanel();
		panel_7.add(panel_15);

		JLabel lblForcePlayerSeed = new JLabel("Force Player Seed (optional):");
		lblForcePlayerSeed.setToolTipText("Only use this if you (for some reason) already know the player seed");
		panel_15.add(lblForcePlayerSeed);

		forcePlayerSeedTextField = new FixedTextField();
		panel_15.add(forcePlayerSeedTextField);
		forcePlayerSeedTextField.setColumns(10);
		btnCalculate.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_7.add(btnCalculate);

		playerSeedOutput = new JLabel("No seed calculated");
		playerSeedOutput.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_7.add(playerSeedOutput);

		JPanel panel_10 = new JPanel();
		tabbedPane.addTab("Manipulate", null, panel_10, null);
		panel_10.setLayout(new BoxLayout(panel_10, BoxLayout.Y_AXIS));

		JPanel panel_11 = new JPanel();
		panel_10.add(panel_11);

		JLabel lblItem = new JLabel("Item:");
		lblItem.setToolTipText("The item you want to enchant");
		panel_11.add(lblItem);

		itemTextField = new FixedTextField();
		panel_11.add(itemTextField);
		itemTextField.setColumns(10);

		JButton btnCalculate_1 = new JButton("Calculate");
		btnCalculate_1.setToolTipText(
				"<html>Press to calculate how many items you <i>would</i> need to throw to get these enchantments</html>");
		btnCalculate_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				long seed = playerSeed;
				String item = itemTextField.getText();

				Log.info("Calculating items to throw");
				Log.info("Item: " + item);
				Log.info("Wanted list:");
				for (Enumeration<Enchantments.EnchantmentInstance> e = wantedListModel.elements(); e
						.hasMoreElements();) {
					Log.info("  " + e.nextElement());
				}
				Log.info("Not wanted list:");
				for (Enumeration<Enchantments.EnchantmentInstance> e = unwantedListModel.elements(); e
						.hasMoreElements();) {
					Log.info("  " + e.nextElement());
				}

				if (Items.getEnchantability(item) == 0) {
					return;
				}

				// -2: not found; -1: no dummy enchantment needed; >= 0: number of times needed
				// to throw out item before dummy enchantment
				int timesNeeded = -2;
				int bookshelvesNeeded = 0;
				int slot = 0;
				int[] enchantLevels = new int[3];

				outerLoop: for (int i = -1; i < 10000; i++) {
					int xpSeed;
					if (i == -1) {
						// XP seed will be the current seed, because there is no dummy enchant
						xpSeed = (int) (seed >>> 16);
					} else {
						// XP seed will be the current seed, advanced by one because of the dummy enchant
						xpSeed = (int) (((seed * 0x5deece66dL + 0xb) & 0x0000_ffff_ffff_ffffL) >>> 16);
					}

					Random rand = new Random();
					for (bookshelvesNeeded = 0; bookshelvesNeeded <= 15; bookshelvesNeeded++) {
						rand.setSeed(xpSeed);
						// Calculate all slot levels
						// Important they're done in a row like this because RNG is not reset in between
						for (slot = 0; slot < 3; slot++) {
							int level = Enchantments.calcEnchantmentTableLevel(rand, slot, bookshelvesNeeded, item);
							if (level < slot + 1) {
								level = 0;
							}
							enchantLevels[slot] = level;
						}

						slotLoop: for (slot = 0; slot < 3; slot++) {
							// Get enchantments (changes RNG seed)
							List<Enchantments.EnchantmentInstance> enchantments = Enchantments
									.getEnchantmentsInTable(rand, xpSeed, item, slot, enchantLevels[slot]);

							// Does this list contain all the enchantments we want?
							for (Enumeration<Enchantments.EnchantmentInstance> e = wantedListModel.elements(); e
									.hasMoreElements();) {
								if (!enchantments.contains(e.nextElement())) {
									continue slotLoop;
								}
							}

							// Does this list contain none of the enchantments we don't want?
							for (Enumeration<Enchantments.EnchantmentInstance> e = unwantedListModel.elements(); e
									.hasMoreElements();) {
								if (enchantments.contains(e.nextElement())) {
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
					Log.info("Impossible combination");
					manipulateOutput.setText("Impossible");
				} else if (timesNeeded == -1) {
					Log.info("No dummy, b = " + bookshelvesNeeded + ", s = " + (slot + 1));
					manipulateOutput.setText("No dummy; b: " + bookshelvesNeeded + ", s: " + (slot + 1));
				} else {
					Log.info("Throw " + timesNeeded + " items, b = " + bookshelvesNeeded + ", s = " + (slot + 1));
					manipulateOutput.setText("Throw " + timesNeeded + " (" + (timesNeeded / 64) + ":"
							+ (timesNeeded % 64) + ") items; b: " + bookshelvesNeeded + ", s: " + (slot + 1));
				}
			}
		});
		panel_11.add(btnCalculate_1);

		manipulateOutput = new JLabel("Not calculated");
		panel_11.add(manipulateOutput);

		JButton btnDone = new JButton("Done");
		btnDone.setToolTipText("Press to let the Cracker know that you have gone for these enchantments");
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Log.info("Enchanted and applied changes");
				if (timesNeeded == -2) {
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

				timesNeeded = -2;
				manipulateOutput.setText("Not calculated");
			}
		});
		panel_11.add(btnDone);

		JPanel panel_14 = new JPanel();
		panel_10.add(panel_14);

		JLabel lblEnchantment = new JLabel("Enchantment:");
		lblEnchantment.setToolTipText("The enchantment ID (see the wiki) followed optionally by the level.");
		panel_14.add(lblEnchantment);

		enchantmentTextField = new FixedTextField();
		enchantmentTextField.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_14.add(enchantmentTextField);
		enchantmentTextField.setColumns(10);

		Component horizontalStrut = Box.createHorizontalStrut(30);
		panel_14.add(horizontalStrut);

		JPanel panel_16 = new JPanel();
		panel_14.add(panel_16);
		panel_16.setLayout(new GridLayout(0, 2, 0, 0));

		JButton btnWanted = new JButton("Wanted");
		btnWanted.setToolTipText("This enchantment is wanted in the list of enchantments on the item");
		panel_16.add(btnWanted);

		JButton btnNotWanted = new JButton("Not Wanted");
		btnNotWanted.setToolTipText("This enchantment is not wanted in the list of enchantments on the item");
		panel_16.add(btnNotWanted);

		JButton btnDontCare = new JButton("Don't Care");
		btnDontCare.setToolTipText(
				"It doesn't matter whether this enchantment is in the list of enchantments on the item");
		panel_16.add(btnDontCare);

		JButton btnClear = new JButton("Clear");
		btnClear.setToolTipText("Remove all wanted and unwanted enchantments");
		panel_16.add(btnClear);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				wantedListModel.removeAllElements();
				unwantedListModel.removeAllElements();
			}
		});
		btnDontCare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<Enchantments.EnchantmentInstance> enchantments = Enchantments
						.parseEnchantmentInstance(itemTextField.getText(), enchantmentTextField.getText(), false);

				if (!enchantments.isEmpty()) {
					enchantmentTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					enchantmentTextField.setText("");
					for (Enchantments.EnchantmentInstance enchantment : enchantments) {
						wantedListModel.removeElement(enchantment);
						unwantedListModel.removeElement(enchantment);
					}
				} else {
					enchantmentTextField.setBorder(BorderFactory.createLineBorder(Color.RED));
				}
			}
		});
		btnNotWanted.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<Enchantments.EnchantmentInstance> enchantments = Enchantments
						.parseEnchantmentInstance(itemTextField.getText(), enchantmentTextField.getText(), false);

				if (!enchantments.isEmpty()) {
					enchantmentTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					enchantmentTextField.setText("");
					for (Enchantments.EnchantmentInstance enchantment : enchantments) {
						wantedListModel.removeElement(enchantment);
						if (!unwantedListModel.contains(enchantment)) {
							unwantedListModel.addElement(enchantment);
						}
					}
				} else {
					enchantmentTextField.setBorder(BorderFactory.createLineBorder(Color.RED));
				}
			}
		});
		btnWanted.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<Enchantments.EnchantmentInstance> unwantedEnchantments = Enchantments
						.parseEnchantmentInstance(itemTextField.getText(), enchantmentTextField.getText(), false);
				List<Enchantments.EnchantmentInstance> wantedEnchantments = Enchantments
						.parseEnchantmentInstance(itemTextField.getText(), enchantmentTextField.getText(), true);

				if (!wantedEnchantments.isEmpty()) {
					enchantmentTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					enchantmentTextField.setText("");

					for (Enchantments.EnchantmentInstance enchantment : unwantedEnchantments) {
						unwantedListModel.removeElement(enchantment);
					}
					for (Enchantments.EnchantmentInstance enchantment : wantedEnchantments) {
						if (!wantedListModel.contains(enchantment)) {
							wantedListModel.addElement(enchantment);
						}
					}
				} else {
					enchantmentTextField.setBorder(BorderFactory.createLineBorder(Color.RED));
				}
			}
		});

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_10.add(splitPane);

		JPanel panel_12 = new JPanel();
		splitPane.setLeftComponent(panel_12);
		panel_12.setLayout(new BoxLayout(panel_12, BoxLayout.Y_AXIS));

		JLabel lblWanted = new JLabel("Wanted:");
		panel_12.add(lblWanted);

		JList list = EnchCrackerWindow.createWantedEnchantmentList();
		wantedListModel = (DefaultListModel<Enchantments.EnchantmentInstance>) list.getModel();
		panel_12.add(list);

		JPanel panel_13 = new JPanel();
		splitPane.setRightComponent(panel_13);
		panel_13.setLayout(new BoxLayout(panel_13, BoxLayout.Y_AXIS));

		JLabel lblNotWanted = new JLabel("Not Wanted:");
		panel_13.add(lblNotWanted);

		JList list_1 = EnchCrackerWindow.createUnwantedEnchantmentList();
		unwantedListModel = (DefaultListModel<Enchantments.EnchantmentInstance>) list_1.getModel();
		panel_13.add(list_1);

		JPanel panel_20 = new JPanel();
		tabbedPane.addTab("About", null, panel_20, null);
		panel_20.setLayout(new BoxLayout(panel_20, BoxLayout.Y_AXIS));

		JPanel panel_23 = new JPanel();
		panel_23.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_20.add(panel_23);
		panel_23.setLayout(new BoxLayout(panel_23, BoxLayout.X_AXIS));

		JLabel lblEnchantmentCrackerWritten = new JLabel("Enchantment Cracker, written by Earthcomputer");
		panel_23.add(lblEnchantmentCrackerWritten);

		JPanel panel_26 = new JPanel();
		panel_26.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_20.add(panel_26);
		panel_26.setLayout(new BoxLayout(panel_26, BoxLayout.X_AXIS));

		JLabel lblTutorialAndExplanation = new JLabel("Tutorial and Explanation: ");
		panel_26.add(lblTutorialAndExplanation);

		JLabel lblNewLabel = new JLabel(
				"<html><a href=\"https://youtu.be/hfiTZF0hlzw\">Minecraft, Vanilla Survival: Cracking the Enchantment Seed</a></html>");
		lblNewLabel.setToolTipText("https://youtu.be/hfiTZF0hlzw");
		panel_26.add(lblNewLabel);
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				browse("https://youtu.be/hfiTZF0hlzw");
			}
		});
		lblNewLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		Component verticalStrut = Box.createVerticalStrut(20);
		panel_20.add(verticalStrut);

		JPanel panel_22 = new JPanel();
		panel_22.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_20.add(panel_22);
		panel_22.setLayout(new BoxLayout(panel_22, BoxLayout.X_AXIS));

		JLabel lblGithubPage = new JLabel("GitHub page: ");
		panel_22.add(lblGithubPage);

		JLabel label = new JLabel(
				"<html><a href = \"https://github.com/Earthcomputer/EnchantmentCracker\">Earthcomputer/EnchantmentCracker</a></html>");
		label.setToolTipText("https://github.com/Earthcomputer/EnchantmentCracker");
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				browse("https://github.com/Earthcomputer/EnchantmentCracker");
			}
		});
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panel_22.add(label);

		JPanel panel_24 = new JPanel();
		panel_24.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_20.add(panel_24);
		panel_24.setLayout(new BoxLayout(panel_24, BoxLayout.X_AXIS));

		JLabel lblPleaseReportAny = new JLabel(
				"Please report any bugs you find on the issue tracker on the GitHub page.");
		panel_24.add(lblPleaseReportAny);

		JPanel panel_25 = new JPanel();
		panel_25.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_20.add(panel_25);
		panel_25.setLayout(new BoxLayout(panel_25, BoxLayout.X_AXIS));

		JLabel lblMakeSureTo = new JLabel("Make sure to include the log file (enchcracker.log) in the bug report.");
		panel_25.add(lblMakeSureTo);

		progressBar = new JProgressBar();
		contentPane.add(progressBar);
	}

	/**
	 * @wbp.factory
	 */
	public static JList createWantedEnchantmentList() {
		JList list = new JList();
		list.setModel(new DefaultListModel());
		return list;
	}

	/**
	 * @wbp.factory
	 */
	public static JList createUnwantedEnchantmentList() {
		JList list = new JList();
		list.setModel(new DefaultListModel());
		return list;
	}

	private static void browse(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception e) {
			Log.warn("Error browsing to " + url, e);
		}
	}
}
