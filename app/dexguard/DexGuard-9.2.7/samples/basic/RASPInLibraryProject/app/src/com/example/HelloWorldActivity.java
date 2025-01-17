/*
 * Sample application to illustrate processing with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.widget.*;
import com.example.Library;

/**
 * Sample activity that displays "Hello, world!".
 */
public class HelloWorldActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.main_layout);
        TextView textView = findViewById(R.id.tutorial);
        textView.setText(Html.fromHtml(getResources().getString(R.string.tutorial)));

        // Briefly display a comment.
        Toast.makeText(this, new Library().getMessage(20) , Toast.LENGTH_LONG).show();
    }
}
