/*
 * Sample application to illustrate processing with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.soloader.SoLoader;
import com.facebook.soloader.SoFileLoader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.util.Set;
import java.util.HashSet;


/**
 * Sample activity that writes an encrypted file and reads it back using Facebook's Conceal library.
 */
public class ConcealSampleActivity extends Activity
{
    private Crypto crypto = null;
    private Entity entity = null;

    private final String ENTITY_ID = "sample_id";
    private final String FILE      = "samplefile";


    /**
     * Initializes the SoLoader class to support encryption of native libraries
     * on all supported Android API levels.
     */
    private static void initSoLoader(Context context)
    {
        try
        {
            // Use a custom SoFileLoader in order to support encryption
            // of the loaded native libraries.
            SoLoader.init(context, 0, new SoFileLoader()
            {
                private Set<String> loadedLibraries = new HashSet<String>();

                @Override
                public void load(final String pathToSoFile, final int loadFlags)
                {
                    // Prevent recursive loading of the same library.
                    if (!loadedLibraries.contains(pathToSoFile))
                    {
                        loadedLibraries.add(pathToSoFile);
                        System.loadLibrary(getNativeLibraryName(pathToSoFile));
                    }
                }

                String getNativeLibraryName(String fileName)
                {
                    int startIndex = fileName.lastIndexOf("/lib");
                    return fileName.substring(startIndex < 0 ? 3 : startIndex + 4,
                                              fileName.lastIndexOf(".so"));
                }
            });
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        initSoLoader(this);

        // Creates a new Crypto object with default implementations of a key chain.
        crypto =
            AndroidConceal.get()
                          .createDefaultCrypto(new SharedPrefsBackedKeyChain(this,
                                                                             CryptoConfig.KEY_256));

        entity = new Entity(ENTITY_ID);

        File file = new File(getFilesDir(), FILE);

        // Write an ecrypted string to the file and read it back.
        String message;
        try
        {
            writeConcealTest(file, "This message was (de)(en)crypted!");
            message = readConcealTest(file);
        }
        catch (Exception e)
        {
            message = e.getMessage();
            e.printStackTrace();
        }

        // Display the message.
        TextView view = new TextView(this);
        view.setText(message);
        view.setGravity(Gravity.CENTER);
        setContentView(view);
    }


    private String readConcealTest(File file) throws Exception
    {
        // Check for whether the crypto functionality is available
        // This might fail if android does not load libaries correctly.
        if (crypto != null && !crypto.isAvailable())
        {
            return null;
        }

        // Get the file to which ciphertext has been written.
        FileInputStream fileStream = new FileInputStream(file);

        // Creates an input stream which decrypts the data as
        // it is read from it.
        InputStream inputStream = crypto.getCipherInputStream(
                fileStream,
                entity);

        // Read into a byte array.
        int read;
        byte[] buffer = new byte[1024];


        // You must read the entire stream to completion.
        // The verification is done at the end of the stream.
        // Thus not reading till the end of the stream will cause
        // a security bug.
        StringBuilder stringBuilder = new StringBuilder();
        while ((read = inputStream.read(buffer)) != -1) {
            stringBuilder.append(new String(buffer, "UTF-8"), 0, read);
        }

        inputStream.close();
        return stringBuilder.toString();
    }


    private void writeConcealTest(File file, String message) throws Exception
    {
        // Check for whether the crypto functionality is available
        // This might fail if android does not load libaries correctly.
        if (crypto != null && !crypto.isAvailable())
        {
            return;
        }

        OutputStream fileStream = new BufferedOutputStream(
                new FileOutputStream(file));

        // Creates an output stream which encrypts the data as
        // it is written to it and writes it out to the file.
        OutputStream outputStream = crypto.getCipherOutputStream(
                fileStream,
                entity);

        // Write plaintext to it.
        outputStream.write(message.getBytes());
        outputStream.close();
    }
}
