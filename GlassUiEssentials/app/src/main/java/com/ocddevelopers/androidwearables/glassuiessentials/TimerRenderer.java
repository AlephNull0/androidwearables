package com.ocddevelopers.androidwearables.glassuiessentials;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.View;

import com.google.android.glass.timeline.DirectRenderingCallback;

import java.util.concurrent.TimeUnit;

public class TimerRenderer implements DirectRenderingCallback {
    public static final int UPDATE_RATE_MS = 1000;
    private Handler mHandler;
    private SurfaceHolder mHolder;
    private TimerView mTimerView;
    private long mTimeRemaining, mStartTime;
    private boolean mPaused;
    private SoundPool mSoundPool;
    private int mTimerFinishedSoundId;

    public TimerRenderer(Context context) {
        mHandler = new Handler();
        mTimerView = new TimerView(context);
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mTimerFinishedSoundId = mSoundPool.load(context, R.raw.timer_finished, 1000);
    }

    @Override
    public void renderingPaused(SurfaceHolder surfaceHolder, boolean paused) {
        mPaused = paused;
    }

    public void start() {
        mTimeRemaining = 8000; //TimeUnit.MINUTES.toMillis(25);
        mStartTime = SystemClock.elapsedRealtime();
        mHandler.removeCallbacks(mTimerRunnable);
        mHandler.postDelayed(mTimerRunnable, UPDATE_RATE_MS);
    }

    public void stop() {
        mHandler.removeCallbacks(mTimerRunnable);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        mTimerView.measure(measuredWidth, measuredHeight);
        mTimerView.layout(0, 0, mTimerView.getMeasuredWidth(), mTimerView.getMeasuredHeight());
        draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
    }

    private Runnable mTimerRunnable = new Runnable() {

        @Override
        public void run() {
            draw();
            mHandler.postDelayed(mTimerRunnable, UPDATE_RATE_MS);
        }
    };

    private void draw() {
        if(!mPaused && mHolder != null) {
            long timeRemainingMillis = mTimeRemaining - SystemClock.elapsedRealtime() + mStartTime;
            int secondsRemaining = (int)(timeRemainingMillis + 999) / 1000; // round up

            if(secondsRemaining <= 0) {
                mTimerView.setTimerComplete(true);
                playSound();
                secondsRemaining = Math.abs(secondsRemaining);
            }

            Canvas canvas;
            canvas = mHolder.lockCanvas();

            if(canvas != null) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mTimerView.setTimeRemaining(secondsRemaining/ 60, secondsRemaining% 60);
                mTimerView.draw(canvas);
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void reset() {
        mTimerView.setTimerComplete(false);
        mTimeRemaining = TimeUnit.MINUTES.toMillis(25);
        mStartTime = SystemClock.elapsedRealtime();

        draw();
        mHandler.removeCallbacks(mTimerRunnable);
        mHandler.postDelayed(mTimerRunnable, UPDATE_RATE_MS);
    }

    protected void playSound() {
        mSoundPool.play(mTimerFinishedSoundId,
                1 /* leftVolume */,
                1 /* rightVolume */,
                1000,
                0 /* loop */,
                1 /* rate */);
    }
}
