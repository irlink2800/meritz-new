/*
 * Sample application to illustrate processing with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Sample activity that displays "Hello, world!".
 */
public class HelloWorldActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try {
            // Open the JavaScript asset.
            InputStream stream = getAssets().open("script.js");

            // Get the JavaScript code.
            String script = readFile(stream);

            final Context context = this;

            // Execute the JavaScript code.
            WebView JSView = new WebView(this);
            JSView.getSettings().setJavaScriptEnabled(true);
            JSView.evaluateJavascript(script, new ValueCallback<String>() {

                @Override
                public void onReceiveValue(String message) {

                    // Display the message
                    TextView view = new TextView(context);
                    view.setText(message);
                    view.setGravity(Gravity.CENTER);
                    setContentView(view);
                }

            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Briefly display a comment.
        Toast.makeText(this, "DexGuard has optimized, converted, signed, and aligned this sample", Toast.LENGTH_LONG).show();
    }


    private static String readFile(InputStream inputStream)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String nextLine = reader.readLine();
            while (nextLine != null)
            {
                stringBuilder.append(nextLine);
                nextLine = reader.readLine();
            }
            reader.close();
            return stringBuilder.toString();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
