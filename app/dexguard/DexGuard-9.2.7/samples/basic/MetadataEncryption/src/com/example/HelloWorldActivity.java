/*
 * Sample application to illustrate metadata encryption with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import java.io.*;

/**
 * Sample activity that displays "Hello, world!".
 */
public class HelloWorldActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Retrieve the message metadata from the Android Manifest.
        String message = null;
        try {
            Bundle metadata = getPackageManager().getActivityInfo(this.getComponentName(),
                                                                  PackageManager.GET_META_DATA).metaData;
            if (metadata != null)
            {
                message = metadata.getString("message");
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Display the message.
        TextView view = new TextView(this);
        view.setText(message);
        view.setGravity(Gravity.CENTER);
        setContentView(view);

        // Briefly display a comment.
        Toast.makeText(this, "DexGuard has encrypted the metadata of this sample.", Toast.LENGTH_LONG).show();
    }

}
