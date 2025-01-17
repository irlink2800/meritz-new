/*
 * Sample application to illustrate tamper detection with DexGuard
 *
 * Copyright (c) 2012-2019 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.guardsquare.dexguard.rasp.callback.DetectionReport;


public class HelloWorldActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_layout);
        TextView textView = findViewById(R.id.tutorial);
        textView.setText(Html.fromHtml(getResources().getString(R.string.tutorial)));
        myToast(this, "DexGuard RASP checks have been injected into this sample.");
    }


    private void myToast(Context context, String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }


    /**
     * This is an optional DexGuard RASP callback method. This signature must match.
     *
     * Any strings used in the callback will be automatically encrypted.
     *
     * @param detectionReport A detection report containing threat information.
     */
    public static void myCallback(DetectionReport detectionReport)
    {
        Log.i("HelloWorldActivity", "Threat detected");

        // The type of threat detected can be checked.
        if (detectionReport.isApkFileTampered())
        {
            Log.i("HelloWorldActivity", "Apk file tampered");
        }
        else if (detectionReport.isAppDebuggable())
        {
            Log.i("HelloWorldActivity", "Application is debuggable");
        }
        else if (detectionReport.isApplicationHooked())
        {
            Log.i("HelloWorldActivity", "Hook detected");
        }
        else if (detectionReport.isCertificateTampered())
        {
            Log.i("HelloWorldActivity", "Certificate tampered");
        }
        else if (detectionReport.isDebuggerAttached())
        {
            Log.i("HelloWorldActivity", "Debugger attached detected");
        }
        else if (detectionReport.isDeviceRooted())
        {
            Log.i("HelloWorldActivity", "Rooted device detected");
        }
        else if (detectionReport.isFileTampered())
        {
            Log.i("HelloWorldActivity", "File tamper detected");
        }
        else if (detectionReport.isRunningInEmulator())
        {
            Log.i("HelloWorldActivity", "Running in emulator detected");
        }
        else if (detectionReport.isRunningInVirtualEnvironment())
        {
            Log.i("HelloWorldActivity", "Running in virtual environment detected");
        }
        else if (detectionReport.isSignedWithDebugKeys())
        {
            Log.i("HelloWorldActivity", "Application is signed with debug keys");
        }
    }
}
