package com.touchawesome.dotsandboxes.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.os.Binder;

import com.touchawesome.dotsandboxes.R;

import java.io.IOException;

public class MusicIntentService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "MusicService";
    private final IBinder mBinder = new MusicBinder();
    private MediaPlayer mMusicPlayer;
    private MediaPlayer mSoundPlayer;

    public static final String ACTION_START_MUSIC = "com.touchawesome.dotsandboxes.services.action.START_MUSIC";
    public static final String ACTION_STOP_MUSIC = "com.touchawesome.dotsandboxes.services.action.STOP_MUSIC";
    public static final String ACTION_PLAY_SOUND = "com.touchawesome.dotsandboxes.services.action.PLAY_SOUND";

    private boolean musicPlayerPrepared = false;
    private boolean soundPlayerPrepared = false;

    public MusicIntentService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (mSoundPlayer == null)
            createSoundPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // return Service.START_NOT_STICKY;
        return super.onStartCommand(intent,flags,startId);
    }

    public void sendCommand(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_START_MUSIC.equals(action)) {
                if (mMusicPlayer == null)
                    createMediaPlayer();

                Log.d(TAG, "service play");
                handleActionPlayMusic();
            }
            else if (ACTION_STOP_MUSIC.equals(action)) {
                Log.d(TAG, "service stop");
                handleActionStopMusic();
            }
            else if (ACTION_PLAY_SOUND.equals(action)) {
                Log.d(TAG, "service play");
                if (mSoundPlayer == null)
                    createSoundPlayer();

                handleActionPlaySound();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "onBind: " + intent.toString());
            if (intent.getAction() != null && intent.getAction().equals(ACTION_START_MUSIC)) {
                Log.d(TAG, "onBind action: " + intent.getAction());
                handleActionPlayMusic();
            }
        }

        return mBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mp.equals(mMusicPlayer)) {
            musicPlayerPrepared = true;
        }
        else if (mp.equals(mSoundPlayer)) {
            soundPlayerPrepared = true;
        }
    }

    private void createMediaPlayer() {
        mMusicPlayer = MediaPlayer.create(getApplicationContext(), R.raw.hey_ho);
        if (mMusicPlayer != null) {
            mMusicPlayer.setLooping(true);
            mMusicPlayer.setVolume(100, 100);
            mMusicPlayer.setOnPreparedListener(this);
        }
    }

    private void createSoundPlayer() {
        mSoundPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click);
        if (mSoundPlayer != null) {
            mSoundPlayer.setLooping(true);
            mSoundPlayer.setVolume(100, 100);
            mSoundPlayer.setOnPreparedListener(this);
        }
    }

    private void handleActionPlayMusic() {
        if (mMusicPlayer == null) {
            createMediaPlayer();
        }
        else {
            mMusicPlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMusicPlayer != null) {
            try {
                mMusicPlayer.stop();
                mMusicPlayer.reset();
                mMusicPlayer.release();
            } finally {
                mMusicPlayer = null;
            }
        }

        if (mSoundPlayer != null) {
            try {
                mSoundPlayer.stop();
                mSoundPlayer.reset();
                mSoundPlayer.release();
            } finally {
                mSoundPlayer = null;
            }
        }
    }



    public class MusicBinder extends Binder {
        public MusicIntentService getService() {
            return MusicIntentService.this;
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError: " + " what: " + what + " extra: " + extra);

        if (mMusicPlayer != null) {
            try {
                mMusicPlayer.stop();
                mMusicPlayer.reset();
                mMusicPlayer.release();
            } finally {
                mMusicPlayer = null;
            }
        }

        if (mSoundPlayer != null) {
            try {
                mSoundPlayer.stop();
                mSoundPlayer.reset();
                mSoundPlayer.release();
            } finally {
                mSoundPlayer = null;
            }
        }

        return false;
    }
    private void handleActionStopMusic() {
        if (mMusicPlayer != null) {
            try {
                mMusicPlayer.stop();
                mMusicPlayer.release();
            } finally {
                mMusicPlayer = null;
            }
        }
    }

    private void handleActionPlaySound() {
        if (mSoundPlayer != null && !mSoundPlayer.isPlaying())
            try {
                mSoundPlayer.prepare();
                mSoundPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
