/*
 * Sample application to illustrate SSL pinning with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.guardsquare.dexguard.runtime.net.SSLPinningWebViewClient;

/**
 * Sample activity that shows a web view with SSL pinning.
 */
public class PinningWebViewActivity extends Activity
{
    public static final String EXTRA_URL    = "extraUrl";
    public static final String EXTRA_HASHES = "extraHashes";


    /**
     * Convenience method to start a PinningWebViewActivity with
     * SSLCertificate verification.
     *
     * @param context   the application context.
     * @param url       URL to load.
     * @param keyHashes hashes of the public keys to accept.
     * @return intent to start the requested activity with.
     */
    public static Intent createPinningWebViewIntent(Context  context,
                                                    String   url,
                                                    String[] keyHashes)
    {
        Intent intent = new Intent(context, PinningWebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_HASHES, keyHashes);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get arguments.
        String   url       = getIntent().getStringExtra(EXTRA_URL);
        String[] keyHashes = getIntent().getStringArrayExtra(EXTRA_HASHES);

        // Create a web view with the main page.
        WebView webView = new WebView(this);

        // Get the WebViewClient.
        WebViewClient client = createPinningClient(keyHashes);

        webView.setWebViewClient(client);

        // Set layout to have the webview and load the content.
        webView.loadUrl(url);
        setContentView(webView);
    }


    /**
     * Creates and configures an SSLPinningWebViewClient.
     *
     * This implementation of the WebViewClient reroutes https requests to
     * check the SSL certificates against a predefined list of accepted
     * certificates. This approach has some drawbacks, the most significant
     * ones are:
     * - Requests can sometimes not be fully replicated, because of missing data
     *   e.g. HTTP headers and method,... (only applicable if API level < 21)
     * - The WebView is still under the impression it loaded the initial URL.
     *   This could cause base_url issues and have strange effects on relative
     *   paths in the webpage.
     */
    private WebViewClient createPinningClient(String[] publicKeyHashes)
    {
        // Set our desired certificates.
        SSLPinningWebViewClient webClient = new SSLPinningWebViewClient(publicKeyHashes);

        // Callback for failed certificates.
        webClient.addWrongCertificateListener(() -> {
            // Note that this callback is not forced on the UI thread in our
            // example. To interact with it, we need to force our code on
            // that thread manually.
            runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                           "Wrong certificate, page will not load.",
                           Toast.LENGTH_LONG).show());
        });

        return webClient;
    }
}
