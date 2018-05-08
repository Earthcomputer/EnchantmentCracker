#include <cstdint>
#include <vector>
#include <mutex>
#include <atomic>
#include <jni.h>
#include "enchcracker.hpp"

typedef uint32_t juint;
typedef uint64_t julong;

// Code to allocate large amounts of memory on the Java heap rather than the C++ heap

static JavaVM* cached_jvm = 0;

jint on_load(JavaVM* jvm) {
	cached_jvm = jvm;
	return JNI_VERSION_1_2;
}

static JNIEnv* get_env() {
	JNIEnv* env;
	jint rc = cached_jvm->GetEnv((void**) &env, JNI_VERSION_1_2);
	if (rc == JNI_EDETACHED)
		throw std::runtime_error("Current thread not attached");
	if (rc == JNI_EVERSION)
		throw std::runtime_error("JNI version not supported");
	return env;
}

struct Jalloc {
	jbyteArray jba;
	jobject ref;
};

template<class T>
struct JavaHeapAllocator {
	typedef T value_type;
	JavaHeapAllocator() = default;
	template<class U> constexpr JavaHeapAllocator(const JavaHeapAllocator<U>&)
			noexcept {
	}
	T* allocate(size_t n) {
		if (n > size_t(-1) / sizeof(T))
			throw std::bad_alloc();
		JNIEnv* env = get_env();
		jbyteArray jba = env->NewByteArray(
				(jsize) (sizeof(T) * n + sizeof(Jalloc)));
		if (env->ExceptionOccurred())
			throw std::bad_alloc();
		void* jbuffer = static_cast<void*>(env->GetByteArrayElements(jba, 0));
		if (env->ExceptionOccurred())
			throw std::bad_alloc();
		Jalloc* jalloc = static_cast<Jalloc*>(jbuffer);
		jalloc->jba = jba;
		jalloc->ref = env->NewGlobalRef(jba);
		if (env->ExceptionOccurred())
			throw std::bad_alloc();
		return static_cast<T*>(static_cast<void*>(static_cast<char*>(jbuffer)
				+ sizeof(Jalloc)));
	}
	void deallocate(T* p, size_t n) noexcept {
		if (p != 0) {
			void* buffer =
					static_cast<void*>(static_cast<char*>(static_cast<void*>(p))
							- sizeof(Jalloc));
			Jalloc* jalloc = static_cast<Jalloc*>(buffer);
			if (jalloc->ref) {
				JNIEnv* env = get_env();
				env->DeleteGlobalRef(jalloc->ref);
				env->ReleaseByteArrayElements(jalloc->jba,
						static_cast<jbyte*>(buffer), 0);
			}
		}
	}
};

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
typedef std::vector<juint, JavaHeapAllocator<juint>> juint_vector;
juint_vector* possibleSeeds = nullptr;
juint_vector* nextPossibleSeeds = nullptr;
std::mutex possibleSeedsMutex;
std::atomic<jlong> seedsSearched(0);
std::atomic<bool> abortRequested(false);

/*
 * Called to initialize the cracker
 */
void ninitCracker() {
	possibleSeeds = new juint_vector(JavaHeapAllocator<juint>());
	nextPossibleSeeds = new juint_vector(JavaHeapAllocator<juint>());
	possibleSeeds->reserve(1LL << 27);
}

/*
 * Called to free memory allocated on the Java heap
 */
void nfinalizeCracker() {
	delete possibleSeeds;
	delete nextPossibleSeeds;
}

/*
 * Called when the reset cracker button is pressed
 */
void nresetCracker() {
	possibleSeeds->clear();
	nextPossibleSeeds->clear();
}

/*
 * Called on the first brute force attempt, to save having to store all 2^32 seed initially
 */
void nfirstInput(jint bookshelves, jint slot1, jint slot2, jint slot3) {
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
					possibleSeeds->push_back(seed);
					possibleSeedsMutex.unlock();
				}
			}
		}
	}
}

/*
 * Called on subsequent brute force attempts, makes use of a list of previous possible seeds
 */
void naddInput(jint bookshelves, jint slot1, jint slot2, jint slot3) {

	jrand rand;
	nextPossibleSeeds->clear();
	seedsSearched.store(0);

	auto b = possibleSeeds->begin(), e = possibleSeeds->end();
	for (auto it = b; it != e; ++it) {
		// Occasionally update seeds searched for the GUI
		if ((it - b) % 1000000 == 0) {
			if (abortRequested.load()) {
				abortRequested.store(false);
				break;
			}
			seedsSearched.store((it - b));
		}

		// Test the seed with the new information
		rand.setSeed((jint) *it);
		if (getLevelsSlot1(rand, bookshelves) == slot1) {
			if (getLevelsSlot2(rand, bookshelves) == slot2) {
				if (getLevelsSlot3(rand, bookshelves) == slot3) {
					possibleSeedsMutex.lock();
					nextPossibleSeeds->push_back(*it);
					possibleSeedsMutex.unlock();
				}
			}
		}
	}

	possibleSeeds->clear();
	possibleSeeds->insert(possibleSeeds->end(), nextPossibleSeeds->begin(),
			nextPossibleSeeds->end());
}

/*
 * Called to get the number of possible seeds
 */
jint ngetPossibleSeeds() {
	possibleSeedsMutex.lock();
	jint size = possibleSeeds->size();
	possibleSeedsMutex.unlock();
	return size;
}

/*
 * Called to get the seed, if there is only one possible seed
 */
jint ngetSeed() {
	return (*possibleSeeds)[0];
}

/*
 * Requests that the search should be aborted
 */
void nrequestAbort() {
	abortRequested.store(true);
}

/*
 * Gets whether the search has been requested to be aborted
 */
jboolean nisAbortRequested() {
	return abortRequested.load();
}

/*
 * Gets the number of seeds searched
 */
jlong ngetSeedsSearched() {
	return seedsSearched.load();
}

