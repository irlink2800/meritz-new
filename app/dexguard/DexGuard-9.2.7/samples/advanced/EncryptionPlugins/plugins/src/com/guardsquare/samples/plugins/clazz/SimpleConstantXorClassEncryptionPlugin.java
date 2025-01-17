/*
 * Sample to illustrate encryption plugins for DexGuard.
 *
 * Copyright (c) 2015-2018 Guardsquare NV
 */
package com.guardsquare.samples.plugins.asset;

import com.guardsquare.dexguard.encryption.clazz.ClassEncryptionPlugin;
import com.guardsquare.samples.plugins.*;

import java.io.*;

/**
 * This ClassEncryptor encrypts data by combining all bytes in the data
 * with a constant byte using the XOR operation.
 */
public class SimpleConstantXorClassEncryptionPlugin
extends      ClassEncryptionPlugin<Void, Void>
{
    private static final byte OBFUSCATION_CONSTANT = 76;


    // Implementations for ClassEncryptor.

    @Override
    public OutputStream encryptClasses(OutputStream outputStream,
                                       Void         sharedEncryptionKey,
                                       Void         ClassGroupEncryptionKey  ) throws IOException
    {
        return new XorOutputStream(outputStream, OBFUSCATION_CONSTANT);
    }


    @Override
    public InputStream decryptClasses(InputStream inputStream,
                                      Void        sharedEncryptionKey,
                                      Void        ClassGroupEncryptionKey  ) throws IOException
    {
        return new XorInputStream(inputStream, OBFUSCATION_CONSTANT);
    }
}
