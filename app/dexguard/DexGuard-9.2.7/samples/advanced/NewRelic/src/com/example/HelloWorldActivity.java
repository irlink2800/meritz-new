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

import com.newrelic.agent.android.NewRelic;
import com.newrelic.agent.android.logging.AgentLog;

/**
 * Sample activity that displays "Hello world!",
 * instrumented with NewRelic.
 */
public class HelloWorldActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Set up NewRelic.
        NewRelic.withApplicationToken("0")
                .withLogLevel(AgentLog.DEBUG)
                .start(this.getApplication());

        // Display the message.
        TextView view = new TextView(this);
        view.setText("Hello world!");
        view.setGravity(Gravity.CENTER);
        setContentView(view);

        // Briefly display a comment.
        Toast.makeText(this, "DexGuard has optimized, converted, signed, and aligned this sample", Toast.LENGTH_LONG).show();
    }
}
