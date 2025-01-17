/*
 * Sample application to illustrate adding reflection using DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

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
        view.setText("Hello, world!");
        view.setGravity(Gravity.CENTER);
        // DexGuard will replace this direct call by using reflection instead.
        // The method name will also be encrypted.
        // See dexguard-project.txt for how this is configured.
        setContentView(view);

        // Briefly display a comment.
        Toast.makeText(this, "DexGuard has performed reflection and encryption on an API call inside the application", Toast.LENGTH_LONG).show();
    }
}
