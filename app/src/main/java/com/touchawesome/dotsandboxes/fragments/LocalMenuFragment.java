package com.touchawesome.dotsandboxes.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blackbear.scelus.dotsandboxes.R;
import com.touchawesome.dotsandboxes.game.controllers.Game;

public class LocalMenuFragment extends Fragment {
    public static final int FRAGMENT_ID = 5267;
    private OnFragmentInteractionListener mListener;



    public static LocalMenuFragment newInstance(Bundle args) {
        LocalMenuFragment fragment = new LocalMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LocalMenuFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_local, container, false);
        Button player2PlayButton = (Button) root.findViewById(R.id.buttonPlayLocalPlayer);
        Button cpuPlayButton = (Button) root.findViewById(R.id.buttonPlayLocalComputer);

        player2PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    Bundle args = new Bundle();
                    args.putSerializable(GameLocalFragment.ARG_MODE, Game.Mode.PLAYER);
                    mListener.onLocalMenuFragmentInteraction(GameLocalFragment.FRAGMENT_ID, args);
                }
            }
        });

        cpuPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    Bundle args = new Bundle();
                    args.putSerializable(GameLocalFragment.ARG_MODE, Game.Mode.CPU);
                    mListener.onLocalMenuFragmentInteraction(GameLocalFragment.FRAGMENT_ID, args);
                }
            }
        });

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

    public interface OnFragmentInteractionListener {
        void onLocalMenuFragmentInteraction(int fragmentId, Bundle args);
    }
}
