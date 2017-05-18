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

public class ChooseBoardSizeFragment extends Fragment {
    public static final int FRAGMENT_ID = 2626;

    private OnFragmentInteractionListener mListener;

    public static ChooseBoardSizeFragment newInstance(Bundle args) {
        ChooseBoardSizeFragment fragment = new ChooseBoardSizeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ChooseBoardSizeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // analytics
        // Get tracker.
        Tracker t = ((App) getActivity().getApplication()).getTracker(App.TrackerName.APP_TRACKER);
        // Set screen name.
        t.setScreenName(getString(R.string.screen_name_choose_board_size));
        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_choose_layout, container, false);

        ((TextView) root.findViewById(R.id.label_choose_board_size)).setTypeface(Globals.kgTrueColors);

        Button threeByThree = (Button) root.findViewById(R.id.buttonPlayThreeByThree);
        Button fourByFour = (Button) root.findViewById(R.id.buttonPlayFourByFour);
        Button fiveByFive = (Button) root.findViewById(R.id.buttonPlayFiveByFive);

        threeByThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = getArguments();
                args.putInt("rows", 3);
                args.putInt("columns", 3);
                mListener.onLayoutChosen(args);
            }
        });
        threeByThree.setTypeface(Globals.kgTrueColors);

        fourByFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = getArguments();
                args.putInt("rows", 4);
                args.putInt("columns", 4);
                mListener.onLayoutChosen(args);
            }
        });
        fourByFour.setTypeface(Globals.kgTrueColors);

        fiveByFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = getArguments();
                args.putInt("rows", 5);
                args.putInt("columns", 5);
                mListener.onLayoutChosen(getArguments());
            }
        });
        fiveByFive.setTypeface(Globals.kgTrueColors);

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
        void onLayoutChosen(Bundle args);
    }
}
