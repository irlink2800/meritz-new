/*
 * Sample for custom ViewActions, using a simple button.
 *
 * Copyright (c) 2012-2017 GuardSquare NV
 */

package com.example.android.testing.espresso.custom_viewaction;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.android.testing.espresso.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CustomViewActionTest {

    @Rule
    public ActivityTestRule<CustomViewActionActivity> mActivityRule = new ActivityTestRule<>(
            CustomViewActionActivity.class);

    @Test
    public void testCustomButtonWithCustomAction() {
        // Reverse button text
        onView(withId(R.id.magic_button)).perform(new CustomButtonMagicViewAction());

        // Check whether button text has been reversed
        onView(ViewMatchers.withId(R.id.magic_button))
                .check(matches(withText("nottuB lacigaM")));
    }

}
