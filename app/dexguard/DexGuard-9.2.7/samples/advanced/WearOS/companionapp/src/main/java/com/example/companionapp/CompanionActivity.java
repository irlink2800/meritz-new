package com.example.companionapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CompanionActivity extends Activity {

    private static final String SEND_MESSAGE = "Send message";
    private static final String MESSAGE = "Hello from companion app!";
    private static final String RECEIVE_MESSAGE_CAPABILITY = "receive_message";
    private static final String RECEIVE_MESSAGE_PATH = "/receive_message";

    private String receiveMessageNodeId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.send_button);
        button.setText(SEND_MESSAGE);

        button.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                setupSendMessage();
                deliverMessage();
            } catch (Exception e) {
                Log.e("CompanionActivity", "Unable to deliver message to Wear app.");
            }
        });
    }

    // Find a connected node that can receive our message.
    private void setupSendMessage() throws ExecutionException, InterruptedException {
        CapabilityInfo capabilityInfo = Tasks.await(
                Wearable.getCapabilityClient(this).getCapability(
                        RECEIVE_MESSAGE_CAPABILITY, CapabilityClient.FILTER_REACHABLE));
        updateReceiveMessageCapability(capabilityInfo);
    }

    private void updateReceiveMessageCapability(CapabilityInfo capabilityInfo) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();
        receiveMessageNodeId = pickBestNodeId(connectedNodes);
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily.
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    // Deliver the message to the node.
    private void deliverMessage() {
        if (receiveMessageNodeId != null) {
            Wearable.getMessageClient(this).sendMessage(
                    receiveMessageNodeId, RECEIVE_MESSAGE_PATH, MESSAGE.getBytes());
        }
    }
}
