package com.touchawesome.dotsandboxes.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.fragments.ChooseBoardSizeFragment;
import com.touchawesome.dotsandboxes.fragments.ComingSoonFragment;
import com.touchawesome.dotsandboxes.fragments.GameFragment;
import com.touchawesome.dotsandboxes.fragments.ChooseModeFragment;
import com.touchawesome.dotsandboxes.fragments.ResultsFragment;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.services.MusicService;
import com.touchawesome.dotsandboxes.utils.Constants;

public class MainActivity extends GoogleGamesActivity implements ChooseModeFragment.OnFragmentInteractionListener,
                                                                 FragmentManager.OnBackStackChangedListener,
                                                                 ChooseBoardSizeFragment.OnFragmentInteractionListener{

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add the toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
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
                startActivity(new Intent(v.getContext(), AboutActivity.class));
            }
        });


        // music button
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        final boolean hasMusic = preferences.getBoolean(MainActivity.this.getString(R.string.pref_key_music), false);
        final ImageButton musicButton = (ImageButton) findViewById(R.id.button_music);
        if (hasMusic) {
            Intent intent = new Intent(MainActivity.this, MusicService.class);
            intent.setAction(MusicService.ACTION_START_MUSIC);
            mService.sendCommand(intent);

            musicButton.setImageResource(R.drawable.icon_sound);
        }
        else {
            Intent intent = new Intent(MainActivity.this, MusicService.class);
            intent.setAction(MusicService.ACTION_STOP_MUSIC);
            mService.sendCommand(intent);

            musicButton.setImageResource(R.drawable.icon_mute);
        }

        musicButton.setOnClickListener(new View.OnClickListener() {
            private boolean selected = hasMusic;

            @Override
            public void onClick(View v) {
                if (selected) {
                    Intent intent = new Intent(MainActivity.this, MusicService.class);
                    intent.setAction(MusicService.ACTION_START_MUSIC);
                    mService.sendCommand(intent);

                    preferences.edit().putBoolean(getString(R.string.pref_key_music), true).apply();
                    musicButton.setImageResource(R.drawable.icon_mute);
                    selected = false;
                }
                else {
                    Intent intent = new Intent(MainActivity.this, MusicService.class);
                    intent.setAction(MusicService.ACTION_STOP_MUSIC);
                    mService.sendCommand(intent);

                    preferences.edit().putBoolean(getString(R.string.pref_key_music), false).apply();
                    musicButton.setImageResource(R.drawable.icon_sound);
                    selected = true;
                }
            }
        });

        if (!achievementsChecked)
            new CheckForUnlockedAchievementsTask().execute();

        loadFragment(ChooseModeFragment.FRAGMENT_ID, new Bundle());
    }

    private void showAchievementsPage() {
        if (mGoogleApiClient != null) {
            if (isSignedIn()) {
                onShowAchievementsRequested();
            } else {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();

        // remove fragments when navigating back
        if (backStackCount >= 1) {
            fragmentManager.popBackStack();
        }

        return super.onSupportNavigateUp();
    }

    @Override
    public void onResume() {
        super.onResume();

        // return the user to the beginning
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    private void loadFragment(int fragmentId, Bundle args) {
        Fragment fragment;
        switch (fragmentId) {
            case ChooseModeFragment.FRAGMENT_ID: {
                fragment = ChooseModeFragment.newInstance(args);
                break;
            }
            case GameFragment.FRAGMENT_ID: {
                fragment = GameFragment.newInstance(args);
                break;
            }
            case ResultsFragment.FRAGMENT_ID: {
                fragment = ResultsFragment.newInstance(args);

                break;
            }
            case ChooseBoardSizeFragment.FRAGMENT_ID: {
                fragment = ChooseBoardSizeFragment.newInstance(args);
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

    /**
     * Control the behaviour of the app bar and finish the activity when there are no more fragments left
     */
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
        args.putSerializable(GameFragment.ARG_MODE, Game.Mode.CPU);
        loadFragment(ChooseBoardSizeFragment.FRAGMENT_ID, args);
    }

    @Override
    public void onFriendPlaySelected() {
        Bundle args = new Bundle();
        args.putSerializable(GameFragment.ARG_MODE, Game.Mode.PLAYER);
        loadFragment(ChooseBoardSizeFragment.FRAGMENT_ID, args);
    }

    @Override
    public void onNetworkPlaySelected() {
        loadFragment(ComingSoonFragment.FRAGMENT_ID, new Bundle());
    }

    @Override
    public void onLayoutChosen(Bundle args) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(Constants.INTENT_GAME_EXTRA_BUNDLE, args);
        startActivity(intent);
    }
}
