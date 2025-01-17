/*
 * Sample application to illustrate code virtualization with DexGuard.
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

        // Display the message. We're accessing a method that DexGuard will
        // virtualize (see dexguard-project.txt).
        TextView view = new TextView(this);
        view.setText(new SecretClass().getMessage());
        view.setGravity(Gravity.CENTER);
        setContentView(view);

        // Briefly display a comment.
        Toast.makeText(this, "DexGuard has virtualized the code of all methods inside the application", Toast.LENGTH_LONG).show();
    }
}
