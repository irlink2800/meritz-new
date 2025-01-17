/*
 * Sample for custom ViewActions, using a simple button.
 *
 * Copyright (c) 2012-2017 GuardSquare NV
 */

package com.example.android.testing.espresso.custom_viewaction;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.annotation.VisibleForTesting;

public class MagicalButton extends Button {

    public MagicalButton(Context context) {
        super(context);
    }

    public MagicalButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MagicalButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MagicalButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Reverses the button text
     */
    @VisibleForTesting
    public void performMagic(){
        this.setText(new StringBuilder(this.getText()).reverse().toString());
    }

}
