package com.touchawesome.dotsandboxes.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.touchawesome.dotsandboxes.R;
import com.squareup.picasso.Picasso;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.game.models.Edge;
import com.touchawesome.dotsandboxes.game.models.PlayerBot;
import com.touchawesome.dotsandboxes.utils.CircleTransform;
import com.touchawesome.dotsandboxes.utils.Globals;
import com.touchawesome.dotsandboxes.views.BoardView;

import java.util.Locale;

public class GameLocalFragment extends Fragment implements Game.GameListener, View.OnTouchListener {
    public static final int FRAGMENT_ID = 6164;
    public static final String ARG_MODE = "com.touchawesome.args.mode";
    private static final String ARG_BOARD = "com.touchawesome.args.board";
    private static final String ARG_P1_NEXT = "com.touchawesome.args.scoreView.p1_next";
    public static final String ARG_STATUS = "com.touchawesome.args.status";
    public static final String ARG_WINNER_NAME = "com.touchawesome.args.winner.name";
    public static final String ARG_WINNER_SCORE = "com.touchawesome.args.winner.points";

    private Game.Mode mode;                             // Mode of play - local, network, cpu
    private OnFragmentInteractionListener mListener;

    private BoardView boardView;
    private Game game;         // game state object and controller
    private PlayerBot bot;     // the bot to play in CPU mode

    private int rows = 3;
    private int columns = 3;

    private TransitionDrawable tdPlayer1;
    private TransitionDrawable tdPlayer2;

    private TextView scorePlayer1;
    private TextView scorePlayer2;

    private TextView turnText;

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

        rows = getArguments().getInt("rows", 3);
        columns = getArguments().getInt("columns", 3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game_local, container, false);
        boardView = (BoardView) root.findViewById(R.id.boardView);

        turnText = (TextView) root.findViewById(R.id.turnText);
        turnText.setTypeface(Globals.kgTrueColors);
        turnText.setText(String.format(Locale.getDefault(), getString(R.string.turn_text), getString(R.string.player1name)));
        scorePlayer1 = (TextView) root.findViewById(R.id.player1_score);
        scorePlayer1.setTypeface(Globals.kgTrueColors);
        scorePlayer2 = (TextView) root.findViewById(R.id.player2_score);
        scorePlayer2.setTypeface(Globals.kgTrueColors);

        ImageView imagePlayer1 = (ImageView) root.findViewById(R.id.player1_image);
        ImageView imagePlayer2 = (ImageView) root.findViewById(R.id.player2_image);

        ImageView imagePlayer1Border = (ImageView) root.findViewById(R.id.player1_border);
        ImageView imagePlayer2Border = (ImageView) root.findViewById(R.id.player2_border);

        // set transition drawable to player 1 border
        tdPlayer1 = new TransitionDrawable( new Drawable[] {
                getResources().getDrawable(R.drawable.bg_player1_image_active),
                getResources().getDrawable(R.drawable.bg_player_image_inactive)
        });
        imagePlayer1Border.setImageDrawable(tdPlayer1);

        // set transition drawable to player 2 border
        tdPlayer2 = new TransitionDrawable( new Drawable[] {
                getResources().getDrawable(R.drawable.bg_player_image_inactive),
                getResources().getDrawable(R.drawable.bg_player2_image_active)
        });
        imagePlayer2Border.setImageDrawable(tdPlayer2);

        int padding = getResources().getDimensionPixelSize(R.dimen.player_image_border);
        Picasso.with(getContext())
                .load(R.drawable.sol_image)
                .resize(100, 100)
                .into(imagePlayer1);

        Picasso.with(getContext())
                .load(R.drawable.ky_image)
                .resize(100, 100)
                .into(imagePlayer2);

        bot = new PlayerBot();
        game = new Game(getContext(), rows, columns);
        game.registerListener(this);
        boardView.setGame(game);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(ARG_BOARD))
                game.getBoard().loadBoard(savedInstanceState.getString(ARG_BOARD));

            if (savedInstanceState.containsKey(ARG_P1_NEXT)) {
                Game.Player nextPlayer = savedInstanceState.getBoolean(ARG_P1_NEXT) ? Game.Player.PLAYER1 : Game.Player.PLAYER2;
                game.setNextPlayer(nextPlayer);
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
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onScoreChange(Game.Player player, int score) {
        if (player == Game.Player.PLAYER1)
            scorePlayer1.setText(String.format(Locale.getDefault(), "%d", score));
        else
            scorePlayer2.setText(String.format(Locale.getDefault(), "%d", score));
    }

    @Override
    public void onTurnChange(Game.Player nextToMove) {
        if (mode == Game.Mode.CPU) {
            if (nextToMove == Game.Player.PLAYER2) {
                Edge nextMove = bot.getNextMove(game);
                game.makeAMove(nextMove.getDotStart(), nextMove.getDotEnd(), Game.Player.PLAYER2);
            }
        }

        // UI player images border
        if (nextToMove == Game.Player.PLAYER2) {
            tdPlayer1.startTransition(100);
            tdPlayer2.startTransition(100);
            turnText.setText(String.format(Locale.getDefault(), getString(R.string.turn_text), getString(R.string.player2name)));
        }
        else {
            tdPlayer1.reverseTransition(100);
            tdPlayer2.reverseTransition(100);
            turnText.setText(String.format(Locale.getDefault(), getString(R.string.turn_text), getString(R.string.player1name)));
        }
    }

    @Override
    public void onGameEnd(Game.Player winner) {
        boardView.setOnTouchListener(this);
        Bundle args = getArguments();
        args.putString(ARG_STATUS, winner.name());

        int player1Score = game.getBoard().getScore(Game.Player.PLAYER1);
        int player2Score = game.getBoard().getScore(Game.Player.PLAYER2);

        if (mode == Game.Mode.CPU) {
            args.putString(ARG_WINNER_NAME, getString(R.string.player1name));
        }
        else if (player1Score > player2Score){
            args.putString(ARG_WINNER_NAME, getString(R.string.player1name));
            args.putInt(ARG_WINNER_SCORE, game.getBoard().getScore(Game.Player.PLAYER1));
        }
        else {
            args.putString(ARG_WINNER_NAME, getString(R.string.player2name));
            args.putInt(ARG_WINNER_SCORE, game.getBoard().getScore(Game.Player.PLAYER2));
        }


        mListener.onGameLocalFragmentInteraction(WinnerFragment.FRAGMENT_ID, args);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false; // block all user interaction
    }

    public interface OnFragmentInteractionListener {
        void onGameLocalFragmentInteraction(int fragmentId, Bundle args);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_BOARD, game.getBoard().toString());
        outState.putBoolean(ARG_P1_NEXT, game.getNext() == Game.Player.PLAYER1);
        super.onSaveInstanceState(outState);
    }
}