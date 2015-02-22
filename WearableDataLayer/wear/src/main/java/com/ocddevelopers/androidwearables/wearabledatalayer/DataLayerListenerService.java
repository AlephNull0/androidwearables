package com.ocddevelopers.androidwearables.wearabledatalayer;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataLayerListenerService extends WearableListenerService {
    private static final String START_ACTIVITY_PATH = "/start/CounterActivity";
    private static final String START_CONFIRM_ACTIVITY_PATH = "/start_confirm/CounterActivity";
    private static final String CONFIRMATION_PATH = "/confirm/CounterActivity";
    private static final String IMAGE_ASSET_KEY = "preview";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        String path = messageEvent.getPath();
        Log.e("RCV", path);

        if(START_ACTIVITY_PATH.equals(path)) {
            Intent intent = new Intent(this, CounterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if(START_CONFIRM_ACTIVITY_PATH.equals(path)) {
            Intent intent = new Intent(this, CounterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            confirmStarted(messageEvent.getSourceNodeId());
        }
    }

    private void confirmStarted(String sourceNodeId) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult = googleApiClient.blockingConnect(10, TimeUnit.SECONDS);
        if(!connectionResult.isSuccess()) {
            Log.e("DK", "Failed to connect to GoogleApiClient.");
            return;
        }

        Wearable.MessageApi.sendMessage(googleApiClient, sourceNodeId, CONFIRMATION_PATH, null);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        final List<DataEvent> events = FreezableUtils
                .freezeIterable(dataEvents);

        for(DataEvent dataEvent : events) {
            DataItem dataItem = dataEvent.getDataItem();
            String path = dataItem.getUri().getPath();

            if ("/image".equals(path)) {
                PutDataRequest request = PutDataRequest.createFromDataItem(dataItem);
                Asset profileAsset = request.getAsset(IMAGE_ASSET_KEY);

                Intent intent = new Intent(this, ImagePreviewActivity.class);
                intent.putExtra("asset", profileAsset);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

}
