package com.touchawesome.dotsandboxes.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.touchawesome.dotsandboxes.App;
import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.utils.Globals;

public class ChooseModeFragment extends Fragment {
    public static final int FRAGMENT_ID = 4367;
    private OnFragmentInteractionListener mListener;

    public static ChooseModeFragment newInstance(Bundle args) {
        ChooseModeFragment fragment = new ChooseModeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ChooseModeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // analytics
        // Get tracker.
        Tracker t = ((App) getActivity().getApplication()).getTracker(App.TrackerName.APP_TRACKER);
        // Set screen name.
        t.setScreenName(getString(R.string.screen_name_choose_mode));
        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_main, container, false);
        Button friendPlayButton = (Button) root.findViewById(R.id.buttonPlayFriend);
        Button computerPlayButton = (Button) root.findViewById(R.id.buttonPlayLocalComputer);
//        Button networkPlayButton = (Button) root.findViewById(R.id.buttonPlayNetwork);


        ((TextView) root.findViewById(R.id.label_play)).setTypeface(Globals.kgTrueColors);

        friendPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onFriendPlaySelected();
                }
            }
        });
        friendPlayButton.setTypeface(Globals.kgTrueColors);

        computerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onComputerPlaySelected();
                }
            }
        });
        computerPlayButton.setTypeface(Globals.kgTrueColors);

//        networkPlayButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mListener != null) {
//                    mListener.onNetworkPlaySelected();
//                }
//            }
//        });

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onComputerPlaySelected();
        void onFriendPlaySelected();
        void onNetworkPlaySelected();
    }
}
