/*
 * Sample application to illustrate the use of DexGuard configuration
 * with annotations.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import com.guardsquare.dexguard.annotation.EncryptClass;
import com.guardsquare.dexguard.annotation.EncryptStrings;

/**
 * Sample class that contains sensitive algorithms or implementations that need
 * to be hidden. DexGuard will encrypt it.
 */
@EncryptClass
public class SecretClass
{
    @EncryptStrings
    public String getMessage()
    {
        // We're also encrypting these strings to add another
        // layer of obfuscation inside the encrypted class.
        return Math.random() > 0.5 ?
            "world!" :
            "Android!";
    }
}
