#ifndef ENCHCRACKER_HPP_
#define ENCHCRACKER_HPP_

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_ninitCracker(
		JNIEnv* env, jobject thisObj);

JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_nresetCracker(
		JNIEnv* env, jobject thisObj);

JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_nfirstInput(
		JNIEnv* env, jobject thisObj, jint bookshelves, jint slot1, jint slot2,
		jint slot3);

JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_naddInput(
		JNIEnv* env, jobject thisObj, jint bookshelves, jint slot1, jint slot2,
		jint slot3);

JNIEXPORT jint JNICALL Java_enchcracker_NativeSingleSeedCracker_ngetPossibleSeeds(
		JNIEnv* env, jobject thisObj);

JNIEXPORT jint JNICALL Java_enchcracker_NativeSingleSeedCracker_ngetSeed(
		JNIEnv* env, jobject thisObj);

JNIEXPORT void JNICALL Java_enchcracker_NativeSingleSeedCracker_nrequestAbort(
		JNIEnv* env, jobject thisObj);

JNIEXPORT jboolean JNICALL Java_enchcracker_NativeSingleSeedCracker_nisAbortRequested(
		JNIEnv* env, jobject thisObj);

JNIEXPORT jlong JNICALL Java_enchcracker_NativeSingleSeedCracker_ngetSeedsSearched(
		JNIEnv* env, jobject thisObj);

#ifdef __cplusplus
}
#endif

#endif /* ENCHCRACKER_HPP_ */
