/*
 * Sample application to illustrate tamper detection with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

import com.guardsquare.dexguard.runtime.detection.*;

/**
 * Sample activity that displays "Hello, world!". It displays a different
 * message if someone has tampered with the application archive after it has
 * been created by DexGuard.
 *
 * You can experiment with it by first building, installing, and trying the
 * original version:
 *   ant release install
 * You can then tamper with it in some trivial way:
 *   zipalign -f 4 bin/HelloWorld-release.apk HelloWorld-tampered.apk
 *   adb install -r HelloWorld-tampered.apk
 * If you try the application again, you'll see that it detects that it has
 * been modified.
 *
 * Note: some custom firmwares for rooted devices (notably "AvatarRom") apply
 * zipalign when the application is installed, thus triggering the tamper
 * detection. This may or may not be desirable for your application.
 */
public class HelloWorldActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        new Delegate().checkAndInitialize();
    }


    /**
     * This utility class performs the tamper detection and creates the view.
     *
     * We're putting this functionality in a separate class so we can encrypt
     * it, as an extra layer of protection around the tamper detection and
     * some essential code. We can't encrypt the activity itself, for
     * technical reasons, but an inner class or another class are fine.
     */
    private class Delegate
    {
        public void checkAndInitialize()
        {
            // We need a context for most methods.
            final Context context = HelloWorldActivity.this;

            // You can pick your own value or values for OK,
            // to make the code less predictable.
            final int OK = 1;

            // Let the DexGuard runtime library detect whether the apk has
            // been modified or repackaged in any way (with jar, zip,
            // jarsigner, zipalign, or any other tool), after DexGuard has
            // packaged it. The return value is the value of the optional
            // integer argument OK (default=0) if the apk is unchanged.
            int apkChanged =
                TamperDetector.checkApk(context, OK);

            // Let the DexGuard runtime library detect whether the apk has
            // been re-signed with a different certificate, after DexGuard has
            // packaged it.  The return value is the value of the optional
            // integer argument OK (default=0) if the certificate is still
            // the same.
            int certificateChanged =
                CertificateChecker.checkCertificate(context, OK);

            // You can also explicitly pass the SHA-256 hash of a certificate,
            // if the application is only signed after DexGuard has packaged
            // it.
            // You can print out the SHA-256 hash of the certificate of your
            // key store with
            //   keytool -list -v -keystore my.keystore
            //
            // If you want to extract the SHA-256 hash from an earlier version
            // of the application, you can print it out with
            //   keytool -printcert -v -jarfile my.apk
            //
            // If you are publishing on the Amazon Store, you can find the
            // SHA-256 hash in
            //   Amazon Apps & Games Developer Portal
            //     > Binary File(s) Tab > Settings > My Account.
            //
            // With your SHA-256 hash, you can then use one of
            // CertificateChecker.checkCertificate(context,
            //   "EB:B0:FE:DF:19:42:A0:99:B2:87:C3:DB:00:FF:73:21:" +
            //   "62:15:24:81:AB:B2:B6:C7:CB:CD:B2:BA:58:94:A7:68", OK);
            // CertificateChecker.checkCertificate(context,
            //   "EBB0FEDF1942A099B287C3DB00FF7321" +
            //   "62152481ABB2B6C7CBCDB2BA5894A768", OK);
            // CertificateChecker.checkCertificate(context,
            //   0xEBB0FEDF, 0x1942A099, 0xB287C3DB, 0x00FF7321,
            //   0x62152481, 0xABB2B6C7, 0xCBCDB2BA, 0x5894A768, OK);
            // CertificateChecker.checkCertificate(context,
            //   0xEBB0FEDF1942A099L, 0xB287C3DB00FF7321L,
            //   0x62152481ABB2B6C7L, 0xCBCDB2BA5894A768L, OK);
            //
            // If you specify a string, you should make sure it is encrypted,
            // for good measure.

            // Let the DexGuard runtime library determine whether the primary
            // DEX has been modified, after DexGuard has packaged the APK. The
            // return value is the value of the optional integer argument OK
            // (default=0) if the file is still the same.
            //
            // You can check any file, except for files in the META-INF
            // directory. When creating app bundles, you also can't check the
            // resources.arsc, AndroidManifest.xml, and other xml files, since
            // bundletool will still manipluate them when creating the final
            // APK.
            //
            FileChecker fileChecker = new FileChecker(context);

            int primaryDexChanged =
                fileChecker.checkFile("classes.dex", OK);

            // we can also simply check all files.
            int anyFileChanged =
                fileChecker.checkAllFiles(OK);

            // Display a message.
            TextView view = new TextView(context);
            view.setText(apkChanged         == OK &&
                         certificateChanged == OK &&
                         primaryDexChanged  == OK &&
                         anyFileChanged     == OK ?
                             "Hello, world!" :
                             "Tamper alert!");
            view.setGravity(Gravity.CENTER);

            // Change the background color if someone has tampered with the
            // application archive.
            if (apkChanged         != OK ||
                certificateChanged != OK ||
                primaryDexChanged  != OK)
            {
                view.setBackgroundColor(Color.RED);
            }

            setContentView(view);

            // Briefly display a comment.
            String comment =
                primaryDexChanged  != OK ? "The primary DEX has been modified"               :
                anyFileChanged     != OK ? "Some file in the APK has been modified"          :
                certificateChanged != OK ? "The certificate is not the expected certificate" :
                apkChanged         != OK ? "The application archive has been modified"       :
                                           "The application has not been modified";

            Toast.makeText(context, comment, Toast.LENGTH_LONG).show();
        }
    }
}
