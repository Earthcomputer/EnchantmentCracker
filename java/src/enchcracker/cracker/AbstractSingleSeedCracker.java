package enchcracker.cracker;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractSingleSeedCracker {

	private boolean firstTime = true;
	private AtomicBoolean running = new AtomicBoolean(false);

	public void setRunning(boolean running) {
		this.running.set(running);
	}

	public boolean isRunning() {
		return running.get();
	}

	public void abortAndThen(Runnable r) {
		if (isRunning()) {
			if (!isAbortRequested()) {
				requestAbort();
			}
			while (isRunning()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			firstTime = true;
		}
		r.run();
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}

	public boolean isFirstTime() {
		return firstTime;
	}

	public abstract boolean initCracker();

	public abstract void resetCracker();

	public abstract void firstInput(int bookshelves, int slot1, int slot2, int slot3);

	public abstract void addInput(int bookshelves, int slot1, int slot2, int slot3);

	public abstract int getPossibleSeeds();

	public abstract int getSeed();

	public abstract void requestAbort();

	public abstract boolean isAbortRequested();

	public abstract long getSeedsSearched();

}
