/*
 * Sample application to illustrate processing with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.crashlytics.FirebaseCrashlytics;


/**
 * Sample activity that allows to trigger a Java crash collected by crashlytics.
 */
public class HelloWorldActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

        crashlytics.log("onCreate");
        crashlytics.setCustomKey("HelloWorld", 1234);
        crashlytics.setUserId("1234");

        setContentView(R.layout.main);
    }

    public void forceJavaCrash(View view)
    {
        Handler.forceJavaCrash(view);
    }
}
