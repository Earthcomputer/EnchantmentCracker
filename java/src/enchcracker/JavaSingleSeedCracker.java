package enchcracker;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class JavaSingleSeedCracker extends AbstractSingleSeedCracker {
	// more efficient implementation of java's random class for this specific use-case
	private static long multiplier = 0x5DEECE66DL;
	private static long mask = (1L << 48) - 1;
	private class SimpleRandom {
		private long seed = 0;
		void setSeed(long seed) {
			this.seed = (seed ^ multiplier) & mask;
		}

		// Always next(31) - inlined
		private int next() {
			seed = (seed * multiplier + 0xBL) & mask;
			return (int)(seed >>> 17);
		}

		int nextInt(int bound) {
			int r = next();
			int m = bound - 1;
			if ((bound & m) == 0)  // i.e., bound is a power of 2
				r = (int)((bound * (long)r) >> 31);
			else {
				for (int u = r;  u - (r = u % bound) + m < 0; u = next());
			}
			return r;
		}
	}

	private IntArray possibleSeeds = new IntArray();
	private ArrayList<Integer> nextPossibleSeeds = new ArrayList<>();
	private AtomicLong seedsSearched = new AtomicLong(0);
	private AtomicBoolean abortRequested = new AtomicBoolean(false);

	// Level generators
	private static int getGenericEnchantability(SimpleRandom rand, int bookshelves) {
		int first = rand.nextInt(8);
		int second = rand.nextInt(bookshelves + 1);
		return first + 1 + (bookshelves >> 1) + second;
	}

	private static int getLevelsSlot1(SimpleRandom rand, int bookshelves) {
		int enchantability = getGenericEnchantability(rand, bookshelves) / 3;
		return enchantability < 1 ? 1 : enchantability;
	}

	private static int getLevelsSlot2(SimpleRandom rand, int bookshelves) {
		return getGenericEnchantability(rand, bookshelves) * 2 / 3 + 1;
	}

	private static int getLevelsSlot3(SimpleRandom rand, int bookshelves) {
		int enchantability = getGenericEnchantability(rand, bookshelves);
		int twiceBookshelves = bookshelves * 2;
		return enchantability < twiceBookshelves ? twiceBookshelves : enchantability;
	}

	@Override
	public boolean initCracker() {
		return true;
	}

	@Override
	public void resetCracker() {
		abortRequested.set(true);
		setFirstTime(true);
		possibleSeeds.clear();
		nextPossibleSeeds.clear();
	}

	private int[] list;
	private int listPos;
	private void addToList(int[] toAdd, int len) {
		if (len == 0) return;
		while (listPos + len >= list.length) {
			int[] newArr = new int[list.length + 5000000];
			System.arraycopy(list, 0, newArr, 0, listPos);
			list = newArr;
		}
		System.arraycopy(toAdd, 0, list, listPos, len);
		listPos += len;
	}

	@Override
	public void firstInput(int bookshelves, int slot1, int slot2, int slot3) {
		abortRequested.set(false);
		final int threadCount = Runtime.getRuntime().availableProcessors() - 1; // always leave one for OS
		final int blockSize = Integer.MAX_VALUE / 20 / threadCount - 1;
		final AtomicInteger seed = new AtomicInteger(Integer.MIN_VALUE);
		ArrayList<Thread> threads = new ArrayList<>();

		final Object sync = new Object();
		list = new int[25000000];
		listPos = 0;

		final int twoShelves = bookshelves * 2;
		final int halfShelves = bookshelves / 2 + 1;
		final int shelvesPlusOne = bookshelves + 1;

		final int firstEarly = slot1 * 3 + 1;
		final int secondEarly = slot2 * 3 / 2;
		final int secondSubOne = slot2 - 1;

		seedsSearched.set(0);

		for (int i = 0; i < threadCount; i++) {
			Thread t = new Thread(() -> {
				final int[] myList = new int[10000000];
				int pos = 0;
				final SimpleRandom myRNG = new SimpleRandom();

				while (true) {
					if (abortRequested.get()) return;

					int curSeed = seed.get();
					final int last = curSeed + blockSize;
					if (last < curSeed) break; // overflow
					if (seed.compareAndSet(curSeed, curSeed + blockSize)) {
						for (; curSeed < last; curSeed++) {
							myRNG.setSeed(curSeed);

							int ench1r1 = myRNG.nextInt(8) + halfShelves;
							if (ench1r1 > firstEarly) continue;
							int ench1 = (ench1r1 + myRNG.nextInt(shelvesPlusOne)) / 3;
							if (ench1 < 1) { if (slot1 != 1) continue; }
							if (ench1 != slot1) continue;

							int ench2r1 = myRNG.nextInt(8) + halfShelves;
							if (ench2r1 > secondEarly) continue;
							int ench2 = (ench2r1 + myRNG.nextInt(shelvesPlusOne)) * 2 / 3;
							if (ench2 != secondSubOne) continue;

							int ench3 = (myRNG.nextInt(8) + halfShelves + myRNG.nextInt(shelvesPlusOne));
							if (Math.max(ench3, twoShelves) != slot3) continue;

							myList[pos++] = curSeed;
							if (pos == myList.length) {
								synchronized(sync) { addToList(myList, myList.length); }
								pos = 0;
							}
						}
					}
				}
				synchronized(sync) { addToList(myList, pos); }
			});
			threads.add(t);
			t.start();
		}

		while (true) {
			if (abortRequested.get()) {
				while (threads.size() > 0) {
					try {
						threads.remove(0).join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				abortRequested.set(false);
				return;
			}
			int cur = seed.get();
			if (cur + blockSize < cur) break;
			seedsSearched.set((long)cur - (long)Integer.MIN_VALUE);
			try { Thread.sleep(1); } catch (InterruptedException ignored) {}
		}
		while (threads.size() > 0) {
			try {
				threads.remove(0).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		SimpleRandom myRNG = new SimpleRandom();
		int[] lastFew = new int[blockSize];
		int lastPos = 0;
		for (int s = seed.get(); s != Integer.MIN_VALUE; s++) {
			myRNG.setSeed(s);
			if (getLevelsSlot1(myRNG, bookshelves) == slot1) {
				if (getLevelsSlot2(myRNG, bookshelves) == slot2) {
					if (getLevelsSlot3(myRNG, bookshelves) == slot3) {
						lastFew[lastPos++] = s;
					}
				}
			}
		}

		addToList(lastFew, lastPos);
		possibleSeeds.addAll(list, listPos);
		abortRequested.set(false);
	}

	@Override
	public void addInput(int bookshelves, int slot1, int slot2, int slot3) {
		SimpleRandom rand = new SimpleRandom();
		nextPossibleSeeds.clear();
		seedsSearched.set(0);

		for (int i = 0, e = possibleSeeds.size(); i < e; i++) {
			// Occasionally update seeds searched for GUI
			if (i % 250000 == 0) {
				if (abortRequested.get()) {
					abortRequested.set(false);
					return;
				}
				seedsSearched.set(i);
			}

			// Test the seed with the new information
			int s = possibleSeeds.get(i);
			rand.setSeed(s);
			if (getLevelsSlot1(rand, bookshelves) == slot1) {
				if (getLevelsSlot2(rand, bookshelves) == slot2) {
					if (getLevelsSlot3(rand, bookshelves) == slot3) {
						nextPossibleSeeds.add(s);
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
