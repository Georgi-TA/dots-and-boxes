package com.touchawesome.dotsandboxes.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.utils.Globals;

public class ChooseLayoutFragment extends Fragment {
    public static final int FRAGMENT_ID = 2626;

    private Button threeByThree;
    private Button fourByFour;
    private Button fiveByFive;
    private OnFragmentInteractionListener mListener;

    public static ChooseLayoutFragment newInstance(Bundle args) {
        ChooseLayoutFragment fragment = new ChooseLayoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ChooseLayoutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_choose_layout, container, false);

        threeByThree = (Button) root.findViewById(R.id.buttonPlayThreeByThree);
        fourByFour = (Button) root.findViewById(R.id.buttonPlayFourByFour);
        fiveByFive = (Button) root.findViewById(R.id.buttonPlayFiveByFive);

        threeByThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLayoutChosen(getArguments(), 3, 3);
            }
        });
        threeByThree.setTypeface(Globals.kgTrueColors);

        fourByFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLayoutChosen(getArguments(), 4, 4);
            }
        });
        fourByFour.setTypeface(Globals.kgTrueColors);

        fiveByFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLayoutChosen(getArguments(), 5, 5);
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
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setListener(OnFragmentInteractionListener listener) {
        this.mListener = listener;
    }

    public interface OnFragmentInteractionListener {
        void onLayoutChosen(Bundle args, int rows, int columns);
    }
}
