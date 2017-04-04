package com.touchawesome.dotsandboxes.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.touchawesome.dotsandboxes.fragments.ChooseLayoutFragment;
import com.touchawesome.dotsandboxes.fragments.ComingSoonFragment;
import com.touchawesome.dotsandboxes.fragments.GameLocalFragment;
import com.touchawesome.dotsandboxes.fragments.MainMenuFragment;
import com.touchawesome.dotsandboxes.fragments.NetworkMenuFragment;
import com.touchawesome.dotsandboxes.fragments.WinnerFragment;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.services.MusicIntentService;
import com.touchawesome.dotsandboxes.utils.Constants;

import java.util.concurrent.TimeUnit;

public class MainActivity extends GoogleGamesActivity implements MainMenuFragment.OnFragmentInteractionListener,
                                                                 FragmentManager.OnBackStackChangedListener,
                                                                 ChooseLayoutFragment.OnFragmentInteractionListener,
                                                                 NetworkMenuFragment.Listener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private String TAG = MainActivity.class.getName();

    private static final String ARG_GAME_IN_PROGRESS = "com.touchawesome.args.gameinprogress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add the toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        if (savedInstanceState != null) {
            // clear the backstack if the game is not in progress
            if (savedInstanceState.containsKey(ARG_GAME_IN_PROGRESS) && !savedInstanceState.getBoolean(ARG_GAME_IN_PROGRESS)) {
                while (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStackImmediate();
            }
        } else {
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
               showAchievementsPage();
            }
        });

        findViewById(R.id.button_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), InfoActivity.class));
            }
        });

        if (!achievementsChecked)
            new CheckForUnlockedAchievementsTask().execute();
    }

    private void showAchievementsPage() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            onShowAchievementsRequested();
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

        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();
    }

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

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (backStackCount > 1) {
                supportActionBar.setDisplayHomeAsUpEnabled(true);
            }
            if (backStackCount == 1) {
                supportActionBar.setDisplayHomeAsUpEnabled(false);
            } else if (backStackCount < 1) {
                finish();
            }
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
        // args.putSerializable(GameLocalFragment.ARG_MODE, Game.Mode.PLAYER);

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(Constants.INTENT_GAME_EXTRA_BUNDLE, args);
        startActivity(intent);
    }

    @Override
    public void onStartGameRequested() {

    }

    @Override
    public void onSignInButtonClicked() {
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onSignOutButtonClicked() {

    }


}
