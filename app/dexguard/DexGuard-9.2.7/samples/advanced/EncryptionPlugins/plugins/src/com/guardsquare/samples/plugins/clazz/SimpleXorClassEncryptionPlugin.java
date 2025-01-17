/*
 * Sample to illustrate encryption plugins for DexGuard.
 *
 * Copyright (c) 2015-2018 Guardsquare NV
 */
package com.guardsquare.samples.plugins.clazz;

import com.guardsquare.dexguard.encryption.clazz.ClassEncryptionPlugin;
import com.guardsquare.samples.plugins.*;

import java.io.*;
import java.util.Set;

/**
 * This ClassEncryptor encrypts data by combining all bytes in the data
 * with a constant byte using the XOR operation.
 * The constant byte itself is the result of the XOR operation on a shared byte and
 * a class group-specific byte.
 */
public class SimpleXorClassEncryptionPlugin
extends      ClassEncryptionPlugin<SimpleXorClassEncryptionPlugin.SharedEncryptionKey,
                                   SimpleXorClassEncryptionPlugin.EncryptionKey>
{
    // Implementations for ClassEncryptor.

    @Override
    public SharedEncryptionKey createClassSharedEncryptionKey(long        sharedSeed,
                                                              Set<String> classNames )
    {
        SharedEncryptionKey encryptionKey = new SharedEncryptionKey();
        encryptionKey.parameter = (byte) sharedSeed;
        return encryptionKey;
    }


    @Override
    public EncryptionKey createClassEncryptionKey(SharedEncryptionKey sharedEncryptionKey,
                                                  long                classSeed,
                                                  Set<String>         classNames          )
    {
        EncryptionKey encryptionKey = new EncryptionKey();
	int numCharsInNames = 0;
	for (String className : classNames)
	{
	    numCharsInNames += className.length();
	}
        encryptionKey.parameter = (byte) numCharsInNames;
        return encryptionKey;
    }


    @Override
    public OutputStream encryptClasses(OutputStream        outputStream,
                                       SharedEncryptionKey sharedEncryptionKey,
                                       EncryptionKey       encryptionKey       ) throws IOException
    {
        byte combinedXorValue =
           (byte)(sharedEncryptionKey.parameter ^ encryptionKey.parameter);

        return new XorOutputStream(outputStream, combinedXorValue);
    }


    @Override
    public InputStream decryptClasses(InputStream         inputStream,
                                      SharedEncryptionKey sharedEncryptionKey,
                                      EncryptionKey       encryptionKey       ) throws IOException
    {
        byte combinedXorValue =
            (byte) (sharedEncryptionKey.parameter ^ encryptionKey.parameter);

        return new XorInputStream(inputStream, combinedXorValue);
    }


    public static class SharedEncryptionKey
    {
        public byte parameter;
    }


    public static class EncryptionKey
    {
        public byte parameter;
    }
}
