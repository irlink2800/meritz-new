/*
 * Sample application to illustrate debug detection, emulator detection, and
 * root detection with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.content.*;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.util.Log;

import com.guardsquare.dexguard.runtime.detection.*;

import java.util.*;

/**
 * This Activity performs some environment checks.
 */
public class EnvironmentCheckActivity extends Activity
{
    private static final int[] imageViewIds = {
        R.id.env_check_image_1,
        R.id.env_check_image_2,
        R.id.env_check_image_3,
        R.id.env_check_image_4,
        R.id.env_check_image_5,
        R.id.env_check_image_6,
        R.id.env_check_image_7,
        R.id.env_check_image_8,
        R.id.env_check_image_9,
        R.id.env_check_image_10
    };

    private final List<ImageView> envCheckImageViewList = new ArrayList<>();

    private Drawable okIcon;
    private Drawable detectedIcon;

    /**
     * The current activity used to implement the static callback.
     */
    private static EnvironmentCheckActivity context;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.main);

        okIcon       = getResources().getDrawable(R.drawable.ic_empty_grey);
        detectedIcon = getResources().getDrawable(R.drawable.ic_tick_red);

        for (int i = 0; i < imageViewIds.length; i++)
        {
            ImageView imageView = (ImageView) findViewById(imageViewIds[i]);
            envCheckImageViewList.add(imageView);
        }

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
     * This utility class performs debug detection, emulator detection, and
     * root detection, and sets up the view. If the environment is okay, the
     * application runs normally and displays "The environment is okay".
     * Otherwise, it displays information about the environment.
     *
     * We're putting this functionality in a separate class, so we can run it
     * in the background and so we can encrypt it, as an extra layer of
     * protection around the tamper detection and some essential code. We
     * can't encrypt the activity itself, for technical reasons, but an inner
     * class or another class are fine.
     *
     * The Delegate class is implemented as an asynchronous task.
     * This way we ensure that the little overhead, introduced by the
     * environment checks, does not affect the main application thread.
     *
     * Based on the nature of the checks, we have divided them into two categories, i.e., one time
     * checks and recurrent checks. The one time checks can be used at the start of the application
     * while the recurrent checks should be used at multiple places in the code, specifically before
     * performing a security sensitive activity.
     */
    private class Delegate extends AsyncTask<Void,Integer,Boolean>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            for (ImageView imageView : envCheckImageViewList) {
                imageView.setImageDrawable(null);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            ImageView imageView = envCheckImageViewList.get(values[0]);
            imageView.setImageDrawable(values[1] == 0 ? okIcon : detectedIcon);
        }

        /**
         * This method will run in a separate thread.
         */
        @Override
        protected Boolean doInBackground(Void... voids)
        {
            // We need a context for most methods.
            final Context context = EnvironmentCheckActivity.this;

            // You can pick your own value or values for OK, to make the code less predictable.
            final int OK = 1;

            // Let the DexGuard runtime library detect whether the application is debuggable. The
            // return code equals OK if it is not.
            // Nature of the check: One time.
            int isDebuggable = DebugDetector.isDebuggable(context, OK);
            publishProgress(0, isDebuggable == OK ? 0 : 1);

            // Let the DexGuard runtime library block native debuggers. The return code equals 0 if
            // debuggers are blocked successfully.
            // Nature of the check: One time (can be called multiple times only if the callback method
            // needs to be updated.)
            int isDebuggerBlocked = DebugBlocker.blockDebugger(new AttackHandlerExample());
            publishProgress(1, isDebuggerBlocked == 0 ? 0 : 1);

            // Let the DexGuard runtime library detect whether the a debugger is attached to the
            // application. The return code is OK if not so.
            // Nature of the check: Recurrent.
            int isDebuggerConnected = DebugDetector.isDebuggerConnected(OK);
            publishProgress(2, isDebuggerConnected == OK ? 0 : 1);

            // Let the DexGuard runtime library detect whether the app is signed with a debug key.
            // The return code equals OK if not so.
            // Nature of the check: One time.
            int isSignedWithDebugKey = DebugDetector.isSignedWithDebugKey(context, OK);
            publishProgress(3, isSignedWithDebugKey == OK ? 0 : 1);

            // Let the DexGuard runtime library detect whether the app is running in an emulator.
            // The return code is OK if not so.
            // Nature of the check: One time.
            int isRunningInEmulator = EmulatorDetector.isRunningInEmulator(context, OK);
            publishProgress(4, isRunningInEmulator == OK ? 0 : 1);

            // Let the DexGuard runtime library detect whether the app is running on a rooted device.
            // The check is performed in an asynchronous fashion, and the results are handled
            // in the callback.
            // We could pass additional flags to reduce possible false positives: SILENT, NO_TRICK_APPS,
            // NO_CIRCUMSTANTIAL, NO_FAIL_ON_HOOKING, IGNORE_BINARY_EXISTENCE, NO_ROOT_MANAGERS, FAST.
            // Nature of the check: One time.
            RootDetector.checkDeviceRooted(context, OK, EnvironmentCheckActivity::rootDetectionCallback);

            // Let the DexGuard runtime library detect whether the application is being hooked.
            // The return code is OK if not so.
            // Nature of the check: Recurrent.
            int isApplicationHooked = HookDetector.isApplicationHooked(context, OK);
            publishProgress(6, isApplicationHooked == OK ? 0 : 1);

            // The FAST flag can be used for faster hook detection.
            int isApplicationHookedFast = HookDetector.isApplicationHooked(context, OK, HookDetector.FAST);
            publishProgress(7, isApplicationHookedFast == OK ? 0 : 1);

            // Let the DexGuard runtime library detect if the application is being run inside a
            // virtual environment. The return code is OK if not so.
            // Nature of the check: One time.
            int isRunningInVirtualEnvironment = VirtualEnvironmentDetector.isRunningInVirtualEnvironment(context, OK);
            publishProgress(8, isRunningInVirtualEnvironment == OK ? 0 : 1);

            int isCertificateTampered = CertificateChecker.checkCertificate(context, OK);
            publishProgress(9, isCertificateTampered == OK ? 0 : 1);

            return true;
        }
    }

    /**
     * The callback to execute when the root check completes. The device is rooted when
     * okValue != returnValue.
     *
     * @param okValue     the OK value provided to checkDeviceRooted().
     * @param returnValue the return value computed by the root detector.
     */
    private static void rootDetectionCallback(int okValue, int returnValue)
    {
        if (context != null)
        {
            context.updateRootImageView(okValue == returnValue);
        }
    }

    /**
     * Updates the root image view on the UI thread.
     */
    private void updateRootImageView(final boolean result)
    {
        runOnUiThread(() -> envCheckImageViewList.get(5)
                .setImageDrawable(result ? context.okIcon : context.detectedIcon));
    }
}

/**
 * This class implements the DebugBlocker.OnAttackListener interface to provide a callback method.
 */
class AttackHandlerExample implements DebugBlocker.OnAttackListener
{
    public void onAttack()
    {
        // Here you should implement the expected app behavior in case of an attack on the DebugBlocker.
        Log.d("DebugBlocker", "Attack on the debug blocker is detected.");
    }
}

