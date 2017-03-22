package com.touchawesome.dotsandboxes.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.squareup.picasso.Picasso;
import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.fragments.ChooseLayoutFragment;
import com.touchawesome.dotsandboxes.fragments.GameLocalFragment;
import com.touchawesome.dotsandboxes.fragments.NetworkMenuFragment;
import com.touchawesome.dotsandboxes.game.controllers.Game;

/**
 * Created by scelus on 21.03.17
 */

public class NetworkPlayActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
                                                                      GoogleApiClient.OnConnectionFailedListener,
                                                                      NetworkMenuFragment.Listener,
                                                                      ChooseLayoutFragment.OnFragmentInteractionListener,
                                                                      GameLocalFragment.OnFragmentInteractionListener{

    // Client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // request codes we use when invoking an external activity
    private static final int RC_RESOLVE = 5000;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    // tag for debug logging
    final boolean ENABLE_DEBUG = true;
    final String TAG = "NetworkPlayActivity";

    // playing on hard mode?
    boolean mHardMode = false;

    // achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    private NetworkMenuFragment mMainMenuFragment;
    private ChooseLayoutFragment mChooseLayoutFragment;
    private GameLocalFragment mGameFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_play);

        // Load the toolbar as a support appbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup action bar appearance
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the Google API Client with access to Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        // create fragments
        mMainMenuFragment = new NetworkMenuFragment();

        // listen to fragment events
        mMainMenuFragment.setListener(this);


        mChooseLayoutFragment = new ChooseLayoutFragment();
        mChooseLayoutFragment.setListener(this);

        // add initial fragment (welcome fragment)
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mMainMenuFragment).commit();

        // IMPORTANT: if this Activity supported rotation, we'd have to be
        // more careful about adding the fragment, since the fragment would
        // already be there after rotation and trying to add it again would
        // result in overlapping fragments. But since we don't support rotation,
        // we don't deal with that for code simplicity.

        // load outbox from file
        mOutbox.loadLocal(this);
    }

    // Switch UI to the given fragment
    void switchToFragment(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag)
                .commit();
    }

    private boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.online, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_achievements: {
                onShowAchievementsRequested();
                return true;
            }
            case R.id.action_leaderboard: {
                onShowLeaderboardsRequested();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart(): connecting");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(): disconnecting");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStartGameRequested() {
        startGame();
    }

    @Override
    public void onShowAchievementsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                    RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.achievements_not_available)).show();
        }
    }

    @Override
    public void onShowLeaderboardsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient),
                    RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.leaderboards_not_available)).show();
        }
    }

    /**
     * Start gameplay. This means updating some status variables and switching
     * to the "gameplay" screen (the screen where the user types the score they want).
     */
    void startGame() {
        switchToFragment(mChooseLayoutFragment);
    }

    //TODO: should be added to the gameplay fragment
//    @Override
//    public void onEnteredScore(int requestedScore) {
//        // Compute final score (in easy mode, it's the requested score; in hard mode, it's half)
//        int finalScore = mHardMode ? requestedScore / 2 : requestedScore;
//
//        mWinFragment.setFinalScore(finalScore);
//        mWinFragment.setExplanation(mHardMode ? getString(R.string.hard_mode_explanation) :
//                getString(R.string.easy_mode_explanation));
//
//        // check for achievements
//        checkForAchievements(requestedScore, finalScore);
//
//        // update leaderboards
//        updateLeaderboards(finalScore);
//
//        // push those accomplishments to the cloud, if signed in
//        pushAccomplishments();
//
//        // switch to the exciting "you won" screen
//        switchToFragment(mWinFragment);
//    }


    /**
     * Check for achievements and unlock the appropriate ones.
     *
     * @param requestedScore the score the user requested.
     * @param finalScore the score the user got.
     */
    void checkForAchievements(int requestedScore, int finalScore) {
//        // Check if each condition is met; if so, unlock the corresponding
//        // achievement.
//        if (isPrime(finalScore)) {
//            mOutbox.mPrimeAchievement = true;
//            achievementToast(getString(R.string.achievement_prime_toast_text));
//        }
//        if (requestedScore == 9999) {
//            mOutbox.mArrogantAchievement = true;
//            achievementToast(getString(R.string.achievement_arrogant_toast_text));
//        }
//        if (requestedScore == 0) {
//            mOutbox.mHumbleAchievement = true;
//            achievementToast(getString(R.string.achievement_humble_toast_text));
//        }
//        if (finalScore == 1337) {
//            mOutbox.mLeetAchievement = true;
//            achievementToast(getString(R.string.achievement_leet_toast_text));
//        }
        mOutbox.mBoredSteps++;
    }

    void unlockAchievement(int achievementId, String fallbackString) {
        if (isSignedIn()) {
            Games.Achievements.unlock(mGoogleApiClient, getString(achievementId));
        } else {
            Toast.makeText(this, getString(R.string.achievement) + ": " + fallbackString,
                    Toast.LENGTH_LONG).show();
        }
    }

    void achievementToast(String achievement) {
        // Only show toast if not signed in. If signed in, the standard Google Play
        // toasts will appear, so we don't need to show our own.
        if (!isSignedIn()) {
            Toast.makeText(this, getString(R.string.achievement) + ": " + achievement,
                    Toast.LENGTH_LONG).show();
        }
    }

    void pushAccomplishments() {
//        if (!isSignedIn()) {
//            // can't push to the cloud, so save locally
//            mOutbox.saveLocal(this);
//            return;
//        }
//        if (mOutbox.mPrimeAchievement) {
//            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_prime));
//            mOutbox.mPrimeAchievement = false;
//        }
//        if (mOutbox.mArrogantAchievement) {
//            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_arrogant));
//            mOutbox.mArrogantAchievement = false;
//        }
//        if (mOutbox.mHumbleAchievement) {
//            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_humble));
//            mOutbox.mHumbleAchievement = false;
//        }
//        if (mOutbox.mLeetAchievement) {
//            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_leet));
//            mOutbox.mLeetAchievement = false;
//        }
//        if (mOutbox.mBoredSteps > 0) {
//            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_really_bored),
//                    mOutbox.mBoredSteps);
//            Games.Achievements.increment(mGoogleApiClient, getString(R.string.achievement_bored),
//                    mOutbox.mBoredSteps);
//        }
//        if (mOutbox.mEasyModeScore >= 0) {
//            Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_easy),
//                    mOutbox.mEasyModeScore);
//            mOutbox.mEasyModeScore = -1;
//        }
//        if (mOutbox.mHardModeScore >= 0) {
//            Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_hard),
//                    mOutbox.mHardModeScore);
//            mOutbox.mHardModeScore = -1;
//        }
        mOutbox.saveLocal(this);
    }

    /**
     * Update leaderboards with the user's score.
     *
     * @param finalScore The score the user got.
     */
    void updateLeaderboards(int finalScore) {
        if (mHardMode && mOutbox.mHardModeScore < finalScore) {
            mOutbox.mHardModeScore = finalScore;
        } else if (!mHardMode && mOutbox.mEasyModeScore < finalScore) {
            mOutbox.mEasyModeScore = finalScore;
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

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        // Show sign-out button on main menu
        mMainMenuFragment.setShowSignInButton(false);
//
//        // Show "you are signed in" message on win screen, with no sign in button.
//        mWinFragment.setShowSignInButton(false);

        // Set the greeting appropriately on main menu
        Player p = Games.Players.getCurrentPlayer(mGoogleApiClient);
        mMainMenuFragment.setPlayer(p);

        // if we have accomplishments to push, push them
        if (!mOutbox.isEmpty()) {
            pushAccomplishments();
            Toast.makeText(this, getString(R.string.your_progress_will_be_uploaded),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;
            if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

        // Sign-in failed, so show sign-in button on main menu
        mMainMenuFragment.setShowSignInButton(true);
        // mWinFragment.setShowSignInButton(true);
    }

    @Override
    public void onSignInButtonClicked() {
//        // Check to see the developer who's running this sample code read the instructions :-)
//        // NOTE: this check is here only because this is a sample! Don't include this
//        // check in your actual production app.
//        if(!BaseGameUtils.verifySampleSetup(this, R.string.app_id,
//                R.string.achievement_prime, R.string.leaderboard_easy)) {
//            Log.w(TAG, "*** Warning: setup problems detected. Sign in may not work!");
//        }

        // start the sign-in flow
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onSignOutButtonClicked() {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        mMainMenuFragment.setShowSignInButton(true);
        // mWinFragment.setShowSignInButton(true);
    }

    @Override
    public void onLayoutChosen(Bundle args, int rows, int columns) {
        Bundle arguments = new Bundle();
        arguments.putInt("rows", rows);
        arguments.putInt("columns", columns);
        arguments.putSerializable(GameLocalFragment.ARG_MODE, Game.Mode.PLAYER);

        mGameFragment = GameLocalFragment.newInstance(arguments);
        switchToFragment(mGameFragment);
    }

    @Override
    public void onGameLocalFragmentInteraction(int fragmentId, Bundle args) {
        // TODO: 22.03.17 add  
    }

    class AccomplishmentsOutbox {

        boolean mPrimeAchievement = false;
        boolean mHumbleAchievement = false;
        boolean mLeetAchievement = false;
        boolean mArrogantAchievement = false;
        int mBoredSteps = 0;
        int mEasyModeScore = -1;
        int mHardModeScore = -1;
        boolean isEmpty() {
            return !mPrimeAchievement && !mHumbleAchievement && !mLeetAchievement &&
                    !mArrogantAchievement && mBoredSteps == 0 && mEasyModeScore < 0 &&
                    mHardModeScore < 0;
        }

        public void saveLocal(Context ctx) {
            /* TODO: This is left as an exercise. To make it more difficult to cheat,
             * this data should be stored in an encrypted file! And remember not to
             * expose your encryption key (obfuscate it by building it from bits and
             * pieces and/or XORing with another string, for instance). */
        }

        public void loadLocal(Context ctx) {
            /* TODO: This is left as an exercise. Write code here that loads data
             * from the file you wrote in saveLocal(). */
        }

    }
//    @Override
//    public void onWinScreenSignInClicked() {
//        mSignInClicked = true;
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    public void onWinScreenDismissed() {
//        switchToFragment(mMainMenuFragment);
//    }

}
