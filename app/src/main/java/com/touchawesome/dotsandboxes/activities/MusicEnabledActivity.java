package com.touchawesome.dotsandboxes.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.services.MusicIntentService;

/**
 * Created by scelus on 31.03.17
 */

public class MusicEnabledActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    public MusicIntentService mService;
    private boolean mBoundMusicService = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((MusicIntentService.MusicBinder) service).getService();

            // start playing music if the user specified so in the settings screen
            boolean playMusic = PreferenceManager.getDefaultSharedPreferences(MusicEnabledActivity.this).getBoolean(getString(R.string.pref_key_music), false);

            if (playMusic) {
                mService.sendCommand(new Intent(MusicIntentService.ACTION_START_MUSIC));
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mService.sendCommand(new Intent(MusicIntentService.ACTION_STOP_MUSIC));
            mService = null;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicIntentService.class);

        if (!mBoundMusicService) {
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mBoundMusicService = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBoundMusicService) {
            unbindService(mConnection);
            mBoundMusicService = false;
        }
    }
}
