package com.touchawesome.dotsandboxes.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.services.MusicService;

import static android.content.ContentValues.TAG;

/**
 * Created by scelus on 16.03.17
 */

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private MusicService mService;
    private boolean mBound = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.pref_settings, rootKey);
        getPreferenceScreen().findPreference(getString(R.string.pref_key_music)).setOnPreferenceChangeListener(this);
        getPreferenceScreen().findPreference(getString(R.string.pref_key_music)).setPersistent(true);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange: " + preference.getKey() + "  " + newValue.toString());

        if (preference.getKey().equals(getString(R.string.pref_key_music))) {
            if ((boolean) newValue) {
                Intent intent = new Intent(getContext(), MusicService.class);
                intent.setAction(MusicService.ACTION_START_MUSIC);
                mService.sendCommand(intent);
            }
            else {
                Intent intent = new Intent(getContext(), MusicService.class);
                intent.setAction(MusicService.ACTION_STOP_MUSIC);
                mService.sendCommand(intent);
            }
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        doBindService();
    }

    @Override
    public void onStop() {
        doUnbindService();
        super.onStop();
    }

    void doBindService() {
        Intent intent = new Intent(getContext(), MusicService.class);

        // start playing music if the user specified so in the settings screen
        boolean playMusic = android.preference.PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(getString(R.string.pref_key_music), false);
        if (playMusic) {
            intent.setAction(MusicService.ACTION_START_MUSIC);
        }

        getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;
    }

    void doUnbindService() {
        if (mBound) {
            getContext().unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     * Class for interacting with the game interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((MusicService.MusicBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
}
