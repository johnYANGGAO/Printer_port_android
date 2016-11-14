LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := serial_port
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	/Users/johnsmac/Documents/my_git_center/O.MEPrinter2/app/src/main/jni/Android.mk \
	/Users/johnsmac/Documents/my_git_center/O.MEPrinter2/app/src/main/jni/Application.mk \
	/Users/johnsmac/Documents/my_git_center/O.MEPrinter2/app/src/main/jni/gen_SerialPort_h.sh \
	/Users/johnsmac/Documents/my_git_center/O.MEPrinter2/app/src/main/jni/SerialPort.c \

LOCAL_C_INCLUDES += /Users/johnsmac/Documents/my_git_center/O.MEPrinter2/app/src/main/jni
LOCAL_C_INCLUDES += /Users/johnsmac/Documents/my_git_center/O.MEPrinter2/app/src/debug/jni

include $(BUILD_SHARED_LIBRARY)
