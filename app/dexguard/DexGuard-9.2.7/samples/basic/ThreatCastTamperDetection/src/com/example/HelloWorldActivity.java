/*
 * Sample application to illustrate tamper detection with DexGuard.
 *
 * Copyright (c) 2012-2019 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;


public class HelloWorldActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        myToast("hello world", this);
        myToast(this, "hello world");
        myToast(1, this);
    }


    private void myToast(String s, Context context)
    {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void myToast(Context context, String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void myToast(int i, Context context)
    {
        Toast.makeText(this, "i: " + i, Toast.LENGTH_LONG).show();
    }
}
