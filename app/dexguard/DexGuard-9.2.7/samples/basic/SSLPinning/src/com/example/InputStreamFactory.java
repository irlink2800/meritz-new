/*
 * Sample application to illustrate SSL pinning with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import java.io.InputStream;

/**
 * This interface creates input streams for given URLs.
 */
public interface InputStreamFactory
{
    /**
     * Creates an input stream for a given URL.
     *
     * @param url the URL to retrieve.
     * @return    the input stream for the URL, or null if the input stream
     *            could not be created.
     */
    InputStream createInputStream(String url);
}
