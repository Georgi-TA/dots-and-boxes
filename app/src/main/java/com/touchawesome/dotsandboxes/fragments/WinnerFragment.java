package com.touchawesome.dotsandboxes.fragments;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.utils.Globals;

import java.util.Locale;

/**
 * Created by scelus on 16.03.17
 */
public class WinnerFragment extends DialogFragment {
    public static final Uri replay = Uri.fromParts("dotsandboxes", "action", "replay");
    public static final Uri mainMenu = Uri.fromParts("dotsandboxes", "action", "mainmenu");
    public static final Uri achievements = Uri.fromParts("dotsandboxes", "action", "achievements");

    public static final int FRAGMENT_ID = 31783;
    String winnerName;
    int winnerScore;
    Game.Player winner;

    private WinnerFragment.OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onReplayRequested(Bundle arguments);
        void onMenuRequested();
    }

    public static WinnerFragment newInstance(Bundle args) {
        WinnerFragment f = new WinnerFragment();

        if (args != null)
            f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        winner = Game.Player.valueOf(getArguments().getString(GameLocalFragment.ARG_STATUS));
        winnerName = getArguments().getString(GameLocalFragment.ARG_WINNER_NAME);
        winnerScore = getArguments().getInt(GameLocalFragment.ARG_WINNER_SCORE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_winner, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = (TextView) view.findViewById(R.id.winner_title);
        title.setTypeface(Globals.kgTrueColors);
        title.setText(R.string.you_win);

//        switch (winner) {
//            case PLAYER1:
//                title.setText(R.string.you_win);
//                break;
//            case NONE:
//                title.setText(R.string.draw);
//                break;
//            case PLAYER2:
//                title.setText(R.string.you_lose);
//                break;
//        }

        TextView name = (TextView) view.findViewById(R.id.winner_name);
        name.setTypeface(Globals.kgTrueColors);
        name.setText(winnerName);

        TextView score = (TextView) view.findViewById(R.id.winner_score);
        score.setTypeface(Globals.kgTrueColors);
        score.setText(String.format(Locale.getDefault(), getString(R.string.winner_points), winnerScore));

        ImageButton replayButton = (ImageButton) view.findViewById(R.id.replay_button);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onReplayRequested(getArguments());
                dismiss();
            }
        });

        ImageButton mainMenuButton = (ImageButton) view.findViewById(R.id.winner_menu_button);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMenuRequested();
                dismiss();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (WinnerFragment.OnFragmentInteractionListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
