/*
 * Sample application to illustrate the use of DexGuard configuration
 * with annotations.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;


import proguard.annotation.*;

/**
 * Sample class that is accessed by reflection.
 * DexGuard needs to keep it.
 */
@Keep
@KeepPublicClassMembers
public class ReflectionClass
{
    public String getMessage()
    {
        return "Hello";
    }
}
