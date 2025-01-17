cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_VERBOSE_MAKEFILE on)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99 -O3 -fvisibility=hidden")
set(LINKER_FLAGS  "${LINKER_FLAGS} -O3")

include_directories(
    jni)

add_library(secret SHARED
    jni/com_example_jni_NativeSecret.c)

#target_link_libraries(secret
#    android
#    log)
