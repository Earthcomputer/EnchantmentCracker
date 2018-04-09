package enchcracker;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

public class NativeSingleSeedCracker {

	private static final NativeSingleSeedCracker INSTANCE = new NativeSingleSeedCracker();

	private static AtomicBoolean running = new AtomicBoolean(false);
	private static boolean firstTime = true;

	public static void setRunning(boolean running) {
		NativeSingleSeedCracker.running.set(running);
	}

	public static boolean isRunning() {
		return running.get();
	}

	public static void abortAndThen(Runnable r) {
		if (running.get()) {
			if (!isAbortRequested()) {
				requestAbort();
			}
			while (running.get()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
		r.run();
	}

	public static void setFirstTime(boolean firstTime) {
		NativeSingleSeedCracker.firstTime = firstTime;
	}

	public static boolean isFirstTime() {
		return firstTime;
	}

	private NativeSingleSeedCracker() {
	}

	public static boolean initCracker() {
		Log.info("System details:");
		Log.info("OS = " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
		Log.info("Arch (either OS/Java) = " + System.getProperty("os.arch"));
		Log.info("Java = " + System.getProperty("java.version"));
		if (System.getProperties().containsKey("sun.arch.data.model")) {
			Log.info("Java arch = " + System.getProperty("sun.arch.data.model"));
		}

		try {
			System.loadLibrary("enchcracker");
		} catch (UnsatisfiedLinkError e) {
			Log.fatal("Failed to load native enchcracker library!", e);

			String message = "Failed to load native enchcracker library!\n";
			String origMessage = e.getMessage();
			message += "Message: " + origMessage + "\n";
			String probableCause;
			if (origMessage.contains("32") || origMessage.contains("64")) {
				probableCause = "You are using 32-bit Java or a 32-bit system. You need 64-bit.";
			} else if (origMessage.contains("java.library.path")) {
				probableCause = "The library file could not be found.\nEnsure you are launching as described in README.txt";
			} else {
				probableCause = "Unknown";
			}
			message += "Probable cause: " + probableCause + "\n";
			message += "\n";
			message += "If you are sure this is not the problem, create an issue on GitHub.\n";
			message += "Make sure to include the contents of the log file (enchcracker.log) in your bug report.";
			JOptionPane.showMessageDialog(null, message, "Library load error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		INSTANCE.ninitCracker();
		return true;
	}

	private native void ninitCracker();

	public static void resetCracker() {
		INSTANCE.nresetCracker();
	}

	private native void nresetCracker();

	public static void firstInput(int bookshelves, int slot1, int slot2, int slot3) {
		INSTANCE.nfirstInput(bookshelves, slot1, slot2, slot3);
	}

	private native void nfirstInput(int bookshelves, int slot1, int slot2, int slot3);

	public static void addInput(int bookshelves, int slot1, int slot2, int slot3) {
		INSTANCE.naddInput(bookshelves, slot1, slot2, slot3);
	}

	private native void naddInput(int bookshelves, int slot1, int slot2, int slot3);

	public static int getPossibleSeeds() {
		return INSTANCE.ngetPossibleSeeds();
	}

	private native int ngetPossibleSeeds();

	public static int getSeed() {
		return INSTANCE.ngetSeed();
	}

	private native int ngetSeed();

	public static void requestAbort() {
		INSTANCE.nrequestAbort();
	}

	private native void nrequestAbort();

	public static boolean isAbortRequested() {
		return INSTANCE.nisAbortRequested();
	}

	private native boolean nisAbortRequested();

	public static long getSeedsSearched() {
		return INSTANCE.ngetSeedsSearched();
	}

	private native long ngetSeedsSearched();

}
