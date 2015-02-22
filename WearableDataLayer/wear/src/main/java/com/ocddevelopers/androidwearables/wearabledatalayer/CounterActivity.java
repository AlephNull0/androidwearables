package com.ocddevelopers.androidwearables.wearabledatalayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class CounterActivity extends Activity {

    private TextView mCountText;
    private GoogleApiClient mGoogleApiClient;
    int mCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        mCountText = (TextView) findViewById(R.id.count);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        Wearable.DataApi.removeListener(mGoogleApiClient, mDataListener);
        super.onStop();
    }

    public void onIncrementClick(View view) {
        ++mCount;
        updateCount();
    }

    private void updateCount() {
        mCountText.setText(Integer.toString(mCount));

        PutDataMapRequest updateCountDataMapRequest = PutDataMapRequest.create("/counter");
        updateCountDataMapRequest.getDataMap().putInt("count", mCount);
        PutDataRequest putDataRequest = updateCountDataMapRequest.asPutDataRequest();

        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {

            }
        });

    }

    public void onDecrementClick(View view) {
        --mCount;
        updateCount();
    }

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Wearable.DataApi.addListener(mGoogleApiClient, mDataListener);
        }

        @Override
        public void onConnectionSuspended(int cause) {

        }
    };

    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener =
            new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
        }
    };

    private DataApi.DataListener mDataListener = new DataApi.DataListener() {
        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            final List<DataEvent> events = FreezableUtils
                    .freezeIterable(dataEvents);
            for(DataEvent dataEvent : events) {
                DataItem dataItem = dataEvent.getDataItem();
                if("/counter".equals(dataItem.getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
                    DataMap dataMap = dataMapItem.getDataMap();
                    int count = dataMap.getInt("count");
                    mCount = count;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCountText.setText(Integer.toString(mCount));
                        }
                    });
                }
            }
        }
    };

}
