package com.touchawesome.dotsandboxes.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.game.models.Edge;
import com.touchawesome.dotsandboxes.game.models.PlayerBot;
import com.touchawesome.dotsandboxes.utils.Globals;
import com.touchawesome.dotsandboxes.views.BoardView;

import java.util.Locale;

import static com.google.android.gms.internal.zzt.TAG;

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

    private TransitionDrawable tdPlayer1;
    private TransitionDrawable tdPlayer2;

    private TextView scorePlayer1;
    private TextView scorePlayer2;

    private TextView turnText;
    private ProgressBar progressBar;

    public static GameLocalFragment newInstance(Bundle args) {
        GameLocalFragment fragment = new GameLocalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public GameLocalFragment() {
    }

    private class BotMoveAsyncTask extends AsyncTask<Void, Integer, Edge> {

        protected void onPreExecute () {
            blockBoard();
        }

        protected Edge doInBackground(Void... params) {
            return bot.getNextMove();
        }

        protected void onProgressUpdate(Integer... progress) { }

        protected void onPostExecute(Edge result) {
            Log.d(TAG, "onPostExecute: " + result.getKey());
            takeTurnFromBot(result);
        }
    }

    private void takeTurnFromBot(Edge move) {
        boolean moveValid = game.makeAMove(move.getDotStart(), move.getDotEnd(), Game.Player.PLAYER2);
        if (!moveValid) {
            new BotMoveAsyncTask().execute();
        }
        else {
            boardView.invalidate();
            unblockBoard();
        }
    }

    private void blockBoard() {
        boardView.disableInteraction();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void unblockBoard() {
        boardView.enableInteraction();
        progressBar.setVisibility(View.GONE);
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

        int rows = getArguments().getInt("rows", 3);
        int columns = getArguments().getInt("columns", 3);

        game = new Game(rows, columns);
        game.registerListener(this);
        bot = new PlayerBot(game);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game_local, container, false);

        boardView = (BoardView) root.findViewById(R.id.boardView);
        boardView.setGame(game);
        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
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

//        Picasso.with(getContext())
//                .load(R.drawable.ic_db_player_1)
//                .resize(100, 100)
//                .into(imagePlayer1);
//
//        Picasso.with(getContext())
//                .load(R.drawable.ic_db_player_2)
//                .resize(100, 100)
//                .into(imagePlayer2);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(ARG_BOARD))
                game.getBoard().loadBoard(savedInstanceState.getString(ARG_BOARD));

            if (savedInstanceState.containsKey(ARG_P1_NEXT)) {
                Game.Player nextPlayer = savedInstanceState.getBoolean(ARG_P1_NEXT) ? Game.Player.PLAYER1 : Game.Player.PLAYER2;
                game.setNextPlayer(nextPlayer);
            }
        }

        unblockBoard();
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
        if (mode == Game.Mode.CPU && nextToMove == Game.Player.PLAYER2) {
            new BotMoveAsyncTask().execute();
        }

        if (nextToMove == Game.Player.PLAYER2) {
            tdPlayer1.startTransition(100);
            tdPlayer2.startTransition(100);
            turnText.setText(String.format(Locale.getDefault(), getString(R.string.turn_text), getString(R.string.player2name)));
        } else {
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
        return false;
    }

    public interface OnFragmentInteractionListener {
        void onGameLocalFragmentInteraction(int fragmentId, Bundle args);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_BOARD, game.getBoard().toString());
        outState.putBoolean(ARG_P1_NEXT, game.getState() == Game.State.PLAYER1_TURN);
        super.onSaveInstanceState(outState);
    }
}