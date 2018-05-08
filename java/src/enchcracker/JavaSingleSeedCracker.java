package enchcracker;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class JavaSingleSeedCracker extends AbstractSingleSeedCracker {

	private ArrayList<Integer> possibleSeeds = new ArrayList<>();
	private ArrayList<Integer> nextPossibleSeeds = new ArrayList<>();
	private AtomicLong seedsSearched = new AtomicLong(0);
	private AtomicBoolean abortRequested = new AtomicBoolean(false);

	// Level generators
	private static int getGenericEnchantability(Random rand, int bookshelves) {
		int first = rand.nextInt(8);
		int second = rand.nextInt(bookshelves + 1);
		return first + 1 + (bookshelves >> 1) + second;
	}

	private static int getLevelsSlot1(Random rand, int bookshelves) {
		int enchantability = getGenericEnchantability(rand, bookshelves) / 3;
		return enchantability < 1 ? 1 : enchantability;
	}

	private static int getLevelsSlot2(Random rand, int bookshelves) {
		return getGenericEnchantability(rand, bookshelves) * 2 / 3 + 1;
	}

	private static int getLevelsSlot3(Random rand, int bookshelves) {
		int enchantability = getGenericEnchantability(rand, bookshelves);
		int twiceBookshelves = bookshelves * 2;
		return enchantability < twiceBookshelves ? twiceBookshelves : enchantability;
	}

	@Override
	public boolean initCracker() {
		possibleSeeds.ensureCapacity(1 << 27);
		return true;
	}

	@Override
	public void resetCracker() {
		possibleSeeds.clear();
		nextPossibleSeeds.clear();
	}

	@Override
	public void firstInput(int bookshelves, int slot1, int slot2, int slot3) {
		Random rand = new Random();
		seedsSearched.set(0);

		for (long seed = 0, e = 1L << 32; seed < e; seed++) {
			// Occasionally update seeds searched for GUI
			if (seed % 1000000 == 0) {
				seedsSearched.set(seed);
				if (abortRequested.get()) {
					abortRequested.set(false);
					break;
				}
			}

			// Test the seed
			rand.setSeed((int) seed);
			if (getLevelsSlot1(rand, bookshelves) == slot1) {
				if (getLevelsSlot2(rand, bookshelves) == slot2) {
					if (getLevelsSlot3(rand, bookshelves) == slot3) {
						synchronized (possibleSeeds) {
							possibleSeeds.add((int) seed);
						}
					}
				}
			}
		}
	}

	@Override
	public void addInput(int bookshelves, int slot1, int slot2, int slot3) {
		Random rand = new Random();
		nextPossibleSeeds.clear();
		seedsSearched.set(0);

		for (int i = 0, e = possibleSeeds.size(); i < e; i++) {
			// Occasionally update seeds searched for GUI
			if (i % 1000000 == 0) {
				if (abortRequested.get()) {
					abortRequested.set(false);
					break;
				}
				seedsSearched.set(i);
			}

			// Test the seed with the new information
			rand.setSeed(possibleSeeds.get(i));
			if (getLevelsSlot1(rand, bookshelves) == slot1) {
				if (getLevelsSlot2(rand, bookshelves) == slot2) {
					if (getLevelsSlot3(rand, bookshelves) == slot3) {
						synchronized (possibleSeeds) {
							nextPossibleSeeds.add(possibleSeeds.get(i));
						}
					}
				}
			}
		}

		possibleSeeds.clear();
		possibleSeeds.addAll(nextPossibleSeeds);
	}

	@Override
	public int getPossibleSeeds() {
		synchronized (possibleSeeds) {
			return possibleSeeds.size();
		}
	}

	@Override
	public int getSeed() {
		return possibleSeeds.get(0);
	}

	@Override
	public void requestAbort() {
		abortRequested.set(true);
	}

	@Override
	public boolean isAbortRequested() {
		return abortRequested.get();
	}

	@Override
	public long getSeedsSearched() {
		return seedsSearched.get();
	}

}
