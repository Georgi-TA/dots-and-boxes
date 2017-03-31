package com.touchawesome.dotsandboxes.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.fragments.GameLocalFragment;
import com.touchawesome.dotsandboxes.fragments.WinnerFragment;
import com.touchawesome.dotsandboxes.services.MusicIntentService;
import com.touchawesome.dotsandboxes.utils.Constants;
import com.touchawesome.dotsandboxes.utils.Globals;

public class GameActivity extends AppCompatActivity implements GameLocalFragment.OnFragmentInteractionListener,
                                                                WinnerFragment.OnFragmentInteractionListener,
                                                                FragmentManager.OnBackStackChangedListener{


    private MusicIntentService mService;
    private boolean mBoundMusicService = false;

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mBoundMusicService = true;

            // This is called when taw service object.
            mService = ((MusicIntentService.LocalBinder) binder).getService();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean playMusic = prefs.getBoolean(getString(R.string.pref_key_music), false);

            if (mBoundMusicService && playMusic)
                mService.send(new Intent(MusicIntentService.ACTION_PLAY_MUSIC));
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundMusicService = false;
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        if (mService != null)
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
        if (mBoundMusicService) {
            unbindService(mConnection);
            mBoundMusicService = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        loadGameFragment();
    }

    private void loadGameFragment() {
        GameLocalFragment gameFragment = new GameLocalFragment();
        Bundle args = getIntent().getBundleExtra(Constants.INTENT_GAME_EXTRA_BUNDLE);
        gameFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
//                                        R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.content, gameFragment);
        transaction.commit();
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
    public void onBackStackChanged() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();

        if (backStackCount < 1)
            finish();
    }

    @Override
    public void onReplayRequested(Bundle arguments) {
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
}
