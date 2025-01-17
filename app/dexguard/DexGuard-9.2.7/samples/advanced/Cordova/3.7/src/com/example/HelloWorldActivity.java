/*
 * Sample application to illustrate Cordova asset encryption with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;

import android.os.Bundle;
import com.guardsquare.dexguard.runtime.encryption.EncryptedCordovaWebViewClient;

/**
 * Sample activity that shows a Cordova web view.
 *
 * @deprecated This sample has been replaced with more recent Cordova samples and may
 *             be removed from future releases.
 */
public class HelloWorldActivity extends CordovaActivity 
{
    // An arbitrary http URL prefix to refer to local assets
    // (encrypted or not).
    private static final String ENCRYPTED_ASSET_PREFIX = "file://_/";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // Start Cordova with the main page.
        super.onCreate(savedInstanceState);
        super.init();
        super.loadUrl(ENCRYPTED_ASSET_PREFIX + "www/index.html");
    }

    @Override
    protected CordovaWebViewClient makeWebViewClient(CordovaWebView webView) {  
        // Create a web view client for encrypted assets.
        return new EncryptedCordovaWebViewClient(super.makeWebViewClient(webView), 
                                                 this,
                                                 ENCRYPTED_ASSET_PREFIX);
    }
}

