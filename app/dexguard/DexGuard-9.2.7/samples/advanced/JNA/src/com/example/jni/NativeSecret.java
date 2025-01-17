/*
 * Sample application to illustrate processing with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example.jni;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Sample class that provides a method accessing the native library
 * using a JNA stub.
 */
public interface NativeSecret extends Library
{
    /**
     * The actual instance to access the native library.
     */
    NativeSecret INSTANCE = (NativeSecret) Native.loadLibrary("secret", NativeSecret.class);

    /**
     * Returns the secret string "Hello world!".
     */
    String getMessage();
}
