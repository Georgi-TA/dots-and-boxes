package com.touchawesome.dotsandboxes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.touchawesome.dotsandboxes.App;
import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.db.GameScore;
import com.touchawesome.dotsandboxes.event_bus.RxBus;
import com.touchawesome.dotsandboxes.event_bus.events.EmitSoundEvent;
import com.touchawesome.dotsandboxes.event_bus.events.BotComputeEvent;
import com.touchawesome.dotsandboxes.event_bus.events.GameEndEvent;
import com.touchawesome.dotsandboxes.event_bus.events.OpponentMoveEvent;
import com.touchawesome.dotsandboxes.event_bus.events.PlayerMoveEvent;
import com.touchawesome.dotsandboxes.event_bus.events.ScoreMadeEvent;
import com.touchawesome.dotsandboxes.event_bus.events.SquareCompletedEvent;
import com.touchawesome.dotsandboxes.event_bus.events.TurnChangeEvent;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.game.models.Edge;
import com.touchawesome.dotsandboxes.game.controllers.PlayerBot;
import com.touchawesome.dotsandboxes.utils.Globals;
import com.touchawesome.dotsandboxes.utils.Constants;
import com.touchawesome.dotsandboxes.views.BoardView;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class GameFragment extends Fragment implements View.OnTouchListener {
    private final String TAG = GameFragment.class.getCanonicalName();

    public static final int FRAGMENT_ID = 6164;
    public static final String ARG_MODE = "com.touchawesome.args.mode";
    private static final String ARG_BOARD = "com.touchawesome.args.board";
    private static final String ARG_P1_NEXT = "com.touchawesome.args.scoreView.p1_next";

    public static final String ARG_PLAYER1_SCORE = "com.touchawesome.args.score.player1";
    public static final String ARG_PLAYER2_SCORE = "com.touchawesome.args.score.player2";
    public static final String ARG_GAME_MODE = "com.touchawesome.args.game.mode";

    private final int BOT_DELAY_TIME = 750; //ms
    private boolean shouldVibrate;

    private Subscription subscription;

    private Game.Mode mode;                             // Mode of play - local, network, cpu
    private OnFragmentInteractionListener mListener;

    private BoardView boardView;
    private Game game;         // game state object and controller
    private PlayerBot bot;     // the bot to play in CPU mode

    private TextView scorePlayer1;
    private TextView scorePlayer2;

    private TextView turnText;
    private ProgressBar progressBar;

    private Vibrator vibrator;

    private CountDownTimer progressTimer = new CountDownTimer(BOT_DELAY_TIME, 4) {
        @Override
        public void onTick(long millisUntilFinished) {
            progressBar.setProgress((int) ((float) millisUntilFinished / 4f));
        }

        @Override
        public void onFinish() {
            progressBar.setProgress(0);
        }
    };

    private TransitionDrawable transitionDrawablePlayer1;
    private TransitionDrawable transitionDrawablePlayer2;

    public static GameFragment newInstance(Bundle args) {
        GameFragment fragment = new GameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public GameFragment() {

    }

    private void initBusSubscription() {
        subscription = RxBus.getInstance()
                            .getBus()
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Object>() {
            static final String TAG = "RxBusGameFragment";

            @Override
            public void call(Object event) {
                Log.i(TAG, "call: " + event.getClass().toString());

                /*
                 * The bot made a move
                 */
                if (event instanceof BotComputeEvent) {
                    BotComputeEvent botMoveEvent = (BotComputeEvent) event;
                    int boxesCompleted = game.makeAMove(botMoveEvent.botMove.getDotStart(), botMoveEvent.botMove.getDotEnd());
                    boardView.invalidate();

                    if (boxesCompleted > 0 && game.getState() != Game.State.END) {
                        takeTurnFromBot();
                    }
                    else {
                        boardView.invalidate();
                        boardView.enableInteraction();

                        setTurnText(Game.Player.PLAYER1);

                        // change the avatar borders
                        transitionDrawablePlayer1.reverseTransition(100);
                        transitionDrawablePlayer2.reverseTransition(100);
                    }

                    if (boxesCompleted > 0) {
                        RxBus.getInstance().send(new SquareCompletedEvent());
                    }
                }

                /*
                 *  The player made a move
                 */
                else if (event instanceof PlayerMoveEvent) {
                    PlayerMoveEvent playermoveEvent = (PlayerMoveEvent) event;
                    int boxesCompleted = game.makeAMove(playermoveEvent.playerMove.getDotStart(), playermoveEvent.playerMove.getDotEnd());
                    boardView.invalidate();

                    if (boxesCompleted == 0) {
                        setTurnText(Game.Player.PLAYER2);

                        // change the avatar borders
                        transitionDrawablePlayer1.startTransition(100);
                        transitionDrawablePlayer2.startTransition(100);

                        if (mode == Game.Mode.CPU) {
                            takeTurnFromBot();
                        }
                    }

                    if (boxesCompleted > 0) {
                        RxBus.getInstance().send(new SquareCompletedEvent());
                    }
                }

                /*
                 *  The opponent made a move
                 */
                else if (event instanceof OpponentMoveEvent) {
                    setTurnText(Game.Player.PLAYER1);

                    // change the avatar borders
                    transitionDrawablePlayer1.reverseTransition(100);
                    transitionDrawablePlayer2.reverseTransition(100);
                }
                else if (event instanceof TurnChangeEvent) {
                    setTurnText(((TurnChangeEvent) event).nextPlayer);
                }
                /*
                 *  Register the event and reflect the score
                 */
                else if (event instanceof ScoreMadeEvent) {
                    ScoreMadeEvent scoreEvent = (ScoreMadeEvent) event;
                    if (scoreEvent.scoringPlayer == Game.Player.PLAYER1) {
                        scorePlayer1.setText(String.format(Locale.getDefault(), "%d", scoreEvent.score));
                    }
                    else {
                        scorePlayer2.setText(String.format(Locale.getDefault(), "%d", scoreEvent.score));
                    }
                }
                /*
                 * Show final credits
                 */
                else if (event instanceof GameEndEvent) {
                    Bundle args = getArguments();

                    if (args == null)
                        args = new Bundle();

                    int player1Score = game.getBoard().getScore(Game.Player.PLAYER1);
                    int player2Score = game.getBoard().getScore(Game.Player.PLAYER2);

                    args.putInt(ARG_PLAYER1_SCORE, player1Score);
                    args.putInt(ARG_PLAYER2_SCORE, player2Score);
                    args.putSerializable(ARG_GAME_MODE, mode);

                    // Analytics Game duration
                    long gameDurationMillis = getContext().getSharedPreferences(Constants.PREFS_NAME_ANALYTICS, Context.MODE_PRIVATE)
                                                          .getLong(Constants.GAME_DURATION_START, System.currentTimeMillis()) - System.currentTimeMillis();

                    Tracker t = ((App) getActivity().getApplication()).getTracker(App.TrackerName.APP_TRACKER);
                    t.send(new HitBuilders.TimingBuilder()
                                           .setCategory(getString(R.string.game_stats_category))
                                           .setLabel(getString(R.string.game_duration_label))
                                           .setVariable(getString(R.string.game_duration_label))
                                           .setValue(gameDurationMillis)
                                           .build());

                    // clear the timestamp for game start
                    getContext().getSharedPreferences(Constants.PREFS_NAME_ANALYTICS, Context.MODE_PRIVATE).edit().remove(Constants.GAME_DURATION_START).apply();

                    // Analytics Play stats
                    String difficulty = getString(R.string.three_by_three);
                    switch (game.getBoard().getRows()) {
                        case 3:
                            difficulty = getString(R.string.three_by_three);
                            break;

                        case 4:
                            difficulty = getString(R.string.four_by_four);
                            break;

                        case 5:
                            difficulty = getString(R.string.five_by_five);
                            break;
                    }

                    t.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.game_stats_category))
                            .setLabel(getString(R.string.game_mode_label))
                            .setAction(String.format(Locale.ENGLISH, getString(R.string.game_mode_template), difficulty, mode.toString(), player1Score, player2Score))
                            .build());

                    // Insert record in database
                    GameScore gameScore = new GameScore();
                    gameScore.setDate(new Date(System.currentTimeMillis()));
                    gameScore.setMode(difficulty);
                    gameScore.setOpponent(getString(R.string.versus) + " " + mode.toString());
                    String scoreString = (player1Score > player2Score ? getString(R.string.win) : player1Score == player2Score ? getString(R.string.tie) : getString(R.string.lost)) +
                            " " +
                            player1Score +
                            ":" +
                            player2Score;
                    gameScore.setScore(scoreString);
                    ((App) getActivity().getApplication()).getDaoSession().getGameScoreDao().insert(gameScore);


                    if (mListener != null)
                        mListener.onWinFragmentLoad(ResultsFragment.FRAGMENT_ID, args);
                }
                else if (event instanceof EmitSoundEvent) {
                    if (mListener != null) {
                        mListener.onSoundRequested();
                    }
                }
                else if (event instanceof SquareCompletedEvent) {
                    if (shouldVibrate) {
                        vibrator.vibrate(getResources().getInteger(R.integer.vibrate_duration));
                    }
                }
            }
        });
    }

    private void takeTurnFromBot() {
        boardView.disableInteraction();
        progressBar.setProgress(100);
        progressTimer.start();

        // Offload to an async calculation on a separate Rx Thread
        Observable.fromCallable(new Callable<Edge>() {
                                    @Override
                                    public Edge call() throws Exception {
                                        return bot.getNextMove();
                                    }
                                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .delay(BOT_DELAY_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Edge>() {
                    @Override
                    public void call(Edge edge) {
                        Log.d(TAG, "onPostExecute: " + edge.getKey());
                        RxBus.getInstance().send(new BotComputeEvent(edge));
                    }
                });
    }

    public void setTurnText(Game.Player player) {
        Spannable turnString;
        String playerName;
        if (player == Game.Player.PLAYER1) {
            playerName = getString(R.string.player1TurnName);
            turnString = new SpannableString(String.format(Locale.getDefault(), getString(R.string.turn_text), playerName));
            turnString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.boxPlayer1)), 0, turnString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        else {
            playerName = getString(R.string.player2TurnName);

            turnString = new SpannableString(String.format(Locale.getDefault(), getString(R.string.turn_text), playerName));
            turnString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.boxPlayer2)), 0, turnString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        turnText.setText(turnString);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int rows = getResources().getInteger(R.integer.default_rows);
        int columns = getResources().getInteger(R.integer.default_columns);

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_MODE)) {
                mode = (Game.Mode) getArguments().getSerializable(ARG_MODE);
            }
            else {
                mode = Game.Mode.PLAYER;
            }

            rows = args.getInt("rows", rows);
            columns = args.getInt("columns", columns);
        }
        else {
            mode = Game.Mode.CPU;
        }

        game = new Game(rows, columns);
        bot = new PlayerBot(game);
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        shouldVibrate = sharedPref.getBoolean(getContext().getString(R.string.pref_key_vibrate), true);

        // RxBus
        initBusSubscription();

        // Analytics
        Tracker t = ((App) getActivity().getApplication()).getTracker(App.TrackerName.APP_TRACKER);
        t.setScreenName(getString(R.string.screen_name_game));
        t.send(new HitBuilders.ScreenViewBuilder().build());

        getContext().getSharedPreferences(Constants.PREFS_NAME_ANALYTICS, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(Constants.GAME_DURATION_START, System.currentTimeMillis())
                    .apply();
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game_local, container, false);

        boardView = (BoardView) root.findViewById(R.id.boardView);
        boardView.setGame(game);

        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);

        scorePlayer1 = (TextView) root.findViewById(R.id.player1_score);
        scorePlayer1.setTypeface(Globals.kgTrueColors);
        scorePlayer2 = (TextView) root.findViewById(R.id.player2_score);
        scorePlayer2.setTypeface(Globals.kgTrueColors);

        ImageView imagePlayer1Border = (ImageView) root.findViewById(R.id.player1_border);
        ImageView imagePlayer2Border = (ImageView) root.findViewById(R.id.player2_border);

        // set transition drawable to player 1 border
        transitionDrawablePlayer1 = new TransitionDrawable(new Drawable[]{
                getResources().getDrawable(R.drawable.bg_player1_image_active),
                getResources().getDrawable(R.drawable.bg_player_image_inactive)
        });
        imagePlayer1Border.setImageDrawable(transitionDrawablePlayer1);

        // set transition drawable to player 2 border
        transitionDrawablePlayer2 = new TransitionDrawable(new Drawable[]{
                getResources().getDrawable(R.drawable.bg_player_image_inactive),
                getResources().getDrawable(R.drawable.bg_player2_image_active)
        });
        imagePlayer2Border.setImageDrawable(transitionDrawablePlayer2);

        turnText = (TextView) root.findViewById(R.id.turnText);
        turnText.setTypeface(Globals.kgTrueColors);
        setTurnText(Game.Player.PLAYER1);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ARG_BOARD))
                game.getBoard().loadBoard(savedInstanceState.getString(ARG_BOARD));

            if (savedInstanceState.containsKey(ARG_P1_NEXT)) {
                Game.Player nextPlayer = savedInstanceState.getBoolean(ARG_P1_NEXT) ? Game.Player.PLAYER1 : Game.Player.PLAYER2;
                game.setNextPlayer(nextPlayer);
            }
        }

        boardView.enableInteraction();
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
        subscription.unsubscribe();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public interface OnFragmentInteractionListener {
        void onWinFragmentLoad(int fragmentId, Bundle args);
        void onSoundRequested();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_BOARD, game.getBoard().toString());
        outState.putBoolean(ARG_P1_NEXT, game.getState() == Game.State.PLAYER1_TURN);
        super.onSaveInstanceState(outState);
    }
}