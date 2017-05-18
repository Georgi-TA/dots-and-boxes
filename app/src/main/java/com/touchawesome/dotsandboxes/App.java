package com.touchawesome.dotsandboxes;

import android.app.Application;
import android.graphics.Typeface;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.touchawesome.dotsandboxes.db.DaoMaster;
import com.touchawesome.dotsandboxes.db.DaoSession;
import com.touchawesome.dotsandboxes.utils.Globals;

import org.greenrobot.greendao.database.Database;

import java.util.HashMap;

/**
 * Created by SceLus on 15/10/2014
 */
public class App extends Application {

    private static final String PROPERTY_ID = "UA-97658506-1";
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        Globals.kgTrueColors = Typeface.createFromAsset(getAssets(), "fonts/KGTrueColors.ttf");

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public enum TrackerName {
        APP_TRACKER
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

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
