package com.touchawesome.dotsandboxes.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.blackbear.scelus.dotsandboxes.R;

/**
 * Created by scelus on 16.03.17
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_settings);
    }
}
