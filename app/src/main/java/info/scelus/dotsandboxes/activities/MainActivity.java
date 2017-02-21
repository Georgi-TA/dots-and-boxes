package info.scelus.dotsandboxes.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import info.blackbear.scelus.dotsandboxes.R;
import info.scelus.dotsandboxes.external.Board;
import info.scelus.dotsandboxes.fragments.ComingSoonFragment;
import info.scelus.dotsandboxes.fragments.GameLocalFragment;
import info.scelus.dotsandboxes.fragments.LocalMenuFragment;
import info.scelus.dotsandboxes.fragments.MainMenuFragment;
import info.scelus.dotsandboxes.views.BoardView;

public class MainActivity extends FragmentActivity
                          implements MainMenuFragment.OnFragmentInteractionListener,
        LocalMenuFragment.OnFragmentInteractionListener,
        GameLocalFragment.OnFragmentInteractionListener {

    private Board board;
    private BoardView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(MainMenuFragment.FRAGMENT_ID, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(fragment.toString());
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
}
