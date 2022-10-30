TOP_DIR := $(call my-dir)
LOCAL_PATH := $(call my-dir)


include $(TOP_DIR)/SDL2_OpenTouch/Android.mk


include $(TOP_DIR)/Clibs_OpenTouch/Android.mk
include $(TOP_DIR)/Clibs_OpenTouch/jpeg8d/Android.mk

include $(TOP_DIR)/ZMusic/source/Android.mk

include $(TOP_DIR)/AudioLibs_OpenTouch/openal/Android.mk
include $(TOP_DIR)/AudioLibs_OpenTouch/libmpg123/Android.mk
include $(TOP_DIR)/AudioLibs_OpenTouch/libsndfile-android/jni/Android.mk
include $(TOP_DIR)/AudioLibs_OpenTouch/fluidsynth-lite/src/Android.mk
include $(TOP_DIR)/AudioLibs_OpenTouch/android_external_flac/Android.mk

include $(TOP_DIR)/MobileTouchControls/Android.mk

include $(TOP_DIR)/../../../../../SAFFAL/saffal/src/main/jni/Android.mk


include $(TOP_DIR)/Raze/mobile/Android.mk
include $(TOP_DIR)/Raze_dev/mobile/Android.mk

include $(TOP_DIR)/gl4es/Android.mk

include $(TOP_DIR)/eduke32_mobile_dev/Android.mk
include $(TOP_DIR)/eduke32_mobile_dev/Android_xmp.mk
include $(TOP_DIR)/eduke32_mobile/Android.mk
include $(TOP_DIR)/AWOL/Android.mk
include $(TOP_DIR)/Clibs_OpenTouch/libvpx/Android.mk

#include $(TOP_DIR)/eduke32_mobile/Android_xmp.mk
