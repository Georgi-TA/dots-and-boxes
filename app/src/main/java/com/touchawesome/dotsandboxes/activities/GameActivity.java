package com.touchawesome.dotsandboxes.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.fragments.GameFragment;
import com.touchawesome.dotsandboxes.fragments.ResultsFragment;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.services.MusicService;
import com.touchawesome.dotsandboxes.utils.Constants;

public class GameActivity extends GoogleGamesActivity implements GameFragment.OnFragmentInteractionListener,
                                                                    ResultsFragment.OnFragmentInteractionListener,
                                                                    FragmentManager.OnBackStackChangedListener {

    private GameFragment gameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (savedInstanceState != null) {
            gameFragment = (GameFragment) getSupportFragmentManager().getFragment(savedInstanceState, GameFragment.class.getName());
        }
        else {
            Bundle args = getIntent().getBundleExtra(Constants.INTENT_GAME_EXTRA_BUNDLE);
            gameFragment = new GameFragment();
            gameFragment.setArguments(args);
        }

        loadGameFragment();
    }



    private void loadGameFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, gameFragment);
        transaction.commit();

        // log the time of game start
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME_ACHIEVEMENTS, MODE_PRIVATE);
        prefs.edit().putLong(Constants.PREFS_ACHIEVEMENT_START_TIME, System.currentTimeMillis()).apply();
    }

    @Override
    public void onWinFragmentLoad(int fragmentId, Bundle args) {
        if (fragmentId == ResultsFragment.FRAGMENT_ID) {
            int scorePlayer1 = args.getInt(GameFragment.ARG_PLAYER1_SCORE);
            int scorePlayer2 = args.getInt(GameFragment.ARG_PLAYER2_SCORE);
            Game.Mode mode = (Game.Mode) args.getSerializable(GameFragment.ARG_MODE);

            // check for achievements
            if (isSignedIn()) {
                long startTimeInMillis = getSharedPreferences(Constants.PREFS_NAME_ACHIEVEMENTS, MODE_PRIVATE)
                        .getLong(Constants.PREFS_ACHIEVEMENT_START_TIME, System.currentTimeMillis());
                long time = System.currentTimeMillis() - startTimeInMillis;
                checkForAchievements(scorePlayer1, scorePlayer2, time, mode);
            }

            ResultsFragment dialog = ResultsFragment.newInstance(args);
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "dialog_fragment");
            dialog.setCancelable(false);
        }
    }

    @Override
    public void onSoundRequested() {
        mService.sendCommand(new Intent(MusicService.ACTION_PLAY_SOUND));
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();

        if (backStackCount < 1)
            finish();
    }

    @Override
    public void onReplayRequested(Bundle arguments) {
        gameFragment = new GameFragment();
        gameFragment.setArguments(arguments);

        loadGameFragment();
    }

    @Override
    public void onAchievementsRequested() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), RC_UNUSED);
        } else {
            BaseGameUtils.makeSimpleDialog(this, getString(R.string.achievements_not_available)).show();
        }
    }

    @Override
    public void onMenuRequested() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, GameFragment.class.getName(), gameFragment);
    }
}
