package com.ocddevelopers.androidwearables.glassuiessentials;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;

import java.util.Locale;

public class PomodoroServiceLowFreq extends Service {
    private static final int UPDATE_RATE_MS = 1000;
    private static final String LIVE_CARD_TAG = "PomodoroCard";
    private LiveCard mLiveCard;
    private RemoteViews mRemoteViews;
    private int mTimeRemaining;
    private Handler mHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(mLiveCard == null) {

            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            // get RemoteViews somehow
            mRemoteViews = new RemoteViews(getPackageName(), R.layout.pomodoro_timer);

            mTimeRemaining = 25 * 60;
            updateRemoteViews();

            // live cards must have a menu
            Intent menuIntent = new Intent(this, LowFreqPomodoroMenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

            mLiveCard.publish(LiveCard.PublishMode.REVEAL);

            mHandler = new Handler();

            mHandler.postDelayed(mRunnable, UPDATE_RATE_MS);
        }


        return START_STICKY;
    }

    private void updateRemoteViews() {
        int minutes = mTimeRemaining/60;
        int seconds = mTimeRemaining%60;
        mRemoteViews.setTextViewText(R.id.minutes, String.format(Locale.US, "%02d", minutes));
        mRemoteViews.setTextViewText(R.id.seconds, String.format(Locale.US, "%02d", seconds));
        mLiveCard.setViews(mRemoteViews);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            --mTimeRemaining;
            updateRemoteViews();

            if(mTimeRemaining > 0) {
                mHandler.postDelayed(mRunnable, UPDATE_RATE_MS);
            }
        }
    };

    @Override
    public void onDestroy() {
        if(mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
            mHandler.removeCallbacks(mRunnable);
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
