package com.example;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView)findViewById(R.id.textView);

        if (BuildConfig.FLAVOR.equals("full"))
        {
            textView.setText("Hello real world");
        }
        else if (BuildConfig.FLAVOR.equals("demo"))
        {
            textView.setText("Hello demo world");

        }
    }

}

