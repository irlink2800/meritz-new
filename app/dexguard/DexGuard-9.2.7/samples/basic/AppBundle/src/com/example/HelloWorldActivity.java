/*
 * Sample application to illustrate app bundles with DexGuard.
 *
 * Copyright (c) 2012-2018 GuardSquare NV
 */
package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Xml;
import android.view.Gravity;
import android.widget.*;

import java.io.*;

import com.guardsquare.dexguard.runtime.detection.*;

/**
 * Sample activity that displays messages about available asset files.
 */
public class HelloWorldActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Initialize the activity's layout.
        setContentView(R.layout.main);

        showResourceMessage(R.raw.language, R.id.message_view1);
        showResourceMessage(R.raw.density,  R.id.message_view2);
        showResourceMessage(R.raw.testxml,  R.id.message_view3);

        showNativeMessage(R.id.message_view4);

        showAssetsMessage("messages#tcf_pvrtc/message.txt",  R.id.message_view5);
        showAssetsMessage("messages#tcf_astc/message.txt",   R.id.message_view6);
        showAssetsMessage("messages/message.txt",            R.id.message_view7);

        showFileCheckerResult("classes.dex",                 R.id.message_view8);
        showFileCheckerResult("assets/messages/message.txt", R.id.message_view9);

        // Briefly display a comment.
        Toast.makeText(this, "DexGuard can create an app bundle of this sample", Toast.LENGTH_LONG).show();
    }


    /**
     * Displays the contents of the specified raw resource file in the
     * specified view.
     */
    private void showResourceMessage(int resourceId, int viewId)
    {
        try
        {
            // Open the message asset, if available
            InputStream stream = getResources().openRawResource(resourceId);

            // Read the message from the stream.
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String message = reader.readLine();
            reader.close();

            // Get the text view.
            TextView view = (TextView) findViewById(viewId);

            // Display the message.
            view.setText(message);
        }
        catch (IOException ignore) {}
    }


    /**
     * Displays the message retrieved from a native library in the specified
     * view.
     */
    private void showNativeMessage(int viewId)
    {
        // Get the message from the native library.
        String message = new Native().getMessage();

        // Get the text view.
        TextView view = (TextView) findViewById(viewId);

        // Display the message.
        view.setText(message);
    }


    /**
     * Displays the contents of the specified local asset file in the
     * specified view.
     */
    private void showAssetsMessage(String assetName, int viewId)
    {
        try
        {
            // Open the message asset, if available
            InputStream stream = getAssets().open(assetName);

            // Read the message from the stream.
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String message = reader.readLine();
            reader.close();

            // Get the text view.
            TextView view = (TextView) findViewById(viewId);

            // Display the message.
            view.setText(message);
        }
        catch (IOException ignore) {}
    }


    /**
     * Displays the result of file-level tamper detection in the
     * specified view.
     */
    private void showFileCheckerResult(String file, int viewId)
    {
        int result = new FileChecker(this).checkFile(file);

        // Get the text view.
        TextView view = (TextView) findViewById(viewId);

        // Display the message.
        view.setText("File '" + file + "' has been modified: " + (result != 0));
    }
}
