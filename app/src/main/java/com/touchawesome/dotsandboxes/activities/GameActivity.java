package com.touchawesome.dotsandboxes.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.fragments.GameLocalFragment;
import com.touchawesome.dotsandboxes.fragments.WinnerFragment;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.services.MusicIntentService;
import com.touchawesome.dotsandboxes.utils.Constants;

public class GameActivity extends GoogleGamesActivity implements GameLocalFragment.OnFragmentInteractionListener,
                                                                    WinnerFragment.OnFragmentInteractionListener,
                                                                    FragmentManager.OnBackStackChangedListener {

    GameLocalFragment gameFragment;

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
            gameFragment = (GameLocalFragment) getSupportFragmentManager().getFragment(savedInstanceState, GameLocalFragment.class.getName());
        }
        else {
            Bundle args = getIntent().getBundleExtra(Constants.INTENT_GAME_EXTRA_BUNDLE);
            gameFragment = new GameLocalFragment();
            gameFragment.setArguments(args);
        }

        loadGameFragment();
    }

    private void loadGameFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
//                                        R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.content, gameFragment);
        transaction.commit();
    }

    @Override
    public void onGameLocalFragmentInteraction(int fragmentId, Bundle args) {
        if (fragmentId == WinnerFragment.FRAGMENT_ID) {
            int scorePlayer1 = args.getInt(GameLocalFragment.ARG_PLAYER1_SCORE);
            int scorePlayer2 = args.getInt(GameLocalFragment.ARG_PLAYER2_SCORE);
            Game.Mode mode = (Game.Mode) args.getSerializable(GameLocalFragment.ARG_MODE);

            // unlock first achievement
            unlockAchievement(R.string.achievement_started_up_id, getString(R.string.achievement_started_up_unlocked));

            // check for achievements
            checkForAchievements(scorePlayer1, scorePlayer2, mode);

            // push those accomplishments to the cloud, if signed in
            pushAccomplishments();

            WinnerFragment dialog = WinnerFragment.newInstance(args);
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "dialog_fragment");
            dialog.setCancelable(false);
        }
    }

    @Override
    public void onSoundRequested() {
        mService.sendCommand(new Intent(MusicIntentService.ACTION_PLAY_SOUND));
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
        gameFragment = new GameLocalFragment();
        gameFragment.setArguments(arguments);

        loadGameFragment();
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
        getSupportFragmentManager().putFragment(outState, GameLocalFragment.class.getName(), gameFragment);
    }
}
