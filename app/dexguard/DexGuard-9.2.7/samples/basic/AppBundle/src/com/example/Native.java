/*
 * Sample application to illustrate processing with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

/**
 * Sample class that loads a native library and provides a native method.
 */
public class Native
{
    static {
        System.loadLibrary("message");
    }


    /**
     * Returns the message string.
     */
    public native String getMessage();
}
