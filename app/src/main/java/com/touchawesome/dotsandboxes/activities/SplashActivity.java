package com.touchawesome.dotsandboxes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.touchawesome.dotsandboxes.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private Timer timer;
    private TimerTask loadNextActivity = new TimerTask() {
        @Override
        public void run() {
            Intent mainActivity = new Intent(getApplicationContext(), TutorialActivity.class);
            startActivity(mainActivity);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        timer = new Timer();
        timer.schedule(loadNextActivity, getResources().getInteger(R.integer.splash_duration));
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
