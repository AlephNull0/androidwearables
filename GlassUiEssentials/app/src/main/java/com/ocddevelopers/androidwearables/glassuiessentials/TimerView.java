package com.ocddevelopers.androidwearables.glassuiessentials;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Locale;

public class TimerView extends FrameLayout {
    private TextView mMinutes, mSeconds;
    private String mPrefix;

    public TimerView(Context context) {
        super(context);
        init(context);
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.pomodoro_timer, this);
        mMinutes = (TextView) findViewById(R.id.minutes);
        mSeconds = (TextView) findViewById(R.id.seconds);
        mPrefix = "";
    }

    public void setTimeRemaining(int minutes, int seconds) {
        mMinutes.setText(String.format(Locale.US, "%s%02d", mPrefix, minutes));
        mSeconds.setText(String.format(Locale.US, "%02d", seconds));
    }

    public void setTimerComplete(boolean complete) {
        if(complete) {
            mMinutes.setTextColor(Color.parseColor("#ffcc3333"));
            mSeconds.setTextColor(Color.parseColor("#ffcc3333"));
            mPrefix = "-";
        } else {
            mMinutes.setTextColor(Color.WHITE);
            mSeconds.setTextColor(Color.WHITE);
            mPrefix = "";
        }
    }
}
