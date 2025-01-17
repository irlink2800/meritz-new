package com.example.wearapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.jetbrains.annotations.NotNull;

public class WearActivity extends Activity {

    private static final String MESSAGE = "Hello from wear app!";
    private TextView mMessageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMessageView = findViewById(R.id.message_view);
        mMessageView.setText(MESSAGE);

        Button mButton = findViewById(R.id.reset_button);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mMessageView.setText(MESSAGE);
            }

        });

        Wearable.getMessageClient(this).addListener(new MessageClient.OnMessageReceivedListener() {

            @Override
            public void onMessageReceived(@NonNull @NotNull MessageEvent messageEvent) {
                mMessageView.setText(new String(messageEvent.getData()));
            }

        });
    }
}
