/*
 * Sample application to illustrate processing with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

import com.example.Library;

/**
 * Sample activity that displays "Hello, world!".
 */
public class HelloWorldActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Display the message.
        TextView view = new TextView(this);
        view.setText(new Library().getMessage());
        view.setGravity(Gravity.CENTER);
        setContentView(view);

        // Briefly display a comment.
        Toast.makeText(this, "DexGuard has obfuscated and encrypted the native library of this sample", Toast.LENGTH_LONG).show();
    }
}
