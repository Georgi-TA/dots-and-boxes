package com.touchawesome.dotsandboxes;

import android.app.Application;
import android.graphics.Typeface;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.touchawesome.dotsandboxes.utils.Globals;

import java.util.HashMap;

/**
 * Created by SceLus on 15/10/2014
 */
public class App extends Application {

    private static final String PROPERTY_ID = "UA-97658506-1";

    @Override
    public void onCreate() {
        super.onCreate();
        Globals.kgTrueColors = Typeface.createFromAsset(getAssets(), "fonts/KGTrueColors.ttf");
    }

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(PROPERTY_ID);
            mTrackers.put(trackerId, t);

        }

        return mTrackers.get(trackerId);
    }
}
