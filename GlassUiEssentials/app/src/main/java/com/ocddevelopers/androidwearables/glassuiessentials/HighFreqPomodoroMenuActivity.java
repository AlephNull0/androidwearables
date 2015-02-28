package com.ocddevelopers.androidwearables.glassuiessentials;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class HighFreqPomodoroMenuActivity extends Activity {
    /*
    private PomodoroServiceHighFreq.PomodoroBinder mPomodoroBinder;
    private boolean mAttachedToWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent(this, PomodoroServiceHighFreq.class);
        bindService(serviceIntent, mServiceConnection, 0);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(service instanceof PomodoroServiceHighFreq.PomodoroBinder) {
                mPomodoroBinder = (PomodoroServiceHighFreq.PomodoroBinder)service;
                openMenu();
            }

            unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    */

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //mAttachedToWindow = true;
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pomodoro_highfreq, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.stop:
                stopService(new Intent(this, PomodoroServiceHighFreq.class));
                return true;
            case R.id.reset:
                /*
                if(mPomodoroBinder != null) {
                    mPomodoroBinder.reset();
                }
                */
                Intent resetIntent = new Intent(this, PomodoroServiceHighFreq.class);
                resetIntent.setAction(PomodoroServiceHighFreq.ACTION_RESET);
                startService(resetIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        finish();
    }

    /*
    private void openMenu() {
        if(mAttachedToWindow && mPomodoroBinder != null) {
            openOptionsMenu();
        }
    }
    */
}
