package info.scelus.dotsandboxes.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import info.blackbear.scelus.dotsandboxes.R;
import info.scelus.dotsandboxes.utils.Globals;

public class MainMenuFragment extends Fragment {
    public static final int FRAGMENT_ID = 4367;
    private OnFragmentInteractionListener mListener;

    private TextView mainTitle;
    private Button localPlayButton;
    private Button networkPlayButton;

    public static MainMenuFragment newInstance(Bundle args) {
        MainMenuFragment fragment = new MainMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MainMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setFonts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_main, container, false);
        mainTitle = (TextView) root.findViewById(R.id.mainTitleText);
        localPlayButton = (Button) root.findViewById(R.id.buttonPlayLocal);
        networkPlayButton = (Button) root.findViewById(R.id.buttonPlayNetwork);

        localPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMainMenuFragmentInteraction(LocalMenuFragment.FRAGMENT_ID, new Bundle());
                }
            }
        });

        networkPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMainMenuFragmentInteraction(ComingSoonFragment.FRAGMENT_ID, new Bundle());
                }
            }
        });

        return root;
    }

    private void setFonts () {
        mainTitle.setTypeface(Globals.kgTrueColors);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onMainMenuFragmentInteraction(int fragmentId, Bundle args);
    }
}
