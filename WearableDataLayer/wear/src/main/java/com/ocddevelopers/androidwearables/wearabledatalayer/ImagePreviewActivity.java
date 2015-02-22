package com.ocddevelopers.androidwearables.wearabledatalayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class ImagePreviewActivity extends Activity {
    public static final String EXTRA_ASSET = "asset";
    private static final String TAG = "ImgPreview";
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImage = new ImageView(this);
        mImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        setContentView(mImage);

        final Asset asset = getIntent().getParcelableExtra(EXTRA_ASSET);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = loadBitmapFromAsset(asset);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImage.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }


    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult result =
                googleApiClient.blockingConnect(500, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }

        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                googleApiClient, asset).await().getInputStream();
        googleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }


}
