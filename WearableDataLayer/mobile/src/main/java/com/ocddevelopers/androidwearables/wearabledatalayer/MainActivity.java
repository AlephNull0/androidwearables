package com.ocddevelopers.androidwearables.wearabledatalayer;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class MainActivity extends Activity {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String ERROR_FRAGMENT_TAG = "errordialog";
    private static final String START_ACTIVITY_PATH = "/start/CounterActivity";
    private static final String START_CONFIRM_ACTIVITY_PATH = "/start_confirm/CounterActivity";
    private static final String CONFIRMATION_PATH = "/confirm/CounterActivity";
    private static final String COUNTER_PATH = "/counter";
    private static final String TAKE_IMAGE_PATH = "/image";
    private static final String IMAGE_ASSET_KEY = "preview";
    private static final String KEY_COUNT = "count";
    private static final String TAG = "MainActivity";
    private boolean mResolvingError = false;
    private NumberPicker mNumberPicker;
    private GoogleApiClient mGoogleApiClient;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNumberPicker = (NumberPicker) findViewById(R.id.counter);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(100);
        mNumberPicker.setOnValueChangedListener(mValueChangeListener);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .build();

        // disable buttons until GoogleApiClient is connected
        setButtonsEnabled(false);
    }

    private void setButtonsEnabled(boolean enabled) {
        findViewById(R.id.start_wearable).setEnabled(enabled);
        findViewById(R.id.start_confirm_wearable).setEnabled(enabled);
        findViewById(R.id.send_image).setEnabled(enabled);
    }

    private NumberPicker.OnValueChangeListener mValueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            PutDataMapRequest updateCounterDataMapRequest = PutDataMapRequest.create(COUNTER_PATH);
            updateCounterDataMapRequest.getDataMap().putInt(KEY_COUNT, newVal);
            PutDataRequest putDataRequest = updateCounterDataMapRequest.asPutDataRequest();

            PendingResult<DataApi.DataItemResult> pendingResult =
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
            pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    String success = (dataItemResult.getStatus().isSuccess())? "success" : "error";
                    Log.d(TAG, "counter update: " + success);
                }
            });
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if(!mResolvingError) {
            Wearable.DataApi.removeListener(mGoogleApiClient, mDataListener);
            Wearable.MessageApi.removeListener(mGoogleApiClient, mMessageListener);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    public void onStartWearableCounterClick(View view) {
        getConnectedNodes().setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for(Node node : getConnectedNodesResult.getNodes()) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),
                            START_ACTIVITY_PATH, null);
                }
            }
        });
    }

    public void onStartConfirmWearableCounterClick(View view) {
        getConnectedNodes().setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for(Node node : getConnectedNodesResult.getNodes()) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),
                            START_CONFIRM_ACTIVITY_PATH, null);
                }
            }
        });
    }

    private PendingResult<NodeApi.GetConnectedNodesResult> getConnectedNodes() {
        return Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
    }

    public void onSendImageClick(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "wear_image";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap imageBitmap = getPicture();
            Asset asset = createAssetFromBitmap(imageBitmap);
            PutDataRequest request = PutDataRequest.create(TAKE_IMAGE_PATH);
            request.putAsset(IMAGE_ASSET_KEY, asset);
            Wearable.DataApi.putDataItem(mGoogleApiClient, request);
        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    // adapted from http://developer.android.com/training/camera/photobasics.html
    private Bitmap getPicture() {
        // the desired dimensions of the View
        int targetW = 400;
        int targetH = 400;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Wearable.DataApi.addListener(mGoogleApiClient, mDataListener);
            Wearable.MessageApi.addListener(mGoogleApiClient, mMessageListener);
            setButtonsEnabled(true);
        }

        @Override
        public void onConnectionSuspended(int cause) {
            setButtonsEnabled(false);
        }
    };

    private DataApi.DataListener mDataListener = new DataApi.DataListener() {
        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            final List<DataEvent> events = FreezableUtils
                    .freezeIterable(dataEvents);

            for(DataEvent dataEvent : events) {
                DataItem dataItem = dataEvent.getDataItem();
                if(COUNTER_PATH.equals(dataItem.getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
                    DataMap dataMap = dataMapItem.getDataMap();
                    final int count = dataMap.getInt(KEY_COUNT);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mNumberPicker.setValue(count);
                        }
                    });
                }
            }
        }
    };

    private MessageApi.MessageListener mMessageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            if(CONFIRMATION_PATH.equals(messageEvent.getPath())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Wearable CounterActivity started",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener =
            new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult connectionResult) {
                    if (mResolvingError) {
                        // Already attempting to resolve an error.
                        return;
                    } else if (connectionResult.hasResolution()) {
                        try {
                            mResolvingError = true;
                            connectionResult.startResolutionForResult(MainActivity.this,
                                    REQUEST_RESOLVE_ERROR);
                        } catch (IntentSender.SendIntentException e) {
                            // There was an error with the resolution intent. Try again.
                            mGoogleApiClient.connect();
                        }
                    } else {
                        // Show dialog using GooglePlayServicesUtil.getErrorDialog()
                        showErrorDialog(connectionResult.getErrorCode());
                        mResolvingError = true;
                    }
                }
            };

    private void showErrorDialog(int errorCode) {
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
                this, REQUEST_RESOLVE_ERROR);

        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = ErrorDialogFragment.newInstance(errorDialog,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mResolvingError = false;
                    }
                });

        dialogFragment.show(getFragmentManager(), ERROR_FRAGMENT_TAG);
    }
}
