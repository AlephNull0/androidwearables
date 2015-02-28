package com.ocddevelopers.androidwearables.glassuiessentials;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.android.glass.timeline.LiveCard;

public class PomodoroServiceHighFreq extends Service {
    private static final String LIVE_CARD_TAG = "PomodoroCard";
    public static final String ACTION_STOP = "com.ocd.dev.androidwearables.chapter8.action.STOP";
    public static final String ACTION_RESET = "com.ocd.dev.androidwearables.chapter8.action.RESET";
    private LiveCard mLiveCard;
    private TimerRenderer mTimerRenderer;
    private PomodoroBinder mBinder = new PomodoroBinder();

    public class PomodoroBinder extends Binder {
        public void reset() {
            if(mTimerRenderer != null) {
                mTimerRenderer.reset();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);

        // don't restart if something goes wrong
        return START_NOT_STICKY;
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if(ACTION_STOP.equals(action)) {
            stopSelf();
            mTimerRenderer.stop();
        } else if(ACTION_RESET.equals(action)) {
            mTimerRenderer.reset();
        } else {
            publishCard();
            mTimerRenderer.start();
        }
    }

    @Override
    public void onDestroy() {
        unpublishCard();
        super.onDestroy();
    }

    private void publishCard() {
        if(mLiveCard == null) {
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            // live cards must have a menu
            Intent menuIntent = new Intent(this,HighFreqPomodoroMenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

            mTimerRenderer = new TimerRenderer(this);
            mLiveCard.setDirectRenderingEnabled(true);
            mLiveCard.getSurfaceHolder().addCallback(mTimerRenderer);
            mLiveCard.publish(LiveCard.PublishMode.REVEAL);
        }
    }

    private void unpublishCard() {
        if(mLiveCard != null) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
