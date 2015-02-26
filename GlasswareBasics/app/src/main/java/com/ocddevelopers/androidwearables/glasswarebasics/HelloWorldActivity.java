package com.ocddevelopers.androidwearables.glasswarebasics;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HelloWorldActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView helloWorld = new TextView(this);
        helloWorld.setText("Hello World");
        setContentView(helloWorld);
    }

}