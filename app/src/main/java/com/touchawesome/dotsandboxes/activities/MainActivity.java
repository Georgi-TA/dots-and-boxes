package com.touchawesome.dotsandboxes.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.fragments.ChooseLayoutFragment;
import com.touchawesome.dotsandboxes.fragments.ComingSoonFragment;
import com.touchawesome.dotsandboxes.fragments.GameLocalFragment;
import com.touchawesome.dotsandboxes.fragments.MainMenuFragment;
import com.touchawesome.dotsandboxes.fragments.NetworkMenuFragment;
import com.touchawesome.dotsandboxes.fragments.WinnerFragment;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.services.MusicIntentService;
import com.touchawesome.dotsandboxes.utils.Constants;

public class MainActivity extends AppCompatActivity
                          implements MainMenuFragment.OnFragmentInteractionListener,
                                     FragmentManager.OnBackStackChangedListener,
                                     GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
                                     NetworkMenuFragment.Listener,
                                     ChooseLayoutFragment.OnFragmentInteractionListener {

    private static final String ARG_GAME_IN_PROGRESS = "info.scelus.args.gameinprogress";
    private static final String TAG = MainActivity.class.getName();
    private MusicIntentService mService;
    private boolean mBoundMusicService = false;

    // Client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // request codes we use when invoking an external activity
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add the toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Create the Google API Client with access to Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();

        if (savedInstanceState != null) {
            // clear the backstack if the game is not in progress
            if (savedInstanceState.containsKey(ARG_GAME_IN_PROGRESS) && !savedInstanceState.getBoolean(ARG_GAME_IN_PROGRESS)) {
                while (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStackImmediate();
            }
        }
        else {
            loadFragment(MainMenuFragment.FRAGMENT_ID, null);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        findViewById(R.id.button_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), SettingsActivity.class));
            }
        });

        findViewById(R.id.button_achievements).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowAchievementsRequested();
            }
        });

        findViewById(R.id.button_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), InfoActivity.class));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();

        if (backStackCount >= 1)
            fragmentManager.popBackStack();

        return super.onSupportNavigateUp();

    }


    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean playMusic = prefs.getBoolean(getString(R.string.pref_key_music), false);

        if (mBoundMusicService && playMusic)
            mService.send(new Intent(MusicIntentService.ACTION_START_MUSIC));
    }

    @Override
    public void onPause() {
        super.onPause();
        mService.send(new Intent(MusicIntentService.ACTION_PAUSE_MUSIC));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, MusicIntentService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBoundMusicService) {
            unbindService(mConnection);
            mBoundMusicService = false;
        }

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundMusicService = true;

            // This is called when taw service object.
            mService = new MusicIntentService(service);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean playMusic = prefs.getBoolean(getString(R.string.pref_key_music), false);

            if (mBoundMusicService && playMusic)
                mService.send(new Intent(MusicIntentService.ACTION_START_MUSIC));
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundMusicService = false;
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
        }
    };

    private void loadFragment(int fragmentId, Bundle args) {
        Fragment fragment;
        switch (fragmentId) {
            case MainMenuFragment.FRAGMENT_ID: {
                fragment = MainMenuFragment.newInstance(args);
                break;
            }
            case GameLocalFragment.FRAGMENT_ID: {
                fragment = GameLocalFragment.newInstance(args);
                break;
            }
            case WinnerFragment.FRAGMENT_ID: {
                fragment = WinnerFragment.newInstance(args);

                break;
            }
            case ChooseLayoutFragment.FRAGMENT_ID: {
                fragment = ChooseLayoutFragment.newInstance(args);
                break;
            }
            case NetworkMenuFragment.FRAGMENT_ID: {
                fragment = NetworkMenuFragment.newInstance(args);
                break;
            }
            case ComingSoonFragment.FRAGMENT_ID: {
                fragment = ComingSoonFragment.newInstance(args);
                break;
            }
            default: {
                fragment = ComingSoonFragment.newInstance(args);
                break;
            }
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                                        R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(fragment.getClass().toString());
        transaction.commit();
    }

    private void startNetworkPlay() {
        loadFragment(NetworkMenuFragment.FRAGMENT_ID, new Bundle());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // check if game is in progress
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName().equals(GameLocalFragment.class.getName()))
            outState.putBoolean(ARG_GAME_IN_PROGRESS, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();

        if (backStackCount > 1) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (backStackCount == 1) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        else if (backStackCount < 1) {
            finish();
        }
    }

    @Override
    public void onComputerPlaySelected() {
        Bundle args = new Bundle();
        args.putSerializable(GameLocalFragment.ARG_MODE, Game.Mode.CPU);
        loadFragment(ChooseLayoutFragment.FRAGMENT_ID, args);
    }

    @Override
    public void onFriendPlaySelected() {
        Bundle args = new Bundle();
        args.putSerializable(GameLocalFragment.ARG_MODE, Game.Mode.PLAYER);
        loadFragment(ChooseLayoutFragment.FRAGMENT_ID, args);
    }

    @Override
    public void onNetworkPlaySelected() {
        startNetworkPlay();
    }

    @Override
    public void onLayoutChosen(Bundle args, int rows, int columns) {
        args.putInt("rows", rows);
        args.putInt("columns", columns);
        args.putSerializable(GameLocalFragment.ARG_MODE, Game.Mode.PLAYER);

        Intent intent = new Intent (this, GameActivity.class);
        intent.putExtra(Constants.INTENT_GAME_EXTRA_BUNDLE, args);
        startActivity(intent);
    }



    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected(): connected to Google APIs");

        // Set the greeting appropriately on main menu
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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;
            if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }
    }

    @Override
    public void onSignInButtonClicked() {
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onSignOutButtonClicked() {

    }

    private boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    @Override
    public void onStartGameRequested() {

    }

    public void onShowAchievementsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.achievements_not_available)).show();
        }
    }

    public void onShowLeaderboardsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.leaderboards_not_available)).show();
        }
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
            Toast.makeText(this, getString(R.string.achievement) + ": " + achievement, Toast.LENGTH_LONG).show();
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
}
