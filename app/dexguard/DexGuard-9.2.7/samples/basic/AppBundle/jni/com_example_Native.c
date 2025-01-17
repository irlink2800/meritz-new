#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

#if defined(__aarch64__)
#define ABI "arm64-v8a"
#elif defined(__arm__)
  #if defined(__ARM_ARCH_7A__)
  #define ABI "armeabi-v7a"
  #else
  #define ABI "armeabi"
#endif
#elif defined(__x86_64__)
#define ABI "x86_64"
#elif defined(__i386__)
#define ABI "x86"
#elif defined(__mips64)
#define ABI "mips64"
#elif defined(__mips__)
#define ABI "mips"
#else
#define ABI "unknown"
#endif

JNIEXPORT jstring JNICALL Java_com_example_Native_getMessage
  (JNIEnv *env, jobject obj) {

    return (*env)->NewStringUTF(env, "This sample contains a native library for " ABI);
}

#ifdef __cplusplus
}
#endif
