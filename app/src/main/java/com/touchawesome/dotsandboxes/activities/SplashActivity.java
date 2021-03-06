package com.touchawesome.dotsandboxes.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private Timer timer;
    private TimerTask loadNextActivity = new TimerTask() {
        @Override
        public void run() {
            Intent nextActivity;
            SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME_GENERAL, Context.MODE_PRIVATE);

            if (!prefs.getBoolean(Constants.TUTORIAL_COMPLETE, false)) {
                nextActivity = new Intent(getApplicationContext(), TutorialActivity.class);
            }
            else {
                nextActivity = new Intent(getApplicationContext(), MainActivity.class);
            }

            startActivity(nextActivity);
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
