/*
 * Sample application to illustrate processing with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.guardsquare.appactions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

import java.text.MessageFormat;

/**
 * Sample activity that displays your search using App Actions.
 */
public class HelloWorldActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String query = "nothing";

        // Obtain the query value if this was started via an App Action
        Intent intent = getIntent();
        if (intent.getData() != null && Intent.ACTION_VIEW.equals(intent.getAction()))
        {
            if ("/search".equals(intent.getData().getPath())) {
                query = intent.getData().getQueryParameter("query");
            }
        }

        // Display message.
        TextView view = new TextView(this);
        view.setText(MessageFormat.format("You searched for: {0}!", query));
        view.setGravity(Gravity.CENTER);
        setContentView(view);
    }
}
