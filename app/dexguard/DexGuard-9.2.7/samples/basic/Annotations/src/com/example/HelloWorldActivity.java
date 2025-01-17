/*
 * Sample application to illustrate the use of DexGuard configuration
 * with annotations.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

import com.guardsquare.dexguard.annotation.EncryptString;
import com.guardsquare.dexguard.annotation.ObfuscateCodeMedium;

import java.lang.reflect.Method;

/**
 * Sample activity that displays "Hello, world!".
 */
public class HelloWorldActivity extends Activity
{
    // Explicitly mark the field to be encrypted.
    @EncryptString
    private static final String TOAST_TEXT = "DexGuard has encrypted a secret class inside the application";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Display the message. We're accessing a class that DexGuard will encrypt.
        TextView view = new TextView(this);

        String message = callByReflection("com.example.ReflectionClass", "getMessage") + " " +
                         new SecretClass().getMessage();

        view.setText(message);
        view.setGravity(Gravity.CENTER);
        setContentView(view);

        // Briefly display a comment.
        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();
    }

    @ObfuscateCodeMedium
    private static String callByReflection(String className, String methodName)
    {
        try
        {
            Class  cls    = Class.forName(className);
            Method method = cls.getMethod(methodName);
            Object obj    = cls.newInstance();
            return (String)method.invoke(obj);
        }
        catch (Exception ex)
        {
            return "Could not access class " + className + " by reflection";
        }
    }
}
