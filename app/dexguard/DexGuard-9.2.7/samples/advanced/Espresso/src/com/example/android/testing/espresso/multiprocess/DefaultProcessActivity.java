/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.testing.espresso.multiprocess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.android.testing.espresso.R;

import static com.example.android.testing.espresso.multiprocess.Util.setCurrentRunningProcess;

/**
 * Activity running in the default process which matches the application package
 */
public class DefaultProcessActivity extends Activity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_process);
        textView = (TextView) findViewById(R.id.textNamedProcess);
        setCurrentRunningProcess(textView, this);
    }

    public void onStartActivityBtnClick(View view) {
        Intent intent = new Intent(this, PrivateProcessActivity.class);
        startActivity(intent);
    }
}