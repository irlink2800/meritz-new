/*
 * Sample application to illustrate processing with DexGuard.
 *
 * Copyright (c) 2012-2018 Guardsquare NV
 */
package com.example;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.google.gson.*;

import java.text.DecimalFormat;

import static android.view.MotionEvent.ACTION_DOWN;

/**
 * Sample activity that serializes and deserializes JSON of increasing sizes
 * and prints out the time it took to execute.
 */
public class GsonSampleActivity extends Activity
{
    public static final Integer[] COMPANY_SIZES = new Integer[]{
        1_000,
        2_500,
        5_000,
        10_000,
        25_000,
        50_000,
    };
    private TextView view;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Display the message.
        view = new TextView(this);
        view.setMovementMethod(new ScrollingMovementMethod());
        view.setGravity(Gravity.CENTER);
        view.setTextSize(18f);
        view.setText("Tap to start benchmark");
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == ACTION_DOWN) {
                    view.setText("Please wait...");
                    view.setOnTouchListener(null);
                    new BenchmarkTask().execute(COMPANY_SIZES);
                    return true;
                }
                return false;
            }
        });
        setContentView(view);

        // Briefly display a comment.
        Toast.makeText(this, "DexGuard has processed the GSON code in this sample", Toast.LENGTH_LONG).show();
    }

    private class BenchmarkTask extends AsyncTask<Integer,Integer,String>
    {
        @Override
        protected String doInBackground(Integer... companySizes) {
            // Measure GSON creation time.
            long startCreateGson = System.currentTimeMillis();
            Gson gson = new GsonBuilder().create();
            long gsonCreationTime = System.currentTimeMillis() - startCreateGson;

            int[]  jsonSizes     = new int[companySizes.length];
            long[] toJsonTimes   = new long[companySizes.length];
            long[] fromJsonTimes = new long[companySizes.length];

            for (int sizeIndex = 0; sizeIndex < companySizes.length; sizeIndex++) {
                // Generate domain model of a company with the specified size.
                int companySize = COMPANY_SIZES[sizeIndex];
                SoftwareDeveloper[] employees = new SoftwareDeveloper[companySize];
                for (int i = 0; i < employees.length; i++) {
                    Address homeAddress = new Address(
                        15,
                        "Brusselsestraat",
                        "Leuven",
                        "3000",
                        "Belgium");
                    employees[i] = new SoftwareDeveloper(homeAddress,
                                                         new Name("Mary", "Smith"),
                                                         Sex.FEMALE,
                                                         "Android developer",
                                                         1234.56,
                                                         "Java");
                }

                Address officeAddress = new Address(
                    362,
                    "Tervuursestraat",
                    "Leuven",
                    "3000",
                    "Belgium");

                Company company = new Company("GuardSquare", employees, officeAddress);

                // Serialize domain object.
                long startToJson = System.currentTimeMillis();
                String json = gson.toJson(company);
                jsonSizes[sizeIndex] = json.length();
                toJsonTimes[sizeIndex] = System.currentTimeMillis() - startToJson;

                // Deserialize domain object.
                long startFromJson = System.currentTimeMillis();
                gson.fromJson(json, Company.class);
                fromJsonTimes[sizeIndex] = System.currentTimeMillis() - startFromJson;
            }

            // Print out GSON instance creation time.
            DecimalFormat jsonSizeFormat = new DecimalFormat();
            jsonSizeFormat.setGroupingUsed(true);
            jsonSizeFormat.setGroupingSize(3);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("  Gson creation time: ")
                .append(gsonCreationTime)
                .append("ms\n\n");

            // Print out serialization and deserialization time for each size
            // of JSON.
            for (int sizeIndex = 0; sizeIndex < jsonSizes.length; sizeIndex++) {
                int jsonSize = jsonSizes[sizeIndex];
                long toJsonTime = toJsonTimes[sizeIndex];
                long fromJsonTime = fromJsonTimes[sizeIndex];
                String formattedJsonSize = jsonSizeFormat.format(jsonSize);
                stringBuilder.append("  JSON size: ").append(formattedJsonSize).append(" bytes\n");
                stringBuilder.append("      Serializing took ").append(toJsonTime).append("ms\n");
                stringBuilder.append("      Deserializing took ").append(fromJsonTime).append("ms\n\n");
            }

            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // Update text view when benchmark has finished executing.
            view.setGravity(Gravity.LEFT);
            view.setText(result);
        }
    }
}
