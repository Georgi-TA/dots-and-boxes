package com.touchawesome.dotsandboxes.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.touchawesome.dotsandboxes.R;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class MusicIntentService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_SET_VALUE = 2;
    public static final int MSG_UNREGISTER_CLIENT = 3;

    private MediaPlayer mPlayer;

    public static final String ACTION_START_MUSIC = "com.touchawesome.dotsandboxes.services.action.START";
    public static final String ACTION_PLAY_MUSIC = "com.touchawesome.dotsandboxes.services.action.PLAY";
    public static final String ACTION_PAUSE_MUSIC = "com.touchawesome.dotsandboxes.services.action.PAUSE";
    public static final String ACTION_SET_VOLUME = "com.touchawesome.dotsandboxes.services.action.VOLUME";
    public static final String ACTION_STOP_MUSIC = "com.touchawesome.dotsandboxes.services.action.STOP";

    public MusicIntentService() {

    }

    public MusicIntentService(IBinder service) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mPlayer == null)
            createMediaPlayer();

        return mBinder;
    }

    private void createMediaPlayer() {
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.hey_ho);
        if (mPlayer != null) {
            mPlayer.setLooping(true);
            mPlayer.setVolume(100, 100);
            mPlayer.setOnPreparedListener(this);
        }
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Public method for bound clients to access
     * @param intent the object which contains the action information to be performed
     */
    public void send(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_MUSIC.equals(action)) {
                handleActionStart();
            }
            else if (ACTION_PLAY_MUSIC.equals(action)) {
                handleActionPlay();
            }
            else if (ACTION_PAUSE_MUSIC.equals(action)) {
                handleActionPause();
            }
            else if (ACTION_SET_VOLUME.equals(action)) {
                handleActionSetVolume(intent.getFloatExtra("volume", 0f));
            }
            else if (ACTION_STOP_MUSIC.equals(action)) {
                handleActionStop();
            }
        }
    }

    private void handleActionPlay() {
        if (!mPlayer.isPlaying())
            mPlayer.start();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayer.start();
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public MusicIntentService getService() {
            return MusicIntentService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
        return false;
    }

    private void handleActionStart() {
       if(mPlayer != null && !mPlayer.isPlaying()) {
            try {
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleActionSetVolume(float volume) {
        mPlayer.setVolume(volume, volume);
    }

    private void handleActionStop() {
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
    }

    private void handleActionPause() {
        if (mPlayer != null && mPlayer.isPlaying())
            mPlayer.pause();
    }
}
