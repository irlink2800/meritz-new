LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := secret
LOCAL_SRC_FILES := com_example_jni_NativeSecret.c

# Strip all non-exported symbol names.
LOCAL_CPPFLAGS += -fvisibility=hidden
LOCAL_CFLAGS   += -fvisibility=hidden

include $(BUILD_SHARED_LIBRARY)
