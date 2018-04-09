#include <cstdint>
#include <vector>
#include <mutex>
#include <atomic>
#include <jni.h>
#include "enchcracker.hpp"

typedef uint32_t juint;
typedef uint64_t julong;

// Java RNG implementation
#define MULTIPLIER 0x5DEECE66DLL
#define ADDEND 0xBLL
#define MASK ((1LL << 48) - 1)
class jrand {
private:
	julong mseed;

	inline juint next(int bits) {
		return (mseed = (mseed * MULTIPLIER + ADDEND) & MASK) >> (48 - bits);
	}

public:
	jrand() {
		mseed = 0;
	}

	jrand(julong seed) {
		setSeed(seed);
	}

	inline jint nextInt() {
		return next(32);
	}

	inline jint nextInt(jint bound) {
		if ((bound & -bound) == bound) {
			return ((julong) bound * (julong) next(31)) >> 31;
		}

		jint bits, val;
		do {
			bits = next(31);
			val = bits % bound;
		} while (bits - val + (bound - 1) < 0);
		return val;
	}

	inline void setSeed(julong seed) {
		mseed = (seed ^ MULTIPLIER) & MASK;
	}

	inline julong getSeed() {
		return mseed ^ MULTIPLIER;
	}
};
#undef MULTIPLIER
#undef ADDEND
#undef MASK

// Level generators
inline jint getGenericEnchantability(jrand& rand, jint bookshelves) {
	jint first = rand.nextInt(8);
	jint second = rand.nextInt(bookshelves + 1);
	return first + 1 + (bookshelves >> 1) + second;
}

inline jint getLevelsSlot1(jrand& rand, jint bookshelves) {
	jint enchantability = getGenericEnchantability(rand, bookshelves) / 3;
	return enchantability < 1 ? 1 : enchantability;
}

inline jint getLevelsSlot2(jrand& rand, jint bookshelves) {
	return getGenericEnchantability(rand, bookshelves) * 2 / 3 + 1;
}

inline jint getLevelsSlot3(jrand& rand, jint bookshelves) {
	jint enchantability = getGenericEnchantability(rand, bookshelves);
	jint twiceBookshelves = bookshelves * 2;
	return enchantability < twiceBookshelves ? twiceBookshelves : enchantability;
}

// Multithreading stuff (actual threads handled on the Java end)
std::vector<juint> possibleSeeds;
std::vector<juint> nextPossibleSeeds;
std::mutex possibleSeedsMutex;
std::atomic<jlong> seedsSearched(0);
std::atomic<bool> abortRequested(false);

/*
 * Called to initialize the cracker
 */
JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_ninitCracker(
		JNIEnv* env, jobject thisObj) {
	possibleSeeds.reserve(1LL << 25);
}

/*
 * Called when the reset cracker button is pressed
 */
JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_nresetCracker(
		JNIEnv* env, jobject thisObj) {
	possibleSeeds.clear();
	nextPossibleSeeds.clear();
}

/*
 * Called on the first brute force attempt, to save having to store all 2^32 seed initially
 */
JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_nfirstInput(
		JNIEnv* env, jobject thisObj, jint bookshelves, jint slot1, jint slot2,
		jint slot3) {
	jrand rand;
	seedsSearched.store(0);

	for (julong seed = 0, e = 1LL << 32; seed < e; seed++) {
		// Occasionally update seeds searched for the GUI
		if (seed % 1000000 == 0) {
			seedsSearched.store(seed);
			if (abortRequested.load()) {
				abortRequested.store(false);
				break;
			}
		}

		// Test the seed
		rand.setSeed((jint) seed);
		if (getLevelsSlot1(rand, bookshelves) == slot1) {
			if (getLevelsSlot2(rand, bookshelves) == slot2) {
				if (getLevelsSlot3(rand, bookshelves) == slot3) {
					possibleSeedsMutex.lock();
					possibleSeeds.push_back(seed);
					possibleSeedsMutex.unlock();
				}
			}
		}
	}
}

/*
 * Called on subsequent brute force attempts, makes use of a list of previous possible seeds
 */
JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_naddInput(
		JNIEnv* env, jobject thisObj, jint bookshelves, jint slot1, jint slot2,
		jint slot3) {

	jrand rand;
	nextPossibleSeeds.clear();
	seedsSearched.store(0);

	for (auto it = possibleSeeds.begin(); it != possibleSeeds.end(); ++it) {
		// Occasionally update seeds searched for the GUI
		if ((it - possibleSeeds.begin()) % 1000000 == 0) {
			if (abortRequested.load()) {
				abortRequested.store(false);
				break;
			}
			seedsSearched.store((it - possibleSeeds.begin()));
		}

		// Test the seed with the new information
		rand.setSeed((jint) *it);
		if (getLevelsSlot1(rand, bookshelves) == slot1) {
			if (getLevelsSlot2(rand, bookshelves) == slot2) {
				if (getLevelsSlot3(rand, bookshelves) == slot3) {
					possibleSeedsMutex.lock();
					nextPossibleSeeds.push_back(*it);
					possibleSeedsMutex.unlock();
				}
			}
		}
	}

	possibleSeeds.clear();
	possibleSeeds.insert(possibleSeeds.end(), nextPossibleSeeds.begin(),
			nextPossibleSeeds.end());
}

/*
 * Called to get the number of possible seeds
 */
JNIEXPORT jint JNICALL Java_enchcracker_NativeSingleSeedCracker_ngetPossibleSeeds(
		JNIEnv* env, jobject thisObj) {
	possibleSeedsMutex.lock();
	jint size = possibleSeeds.size();
	possibleSeedsMutex.unlock();
	return size;
}

/*
 * Called to get the seed, if there is only one possible seed
 */
JNIEXPORT jint JNICALL Java_enchcracker_NativeSingleSeedCracker_ngetSeed(
		JNIEnv* env, jobject thisObj) {
	return possibleSeeds[0];
}

/*
 * Requests that the search should be aborted
 */
JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_nrequestAbort(
		JNIEnv* env, jobject thisObj) {
	abortRequested.store(true);
}

/*
 * Gets whether the search has been requested to be aborted
 */
JNIEXPORT jboolean JNICALL Java_enchcracker_NativeSingleSeedCracker_nisAbortRequested(
		JNIEnv* env, jobject thisObj) {
	return abortRequested.load();
}

/*
 * Gets the number of seeds searched
 */
JNIEXPORT jlong JNICALL Java_enchcracker_NativeSingleSeedCracker_ngetSeedsSearched(
		JNIEnv* env, jobject thisObj) {
	return seedsSearched.load();
}

