/*
 * Sample for custom ViewActions, using a simple button.
 *
 * Copyright (c) 2012-2017 GuardSquare NV
 */

package com.example.android.testing.espresso.custom_viewaction;


import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

public class CustomButtonMagicViewAction implements ViewAction {

    @Override
    public Matcher<View> getConstraints() {
        return isAssignableFrom(MagicalButton.class);
    }

    @Override
    public String getDescription() {
        return "Custom view action test for custom button.";
    }

    @Override
    public void perform(UiController uiController, View view) {
        MagicalButton button = (MagicalButton) view;
        button.performMagic();
    }

}
