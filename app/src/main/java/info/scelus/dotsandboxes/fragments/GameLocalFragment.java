package info.scelus.dotsandboxes.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.blackbear.scelus.dotsandboxes.R;
import info.scelus.dotsandboxes.external.Board;
import info.scelus.dotsandboxes.external.Game;
import info.scelus.dotsandboxes.views.BoardView;
import info.scelus.dotsandboxes.views.ScoreView;

public class GameLocalFragment extends Fragment {
    public static final int FRAGMENT_ID = 6164;
    public static final String ARG_MODE = "info.scelus.args.mode";
    private static final String ARG_BOARD = "info.scelus.args.board";
    private static final String ARG_P1_SCORE = "info.scelus.args.score.p1";
    private static final String ARG_P2_SCORE = "info.scelus.args.score.p2";
    private static final String ARG_P1_NEXT = "info.scelus.args.score.p1_next";

    private Game.Mode mode;
    private Board board;
    private BoardView boardView;
    private ScoreView score;
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
        score = (ScoreView) root.findViewById(R.id.scoreLayout);

        game = new Game(rows * columns);
        board = new Board(game, rows, columns);
        boardView.setBoard(board);
        game.registerListener(score);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(ARG_BOARD))
                board.loadBoard(savedInstanceState.getString(ARG_BOARD));

            if (savedInstanceState.containsKey(ARG_P1_SCORE) && savedInstanceState.containsKey(ARG_P2_SCORE) && savedInstanceState.containsKey(ARG_P1_NEXT)) {
                Game.Player nextPlayer = savedInstanceState.getBoolean(ARG_P1_NEXT) ? Game.Player.PLAYER1 : Game.Player.PLAYER2;
                game.setInitScoreAndTurn(savedInstanceState.getInt(ARG_P1_SCORE), savedInstanceState.getInt(ARG_P2_SCORE), nextPlayer);
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
        outState.putString(ARG_BOARD, board.toString());
        outState.putInt(ARG_P1_SCORE, board.getScore(Game.Player.PLAYER1));
        outState.putInt(ARG_P2_SCORE, board.getScore(Game.Player.PLAYER2));
        outState.putBoolean(ARG_P1_NEXT, game.getNext() == Game.Player.PLAYER1);
        super.onSaveInstanceState(outState);
    }
}
