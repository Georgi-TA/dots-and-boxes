package com.touchawesome.dotsandboxes.activities;

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
import com.touchawesome.dotsandboxes.utils.Constants;
import com.touchawesome.dotsandboxes.utils.Globals;

public class GameActivity extends AppCompatActivity implements GameLocalFragment.OnFragmentInteractionListener,
                                                                WinnerFragment.OnFragmentInteractionListener,
                                                                FragmentManager.OnBackStackChangedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = (TextView) toolbar.findViewById(R.id.game_title);
        title.setTypeface(Globals.kgTrueColors);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        loadGameFragment();
    }

    private void loadGameFragment() {
        GameLocalFragment gameFragment = new GameLocalFragment();
        Bundle args = getIntent().getBundleExtra(Constants.INTENT_GAME_EXTRA_BUNDLE);
        gameFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                                        R.anim.enter_from_left, R.anim.exit_to_right);
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
        // loadGameFragment();
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
