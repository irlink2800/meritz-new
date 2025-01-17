/*
 * Sample application to illustrate SafetyNet with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.os.*;
import android.util.*;
import android.view.View;
import android.widget.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.safetynet.*;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;

public class SafetyNetDemoActivity extends Activity
{
    private static final String TAG = "SafetyNetDemo";

    /**
     * Copy your own API key here. More information can be found on:
     * https://developer.android.com/training/safetynet/index.html
     */
    private static final String KEY = "<YOUR_API_KEY>";

    /**
     * Build the HTTP Post URL
     */
    private static final String URL =
        "https://www.googleapis.com/androidcheck/v1/attestations/verify?key=" + KEY;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button status = (Button)findViewById(R.id.status);
        status.setOnClickListener((evt) -> useSafetyNet());
    }


    private void useSafetyNet()
    {
        TextView txtView = (TextView)findViewById(R.id.textView1);
        txtView.setText(R.string.performing_call);
        new Delegate(txtView).execute();
    }


    private class Delegate extends AsyncTask<Void, Void, String>
    {
        private TextView txtView;


        public Delegate(TextView view)
        {
            this.txtView = view;
        }


        /**
         * This method will run in a separate thread. We will perform the remote attestation call
         * to the SafetyNet API
         */
        @Override
        protected String doInBackground(Void... voids)
        {

            GoogleApiClient apiClient = new GoogleApiClient.Builder(SafetyNetDemoActivity.this)
                .addApi(SafetyNet.API)
                .build();

            ConnectionResult cr = apiClient.blockingConnect();

            //Check if we successfully performed a request.
            if (!cr.isSuccess())
            {
                Log.d(TAG, String.valueOf(cr.getErrorCode()));
            }

            byte[] nonce = generateSecureNonce();

            SafetyNetApi.AttestationResult attestationResult =
                SafetyNet.SafetyNetApi.attest(apiClient, nonce).await();

            com.google.android.gms.common.api.Status status = attestationResult.getStatus();

            if (status.isSuccess())
            {
                String encodedJWT = attestationResult.getJwsResult();
                return validateJWS(encodedJWT, nonce);
            }
            else
            {
                Log.d(TAG, "SafetyNet Status = failed");
                return "API call failed";
            }
        }


        /**
         * Validating the JWS happens in three steps:
         * <p>
         * 1. Validate with Google that the JWS is sent by them
         * 2. Check that the nonce is the same
         * 3. Check the response of SafetyNet: compatible or not
         */
        private String validateJWS(String jws, byte[] usedNonce)
        {
            try
            {
                // Check with Google.
                if (!isNotValidGoogleResponse(jws))
                {
                    return getString(R.string.invalid_jws);
                }

                // Decode the JWS.
                String[]   decoded = decodeJws(jws);
                JSONObject body    = new JSONObject(decoded[1]);

                // Extract the nonce used in the response.
                byte[] returnedNonce = body.getString("nonce").getBytes();
                usedNonce = Base64.encode(usedNonce, Base64.DEFAULT);

                // Check if the correct nonce has been used in the response.
                if (Arrays.equals(returnedNonce, usedNonce))
                {
                    return getString(R.string.wrong_nonce);
                }

                // Check the actual SafetyNet Response.
                return body.getBoolean("ctsProfileMatch") ? "Compatible" : "Incompatible";
            }
            catch (JSONException e)
            {
                return "JSON Error";
            }
            catch (IOException e)
            {
                return "Could not contact Google";
            }
        }


        /**
         * Ask Google to validate the returned JWS.
         **/
        private boolean isNotValidGoogleResponse(String jws) throws IOException, JSONException
        {
            // Prepare the JSON message.
            JSONObject   jsonMessage = new JSONObject("{ 'signedAttestation': '" + jws + "' }");
            StringEntity se          = new StringEntity(jsonMessage.toString());

            // Perform the POST request.
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost   httpPost   = new HttpPost(URL);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(se);
            HttpResponse response   = httpclient.execute(httpPost);
            StatusLine   statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK)
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                JSONObject answer = new JSONObject(out.toString());
                out.close();

                return answer.getBoolean("isValidSignature");

            }
            else
            {
                // Close the connection.
                response.getEntity().getContent().close();

                return false;
            }
        }


        /**
         * Decode the JWS.
         */
        private String[] decodeJws(String jws)
        {
            // The JWS consists of three Base64 encoded parts
            // seperated by a '.'
            final String[] jwtParts = jws.split("\\.");

            if (jwtParts.length == 3)
            {

                for (int i = 0; i < 3; i++)
                {
                    jwtParts[i] = new String(Base64.decode(jwtParts[i].trim(), Base64.DEFAULT));
                }

                return jwtParts;
            }
            else
            {
                return null;
            }
        }


        /**
         * The SafetyNet API requires a minimum 16-bytes secure random generated nonce.
         */
        private byte[] generateSecureNonce()
        {
            SecureRandom random = new SecureRandom();
            byte[]       bytes  = new byte[32];
            random.nextBytes(bytes);

            return bytes;
        }


        /**
         * This method is automatically called after the background thread has
         * finished executing. The result is passed as a parameter.
         */
        @Override
        protected void onPostExecute(final String result)
        {
            this.txtView.setText(result);
        }
    }

}
