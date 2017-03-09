package info.scelus.dotsandboxes.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.blackbear.scelus.dotsandboxes.R;
import info.scelus.dotsandboxes.game.controllers.Game;
import info.scelus.dotsandboxes.views.BoardView;
import info.scelus.dotsandboxes.views.ScoreView;

public class GameLocalFragment extends Fragment {
    public static final int FRAGMENT_ID = 6164;
    public static final String ARG_MODE = "info.scelus.args.mode";
    private static final String ARG_BOARD = "info.scelus.args.board";
    private static final String ARG_P1_NEXT = "info.scelus.args.scoreView.p1_next";

    private Game.Mode mode;
    private BoardView boardView;
    private ScoreView scoreView;
    private OnFragmentInteractionListener mListener;
    private Game game;

    private int rows = 6;
    private int columns = 6;

    public static GameLocalFragment newInstance(Bundle args) {
        GameLocalFragment fragment = new GameLocalFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public GameLocalFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_MODE))
                mode = (Game.Mode) getArguments().getSerializable(ARG_MODE);
            else
                mode = Game.Mode.PLAYER;
        }
        else {
            mode = Game.Mode.PLAYER;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game_local, container, false);
        boardView = (BoardView) root.findViewById(R.id.boardView);
        scoreView = (ScoreView) root.findViewById(R.id.scoreLayout);

        game = new Game(rows, columns);
        game.registerListener(scoreView);
        boardView.setGame(game);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(ARG_BOARD))
                game.getBoard().loadBoard(savedInstanceState.getString(ARG_BOARD));

            if (savedInstanceState.containsKey(ARG_P1_NEXT)) {
                Game.Player nextPlayer = savedInstanceState.getBoolean(ARG_P1_NEXT) ? Game.Player.PLAYER1 : Game.Player.PLAYER2;
                game.setInitialTurn(nextPlayer);
            }
        }

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
        void onGameLocalFragmentInteraction(Uri uri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_BOARD, game.getBoard().toString());
        outState.putBoolean(ARG_P1_NEXT, game.getNext() == Game.Player.PLAYER1);
        super.onSaveInstanceState(outState);
    }
}