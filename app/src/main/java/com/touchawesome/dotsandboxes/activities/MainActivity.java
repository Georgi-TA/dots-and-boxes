package com.touchawesome.dotsandboxes.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
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
import com.touchawesome.dotsandboxes.utils.Globals;

public class MainActivity extends AppCompatActivity
                          implements MainMenuFragment.OnFragmentInteractionListener,
                                     GameLocalFragment.OnFragmentInteractionListener,
                                     WinnerFragment.OnFragmentInteractionListener,
                                     ChooseLayoutFragment.OnFragmentInteractionListener,
                                     FragmentManager.OnBackStackChangedListener {

    private static final String ARG_GAME_IN_PROGRESS = "info.scelus.args.gameinprogress";
    private MusicIntentService mService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the toolbar as a support appbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup action bar appearance
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("");

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean playMusic = prefs.getBoolean(getString(R.string.pref_key_music), false);

        if (mBound && playMusic)
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBound = true;

            // This is called when taw service object.
            mService = new MusicIntentService(service);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean playMusic = prefs.getBoolean(getString(R.string.pref_key_music), false);

            if (mBound && playMusic)
                mService.send(new Intent(MusicIntentService.ACTION_START_MUSIC));
        }

        public void onServiceDisconnected(ComponentName className) {
            mBound = false;
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
        Intent intent = new Intent(this, NetworkPlayActivity.class);
        startActivity(intent);
    }

    @Override
    public void onGameLocalFragmentInteraction(int fragmentId, Bundle args) {
        if (fragmentId == WinnerFragment.FRAGMENT_ID) {
            WinnerFragment dialog = WinnerFragment.newInstance(args);
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "dialog_fragment");
            dialog.setCancelable(false);
        }
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

        if (backStackCount < 1)
            finish();
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
        loadFragment(GameLocalFragment.FRAGMENT_ID, args);
    }

    @Override
    public void onReplayRequested(Bundle arguments) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(); // pop the dialog
        fragmentManager.popBackStackImmediate(); // pop the game fragment
        loadFragment(GameLocalFragment.FRAGMENT_ID, arguments);
    }

    @Override
    public void onMenuRequested() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.getBackStackEntryCount() > 1)
            fragmentManager.popBackStackImmediate();
    }
}
