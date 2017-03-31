package com.touchawesome.dotsandboxes.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.utils.Constants;

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
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    // achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

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

        mOutbox.loadLocal();
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

    void pushAccomplishments() {
        if (!isSignedIn()) {
            // can't push to the cloud, so save locally
            mOutbox.saveLocal();
            return;
        }

        if (mOutbox.mStartedUpAchievement) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_started_up_id));
            mOutbox.mStartedUpAchievement = false;
        }

        if (mOutbox.wins > 0) {
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_winner_winner_id), mOutbox.wins);
        }

        mOutbox.saveLocal();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
        }
    }

    private boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    public void onShowAchievementsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.achievements_not_available)).show();
        }
    }

//    public void onShowLeaderboardsRequested() {
//        if (isSignedIn()) {
//            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), RC_UNUSED);
//        } else {
//            BaseGameUtils.makeSimpleDialog(this, getString(R.string.leaderboards_not_available)).show();
//        }
//    }

    void unlockAchievement(int achievementId, String fallbackString) {
        if (isSignedIn()) {
            Games.Achievements.unlock(mGoogleApiClient, getString(achievementId));
        } else {
            Toast.makeText(this, getString(R.string.achievement) + ": " + fallbackString, Toast.LENGTH_LONG).show();
        }
    }

    void achievementToast(String achievement) {
        // Only show toast if not signed in. If signed in, the standard Google Play
        // toasts will appear, so we don't need to show our own.
        if (!isSignedIn()) {
            Toast.makeText(this, getString(R.string.achievement) + ": " + achievement, Toast.LENGTH_LONG).show();
        }
    }

    void checkForAchievements(int player1Score, int player2Score, Game.Mode mode) {
        // Check if each condition is met; if so, unlock the corresponding
        // achievement.
        if (mode == Game.Mode.CPU && player1Score > player2Score) {
            unlockAchievement(R.string.achievement_started_up_id, "none");
            achievementToast(getString(R.string.achievement_started_up_unlocked));
        }

        if (mode == Game.Mode.CPU && player1Score > player2Score) {
            mOutbox.wins++;
            Log.d(TAG, "checkForAchievements: " + mOutbox.wins);
            achievementToast(getString(R.string.achievement_winner_winner_unlocked));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_other_error);
            }
        }
    }

    private class AccomplishmentsOutbox {
        boolean mStartedUpAchievement = false;
        boolean mWinnerWinnerAchievement = false;
        int wins = 0;

        boolean isEmpty() {
            return !mStartedUpAchievement && !mWinnerWinnerAchievement;
        }

        void saveLocal() {
            SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME_ACHIEVEMENTS, Context.MODE_PRIVATE);
            prefs.edit().putBoolean(Constants.PREFS_ACHIEVEMENT_STARTED_UP, mStartedUpAchievement)
                        .putBoolean(Constants.PREFS_ACHIEVEMENT_WINNER_WINNER, mWinnerWinnerAchievement)
                        .putInt(Constants.PREFS_ACHIEVEMENT_WIN_COUNT, wins)
                        .apply();
        }

        void loadLocal() {
            SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME_ACHIEVEMENTS, Context.MODE_PRIVATE);
            mStartedUpAchievement = prefs.getBoolean(Constants.PREFS_ACHIEVEMENT_STARTED_UP, false);
            mWinnerWinnerAchievement = prefs.getBoolean(Constants.PREFS_ACHIEVEMENT_WINNER_WINNER, false);
            int wins = prefs.getInt(Constants.PREFS_ACHIEVEMENT_WIN_COUNT, 0);
        }
    }

}
