package com.touchawesome.dotsandboxes.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.utils.Constants;

import java.util.concurrent.TimeUnit;

/**
 * Created by scelus on 31.03.17
 */

public class GoogleGamesActivity extends MusicEnabledActivity implements GoogleApiClient.ConnectionCallbacks,
                                                                         GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GoogleGamesActivity.class.getName();

    // Client used to interact with Google APIs
    public GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    public boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // request codes we use when invoking an external activity
    protected static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    // achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();
    boolean achievementsChecked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the Google API Client with access to Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected(): connected to Google APIs");

//      Set the greeting appropriately on main menu
        Player p = Games.Players.getCurrentPlayer(mGoogleApiClient);
//        mMainMenuFragment.setPlayer(p);
//        if we have accomplishments to push, push them
//        if (!mOutbox.isEmpty()) {
//            pushAccomplishments();
//            Toast.makeText(this, getString(R.string.your_progress_will_be_uploaded),
//                    Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOutbox.saveLocal();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            Log.e(TAG, "Error number " + connectionResult.getErrorMessage() + "  code: " +  connectionResult.getErrorCode());
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
        }

        mGoogleApiClient.connect();
    }

    public boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    public void onShowAchievementsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.achievements_not_available)).show();
        }
    }

    void unlockAchievement(int achievementId, String fallbackString) {
        if (isSignedIn()) {
            Games.Achievements.unlock(mGoogleApiClient, getString(achievementId));
        } else {
            Toast.makeText(this, getString(R.string.achievement) + ": " + fallbackString, Toast.LENGTH_LONG).show();
        }
    }

    void checkForAchievements(int player1Score, int player2Score, long time, Game.Mode mode) {
        // Check if each condition is met; if so, unlock the corresponding
        // achievement.
        if (!mOutbox.mStartedUpAchievement) {
            unlockAchievement(R.string.achievement_started_up_id, getString(R.string.achievement_started_up_unlocked));
        }

        if (player1Score > player2Score) {
            mOutbox.wins++;
            mOutbox.consecutiveWins++;

            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_winner_winner_id), 1);

            if (mOutbox.wins >= 10 && !mOutbox.mWinnerWinnerAchievement)
                Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_winner_winner_id), 1);

            if (mOutbox.consecutiveWins >= 10 && !mOutbox.mFiredUpAchievement)
                unlockAchievement(R.string.achievement_fired_up_id, getString(R.string.achievement_fired_up_unlocked));

            Log.d(TAG, "checkForAchievements: " + mOutbox.wins);
        }
        else {
            mOutbox.consecutiveWins = 0;
        }

        if (player2Score == 0 && !mOutbox.mFlawlessAchievement) {
            unlockAchievement(R.string.achievement_flawless_id, getString(R.string.achievement_flawless_unlocked));
        }

        if (time < 30000 && !mOutbox.mInNoTimeAchievement) {
            unlockAchievement(R.string.achievement_in_no_time_id, getString(R.string.achievement_in_no_time_unlocked));
        }

        Log.d(TAG, "checkForAchievements: consecutive wins " + mOutbox.consecutiveWins);
        Log.d(TAG, "checkForAchievements: wins " + mOutbox.wins);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "checkForAchievements: logging in");
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_other_error);
            }
        }
    }

    private class AccomplishmentsOutbox {
        boolean mStartedUpAchievement = false;
        boolean mWinnerWinnerAchievement = false;
        boolean mFiredUpAchievement = false;
        boolean mInNoTimeAchievement = false;
        boolean mFlawlessAchievement = false;

        int wins = 0;
        int consecutiveWins = 0;

        void saveLocal() {
            SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME_ACHIEVEMENTS, Context.MODE_PRIVATE);
            prefs.edit().putBoolean(Constants.PREFS_ACHIEVEMENT_STARTED_UP, mStartedUpAchievement)
                        .putBoolean(Constants.PREFS_ACHIEVEMENT_WINNER_WINNER, mWinnerWinnerAchievement)
                        .putBoolean(Constants.PREFS_ACHIEVEMENT_FIRED_UP, mFiredUpAchievement)
                        .putBoolean(Constants.PREFS_ACHIEVEMENT_IN_NO_TIME, mInNoTimeAchievement)
                        .putBoolean(Constants.PREFS_ACHIEVEMENT_FLAWLESS, mFlawlessAchievement)
                        .putInt(Constants.PREFS_ACHIEVEMENT_WIN_COUNT, wins)
                        .putInt(Constants.PREFS_ACHIEVEMENT_CONSECUTIVE_WIN_COUNT, consecutiveWins)
                        .apply();
        }

        void loadLocal() {
            SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME_ACHIEVEMENTS, Context.MODE_PRIVATE);

            // get the achievements
            mStartedUpAchievement = prefs.getBoolean(Constants.PREFS_ACHIEVEMENT_STARTED_UP, false);
            mWinnerWinnerAchievement = prefs.getBoolean(Constants.PREFS_ACHIEVEMENT_WINNER_WINNER, false);
            mInNoTimeAchievement = prefs.getBoolean(Constants.PREFS_ACHIEVEMENT_IN_NO_TIME, false);
            mFiredUpAchievement = prefs.getBoolean(Constants.PREFS_ACHIEVEMENT_FIRED_UP, false);
            mFlawlessAchievement = prefs.getBoolean(Constants.PREFS_ACHIEVEMENT_FLAWLESS, false);

            //retrieve the win counts
            wins = prefs.getInt(Constants.PREFS_ACHIEVEMENT_WIN_COUNT, 0);
            consecutiveWins = prefs.getInt(Constants.PREFS_ACHIEVEMENT_CONSECUTIVE_WIN_COUNT, 0);
        }
    }

    class CheckForUnlockedAchievementsTask extends AsyncTask<Object, Object, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            loadUnlockedAchievements();
            achievementsChecked = true;
            return null;
        }
    }

    private void loadUnlockedAchievements() {
        boolean fullLoad = false;  // set to 'true' to reload all achievements (ignoring cache)
        long waitTime = 60;    // seconds to wait for achievements to load before timing out

        // load achievements
        PendingResult pendingResult = Games.Achievements.load( mGoogleApiClient, fullLoad );
        Achievements.LoadAchievementsResult result = (Achievements.LoadAchievementsResult) pendingResult.await(waitTime, TimeUnit.SECONDS );
        int status = result.getStatus().getStatusCode();
        if ( status != GamesStatusCodes.STATUS_OK )  {
            result.release();
            return;
        }

        // cache the loaded achievements
        AchievementBuffer buf = result.getAchievements();
        int bufSize = buf.getCount();
        for ( int i = 0; i < bufSize; i++ )  {
            Achievement ach = buf.get( i );

            // here you now have access to the achievement's data
            String id = ach.getAchievementId();  // the achievement ID string
            boolean unlocked = ach.getState() == Achievement.STATE_UNLOCKED;  // is unlocked

            Log.i(TAG, "loadUnlockedAchievements: " + id + "   " + ach.getName() + "  " + ach.getState());;

            SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME_ACHIEVEMENTS, MODE_PRIVATE);
            if (unlocked) {
                if (id.equals(getString(R.string.achievement_started_up_id))) {
                    prefs.edit().putBoolean(Constants.PREFS_ACHIEVEMENT_STARTED_UP, true).apply();
                    mOutbox.mStartedUpAchievement = true;
                }
                else if (id.equals(getString(R.string.achievement_started_up_id))) {
                    prefs.edit().putInt(Constants.PREFS_ACHIEVEMENT_WIN_COUNT, ach.getCurrentSteps()).apply();
                    if (ach.getCurrentSteps() == ach.getTotalSteps()) {
                        prefs.edit().putBoolean(Constants.PREFS_ACHIEVEMENT_WINNER_WINNER, true).apply();
                        mOutbox.mWinnerWinnerAchievement = true;
                    }
                }
                else if (id.equals(getString(R.string.achievement_fired_up_id))) {
                    prefs.edit().putBoolean(Constants.PREFS_ACHIEVEMENT_FIRED_UP, true).apply();
                    mOutbox.mFiredUpAchievement= true;
                }
                else if (id.equals(getString(R.string.achievement_in_no_time_id))) {
                    prefs.edit().putBoolean(Constants.PREFS_ACHIEVEMENT_IN_NO_TIME, true).apply();
                    mOutbox.mInNoTimeAchievement = true;
                }
                else if (id.equals(getString(R.string.achievement_flawless_id))) {
                    prefs.edit().putBoolean(Constants.PREFS_ACHIEVEMENT_FLAWLESS, true).apply();
                    mOutbox.mFlawlessAchievement = true;
                }
            }
        }

        buf.release();
        result.release();

        mOutbox.loadLocal();
    }
}