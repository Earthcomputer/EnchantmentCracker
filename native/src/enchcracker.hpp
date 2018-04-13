#ifndef ENCHCRACKER_HPP_
#define ENCHCRACKER_HPP_

#include <typeinfo>
#ifndef _MSC_VER
#include <cxxabi.h>
#endif
#include <string>
#include <exception>
#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

inline std::string type_name(std::exception& ex) {
	const char* mangled = typeid(ex).name();
#ifdef _MSC_VER
	return std::string(mangled);
#else
	return std::string(abi::__cxa_demangle(mangled, nullptr, nullptr, nullptr));
#endif
}

inline void throwJavaException(JNIEnv* env, std::exception& ex) {
	jclass clazz = env->FindClass("enchcracker/NativeException");
	if (clazz != nullptr) {
		const char* message;
		try {
			message = (type_name(ex) + ": " + ex.what()).c_str();
		} catch (std::exception& e) {
			message = "Exception getting exception message";
		}
		env->ThrowNew(clazz, message);
	}
}

void ninitCracker();

JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_ninitCracker(
		JNIEnv* env, jobject thisObj) {
	try {
		ninitCracker();
	} catch (std::exception& e) {
		throwJavaException(env, e);
	}
}

void nresetCracker();

JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_nresetCracker(
		JNIEnv* env, jobject thisObj) {
	try {
		nresetCracker();
	} catch (std::exception& e) {
		throwJavaException(env, e);
	}
}

void nfirstInput(jint bookshelves, jint slot1, jint slot2, jint slot3);

JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_nfirstInput(
		JNIEnv* env, jobject thisObj, jint bookshelves, jint slot1, jint slot2,
		jint slot3) {
	try {
		nfirstInput(bookshelves, slot1, slot2, slot3);
	} catch (std::exception& e) {
		throwJavaException(env, e);
	}
}

void naddInput(jint bookshelves, jint slot1, jint slot2, jint slot3);

JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_naddInput(
		JNIEnv* env, jobject thisObj, jint bookshelves, jint slot1, jint slot2,
		jint slot3) {
	try {
		naddInput(bookshelves, slot1, slot2, slot3);
	} catch (std::exception& e) {
		throwJavaException(env, e);
	}
}

jint ngetPossibleSeeds();

JNIEXPORT jint JNICALL Java_enchcracker_NativeSingleSeedCracker_ngetPossibleSeeds(
		JNIEnv* env, jobject thisObj) {
	try {
		return ngetPossibleSeeds();
	} catch (std::exception& e) {
		throwJavaException(env, e);
		return 0;
	}
}

jint ngetSeed();

JNIEXPORT jint JNICALL Java_enchcracker_NativeSingleSeedCracker_ngetSeed(
		JNIEnv* env, jobject thisObj) {
	try {
		return ngetSeed();
	} catch (std::exception& e) {
		throwJavaException(env, e);
		return 0;
	}
}

void nrequestAbort();

JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_nrequestAbort(
		JNIEnv* env, jobject thisObj) {
	try {
		nrequestAbort();
	} catch (std::exception& e) {
		throwJavaException(env, e);
	}
}

jboolean nisAbortRequested();

JNIEXPORT jboolean JNICALL Java_enchcracker_NativeSingleSeedCracker_nisAbortRequested(
		JNIEnv* env, jobject thisObj) {
	try {
		return nisAbortRequested();
	} catch (std::exception& e) {
		throwJavaException(env, e);
		return false;
	}
}

jlong ngetSeedsSearched();

JNIEXPORT jlong JNICALL Java_enchcracker_NativeSingleSeedCracker_ngetSeedsSearched(
		JNIEnv* env, jobject thisObj) {
	try {
		return ngetSeedsSearched();
	} catch (std::exception& e) {
		throwJavaException(env, e);
		return 0;
	}
}

#ifdef __cplusplus
}
#endif

#endif /* ENCHCRACKER_HPP_ */
