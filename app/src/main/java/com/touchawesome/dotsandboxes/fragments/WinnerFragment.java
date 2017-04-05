package com.touchawesome.dotsandboxes.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
    public static final int FRAGMENT_ID = 31783;
    String winnerName;
    Game.Player winner;

    private WinnerFragment.OnFragmentInteractionListener mListener;
    private int player1Score;
    private int player2Score;

    public interface OnFragmentInteractionListener {
        void onReplayRequested(Bundle arguments);
        void onAchievementsRequested();
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


        player1Score = getArguments().getInt(GameLocalFragment.ARG_PLAYER1_SCORE);
        player2Score = getArguments().getInt(GameLocalFragment.ARG_PLAYER2_SCORE);
        Game.Mode mode = (Game.Mode) getArguments().getSerializable(GameLocalFragment.ARG_GAME_MODE);

        if (player1Score > player2Score) {
            winner = Game.Player.PLAYER1;
            winnerName = getString(R.string.player1name);
        }
        else if (player1Score < player2Score) {
            winner = Game.Player.PLAYER2;
            if (mode == Game.Mode.CPU) {
                winnerName = getString(R.string.robot_name);
            }
            else {
                winnerName = getString(R.string.player2name);
            }
        }
        else {
            winner = Game.Player.NONE;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_winner, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = (TextView) view.findViewById(R.id.result_state);
        title.setTypeface(Globals.kgTrueColors);

        TextView name = (TextView) view.findViewById(R.id.winner_name);
        name.setTypeface(Globals.kgTrueColors);
        name.setText(winnerName);

        switch (winner) {
            case PLAYER1:
                name.setTextColor(ContextCompat.getColor(getContext(), R.color.boxPlayer1));
                title.setText(R.string.wins);
                break;
            case NONE:
                name.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorPrimaryDark));
                title.setText(getString(R.string.draw));
                break;
            case PLAYER2:
                name.setTextColor(ContextCompat.getColor(getContext(), R.color.boxPlayer2));
                title.setText(R.string.wins);
                break;
            default:
        }

        TextView resultLabel = (TextView) view.findViewById(R.id.result_label);
        resultLabel.setTypeface(Globals.kgTrueColors);

        TextView score = (TextView) view.findViewById(R.id.result_score);
        score.setTypeface(Globals.kgTrueColors);
        score.setText(String.format(Locale.getDefault(), getString(R.string.winner_points), player1Score, player2Score));

        ImageButton replayButton = (ImageButton) view.findViewById(R.id.replay_button);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onReplayRequested(getArguments());
                dismiss();
            }
        });

        ImageButton achievementsButton = (ImageButton) view.findViewById(R.id.button_achievements);
        achievementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAchievementsRequested();
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
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
