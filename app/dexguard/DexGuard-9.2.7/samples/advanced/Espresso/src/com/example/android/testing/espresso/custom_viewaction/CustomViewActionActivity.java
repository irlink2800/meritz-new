/*
 * Sample for custom ViewActions, using a simple button.
 *
 * Copyright (c) 2012-2017 GuardSquare NV
 */

package com.example.android.testing.espresso.custom_viewaction;

import android.os.Bundle;
import android.app.Activity;

import com.example.android.testing.espresso.R;

public class CustomViewActionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view_action);
    }

}
