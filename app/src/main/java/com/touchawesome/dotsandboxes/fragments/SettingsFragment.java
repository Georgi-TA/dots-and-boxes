package com.touchawesome.dotsandboxes.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.services.MusicIntentService;

import static android.content.ContentValues.TAG;

/**
 * Created by scelus on 16.03.17
 */

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private MusicIntentService mService;
    private boolean mBound = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.pref_settings, rootKey);

        getPreferenceScreen().findPreference(getString(R.string.pref_key_music))
                             .setOnPreferenceChangeListener(this);

        getPreferenceScreen().findPreference(getString(R.string.pref_key_music)).setPersistent(true);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange: " + preference.getKey() + "  " + newValue.toString());

        if (preference.getKey().equals(getString(R.string.pref_key_music))) {
            if ((boolean) newValue) {
                mService.send(new Intent(MusicIntentService.ACTION_PLAY_MUSIC));
            }
            else {
                mService.send(new Intent(MusicIntentService.ACTION_PAUSE_MUSIC));
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
        super.onStop();
        doUnbindService();
    }

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        getContext().bindService(new Intent(getContext(), MusicIntentService.class), mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;
    }

    void doUnbindService() {
        if (mBound) {
            // Detach our existing connection.
            getContext().unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when taw service object.
            mService = ((MusicIntentService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
        }
    };

}
