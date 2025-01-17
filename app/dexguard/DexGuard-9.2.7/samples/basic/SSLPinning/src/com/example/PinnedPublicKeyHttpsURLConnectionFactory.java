/*
 * Sample application to illustrate SSL pinning with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.content.Context;

import com.guardsquare.dexguard.runtime.net.SSLPinner;

/**
 * This sample class creates a javax.net.ssl.HttpsURLConnection with public key
 * pinning.
 *
 * If your application connects to an existing external server, you can
 * print out Java code with the SHA-256 hashes of the keys of the server, with
 * DexGuard's public key pinning tool. For example, for www.wikipedia.org:
 *
 *     bin/hash_certificate.sh "https://www.wikipedia.org"
 *
 * You can then copy/paste the Java code from the output, to create an instance
 * of com.guardsquare.dexguard.runtime.net.PublicKeyTrustManager for the given
 * server(s).
 *
 *
 * On the other hand, if you have your own X509 certificates, you can print
 * out the public keys, for instance with:
 *
 *     openssl x509 -inform pem -in wikipedia.pem -pubkey -noout
 * or
 *     openssl x509 -inform der -in wikipedia.der -pubkey -noout
 *
 * You can then print out the Java code with the SHA-256 hashes of these public
 * keys, for instance with:
 *
 *     bin/hash_certificate.sh "MII.....lHy"
 *
 * The tool ignores spaces and newlines in the public key strings, for easier
 * copy/pasting. You can then again copy/paste the Java code from the output,
 * to create an instance of
 * com.guardsquare.dexguard.runtime.net.PublicKeyTrustManager for the given
 * key(s).
 *
 * It is also possible to use the tool for multiple servers or public keys at once:
 *
 *     bin/hash_certificate.sh "https://wwww.wikipedia.org" "MII.....lHy" ...
 */
public class PinnedPublicKeyHttpsURLConnectionFactory
{
    private final SSLPinner sslPinner;

    public PinnedPublicKeyHttpsURLConnectionFactory(Context context)
    throws Exception
    {
        // Code copied from running tools/server_public_key_pinning_code.sh.
        // In this example, we are choosing to only trust and pin the public
        // key of wikipedia.org.

        // Create a TrustManager that only accepts servers with the specified public key hashes.
        com.guardsquare.dexguard.runtime.net.PublicKeyTrustManager trustManager =
            new com.guardsquare.dexguard.runtime.net.PublicKeyTrustManager(
                new String[] {
                    // CN=*.wikipedia.org, O="Wikimedia Foundation, Inc.", L=San Francisco, ST=California, C=US
                    "06AB2F44F7F01DA153D1584952D1D2A9AD6F87BA0827A1E8036BED4B12457446",
                    // CN=DigiCert TLS Hybrid ECC SHA384 2020 CA1, O=DigiCert Inc, C=US
                    "7B4211CF94E2A37180D57B387D4556987D711C3887D9D31B56D0814A438876A3",
                }
                //, new TrustStoreFactory(context).createTrustStore()
                // Optional trust store containing trusted certificates
            );

        this.sslPinner = new SSLPinner(trustManager);
    }

    public HttpsURLConnection createHttpsURLConnection(String urlString)
    throws IOException
    {
        // Create the URL connection.
        URL url = new URL(urlString);
        HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
        if (!sslPinner.pinHttpsURLConnection(urlConnection))
        {
            throw new RuntimeException("SSL pinning attempt failed, application probably hooked.");
        }

        return urlConnection;
    }
}
