LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := secret
LOCAL_SRC_FILES := NativeSecret.c

include $(BUILD_SHARED_LIBRARY)
