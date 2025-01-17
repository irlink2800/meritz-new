/*
 * Sample application to illustrate debug detection, emulator detection, and
 * root detection with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.util.Log;

import com.guardsquare.dexguard.runtime.detection.*;

/**
 * This Activity performs debug blocking.
 */
public class DebugBlockerActivity extends Activity
{
    private ImageView debugBlockerImageView;
    private Drawable  debuggerBlocked;
    private Drawable  debugBlockerFailed;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        debugBlockerFailed    = getResources().getDrawable(R.drawable.ic_empty_grey);
        debuggerBlocked       = getResources().getDrawable(R.drawable.ic_tick_grey);

        debugBlockerImageView = (ImageView) findViewById(R.id.env_check_image_1);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                new Delegate().execute();
            }
        });

        new Delegate().execute();
    }


    /**
     * This utility class performs debug blocking and displays different message in case debug blocking
     * succeeds/fails.
     *
     * We're putting this functionality in a separate class, so we can run it in the background and
     * so we can encrypt it, as an extra layer of protection around the debug blocker and some
     * essential code. We can't encrypt the activity itself, for technical reasons, but an inner
     * class or another class are fine.
     *
     * The Delegate class is implemented as an asynchronous task. This way we ensure that the little
     * overhead, introduced by the debug blocker, does not affect the main application thread.
     *
     * The debug blocker API needs to be called once at the start of the application. It can be called
     * more than once to update the callback method.
     */
    private class Delegate extends AsyncTask<Void,Integer,Boolean>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            debugBlockerImageView.setImageDrawable(null);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int isDebuggerBlocked = values[0];

            // Set the status icon.
            debugBlockerImageView.setImageDrawable(isDebuggerBlocked == 0 ? debuggerBlocked : debugBlockerFailed);

            // Check the return value in more detail.
            if (isDebuggerBlocked == 0)
            {
                // The debug blocker was successful.
                // The application should run as expected.
                Toast.makeText(DebugBlockerActivity.this,
                               "The debug blocker is set up",
                               Toast.LENGTH_LONG).show();
            }
            else
            {
                if ((isDebuggerBlocked & DebugBlocker.DEBUGGER_CONNECTED) != 0)
                {
                    // A debugger is already connected to the application.
                    // A real application should probably exit at this point.
                    Toast.makeText(DebugBlockerActivity.this,
                                   "WARNING! A debugger was already connected",
                               Toast.LENGTH_LONG).show();
                }
                else
                {
                    // The debug blocker failed in an unexpected way.
                    // You should report this case along with the error code.
                    Toast.makeText(DebugBlockerActivity.this,
                                   "WARNING! The debug blocker failed. Error code: " + isDebuggerBlocked,
                                   Toast.LENGTH_LONG).show();
                }
            }
        }

        /**
         * This method will run in a separate thread.
         */
        @Override
        protected Boolean doInBackground(Void... voids)
        {
            // Let the DexGuard runtime library block native debuggers. The return code equals 0 if
            // debuggers are blocked successfully.
            // Nature of the check: One time (can be called multiple times if the callback method
            // needs to be updated.)
            int isDebuggerBlocked = DebugBlocker.blockDebugger(new MyAttackListener());

            // Report the status on the UI thread.
            publishProgress(isDebuggerBlocked);

            return true;
        }
    }


    /**
     * This DebugBlocker.OnAttackListener simply shows a warning message.
     * You should do something more appropriate in a real application,
     * like exiting or sending back a warning to your server.
     */
    private class MyAttackListener implements DebugBlocker.OnAttackListener
    {
        @Override
        public void onAttack()
        {
            DebugBlockerActivity.this.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    // Set the status icon.
                    debugBlockerImageView.setImageDrawable(debugBlockerFailed);

                    // The debug blocker was attacked and compromised.
                    Toast.makeText(DebugBlockerActivity.this,
                                   "WARNING! The debug blocker has been compromised!",
                                   Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
