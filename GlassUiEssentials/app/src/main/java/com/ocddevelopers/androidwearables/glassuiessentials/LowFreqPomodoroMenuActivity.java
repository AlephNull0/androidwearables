package com.ocddevelopers.androidwearables.glassuiessentials;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class LowFreqPomodoroMenuActivity extends Activity {
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pomodoro_lowfreq, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.stop:
                stopService(new Intent(this, PomodoroServiceLowFreq.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        finish();
    }
}
