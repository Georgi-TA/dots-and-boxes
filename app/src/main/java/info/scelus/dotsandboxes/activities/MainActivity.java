package info.scelus.dotsandboxes.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import info.blackbear.scelus.dotsandboxes.R;
import info.scelus.dotsandboxes.fragments.ComingSoonFragment;
import info.scelus.dotsandboxes.fragments.GameLocalFragment;
import info.scelus.dotsandboxes.fragments.LocalMenuFragment;
import info.scelus.dotsandboxes.fragments.MainMenuFragment;
import info.scelus.dotsandboxes.utils.Globals;

public class MainActivity extends AppCompatActivity
                          implements MainMenuFragment.OnFragmentInteractionListener,
                                     LocalMenuFragment.OnFragmentInteractionListener,
                                     GameLocalFragment.OnFragmentInteractionListener {

    private static final String ARG_GAME_IN_PROGRESS = "info.scelus.args.gameinprogress";
    private TextView mainTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the toolbar as a support appbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mainTitle = (TextView) findViewById(R.id.mainLocalTitleText);
        setSupportActionBar(toolbar);

        // Setup action bar appearance
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();

        if (backStackCount <= 1)
            finish();
        else
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
                // TODO: link to settings screen
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        setFonts();
    }


    private void setFonts () {
        mainTitle.setTypeface(Globals.kgTrueColors);
    }

    private void loadFragment(int fragmentId, Bundle args) {
        Fragment fragment;
        switch (fragmentId) {
            case MainMenuFragment.FRAGMENT_ID: {
                fragment = MainMenuFragment.newInstance(args);
                break;
            }
            case LocalMenuFragment.FRAGMENT_ID: {
                fragment = LocalMenuFragment.newInstance(args);
                break;
            }
            case GameLocalFragment.FRAGMENT_ID: {
                fragment = GameLocalFragment.newInstance(args);
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

    @Override
    public void onMainMenuFragmentInteraction(int fragmentId, Bundle args) {
        loadFragment(fragmentId, args);
    }

    @Override
    public void onLocalMenuFragmentInteraction(int fragmentId, Bundle args) {
        loadFragment(fragmentId, args);
    }

    @Override
    public void onGameLocalFragmentInteraction(Uri uri) {

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // check if game is in progress
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName().equals(GameLocalFragment.class.getName()))
            outState.putBoolean(ARG_GAME_IN_PROGRESS, true);
        super.onSaveInstanceState(outState);
    }
}
