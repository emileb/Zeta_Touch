
APP_ABI      :=  armeabi-v7a arm64-v8a
#APP_ABI      :=  arm64-v8a

APP_PLATFORM:=android-16

APP_STL :=  c++_static

# 16 KB page devices (Android 15+): link arm64 with 16 KB ELF alignment
APP_SUPPORT_FLEXIBLE_PAGE_SIZES := true

##common
APP_CFLAGS += -O2 -DNO_CLOCK_GETTIME
#APP_CFLAGS += -O0 -g

APP_CPPFLAGS += -std=c++11
APP_CPPFLAGS += -std=c++14


APP_CFLAGS += -mllvm -arm-assume-misaligned-load-store
