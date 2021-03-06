package com.touchawesome.dotsandboxes.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.event_bus.RxBus;
import com.touchawesome.dotsandboxes.event_bus.events.PlayerMoveEvent;
import com.touchawesome.dotsandboxes.event_bus.events.ScoreMadeEvent;
import com.touchawesome.dotsandboxes.event_bus.events.SquareCompletedEvent;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.game.models.Edge;
import com.touchawesome.dotsandboxes.utils.Constants;
import com.touchawesome.dotsandboxes.utils.Globals;
import com.touchawesome.dotsandboxes.views.BoardView;

import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class TutorialActivity extends AppCompatActivity {

    private BoardView boardView;
    private Game tutorialGame;
    private BoardView hintBoardView;
    private LinearLayout section2;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // add the toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        section2 = (LinearLayout) findViewById(R.id.instructions_part_2);
        section2.setAlpha(0);
        section2.setVisibility(View.GONE);

        Button buttonCompleteTutorial = (Button) findViewById(R.id.buttonCompleteTutorial);
        buttonCompleteTutorial.setTypeface(Globals.kgTrueColors);
        buttonCompleteTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeTutorial();
            }
        });

        // The board enabled for touch events
        boardView = (BoardView) findViewById(R.id.tutorial_board);
        tutorialGame = new Game(2, 2);
        tutorialGame.getBoard().loadBoard("31,11,47,14");

        // add horizontal edges
        tutorialGame.getGameTree().addEdge(new Edge(0, 1));
        tutorialGame.getGameTree().addEdge(new Edge(1, 2));
        tutorialGame.getGameTree().addEdge(new Edge(3, 4));
        tutorialGame.getGameTree().addEdge(new Edge(6, 7));
        tutorialGame.getGameTree().addEdge(new Edge(7, 8));

        // add vertical edges
        tutorialGame.getGameTree().addEdge(new Edge(0, 3));
        tutorialGame.getGameTree().addEdge(new Edge(1, 4));
        tutorialGame.getGameTree().addEdge(new Edge(2, 5));
        tutorialGame.getGameTree().addEdge(new Edge(3, 6));
        tutorialGame.getGameTree().addEdge(new Edge(4, 7));
        tutorialGame.getGameTree().addEdge(new Edge(5, 8));
        tutorialGame.setNextPlayer(Game.Player.PLAYER1);

        boardView.setGame(tutorialGame);
        boardView.enableInteraction();

        // Hint board and game initialization
        hintBoardView = (BoardView) findViewById(R.id.tutorial_board_hint);
        Game tutorialHintGame = new Game(2, 2);
        tutorialHintGame.getBoard().loadBoard("0,4,0,1");
        hintBoardView.setGame(tutorialHintGame);
        hintBoardView.disableInteraction();
        hintBoardView.setAlpha(0);

        findViewById(R.id.tutorial_screen_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hintBoardView.setAlpha(0);
                hintBoardView.setVisibility(View.VISIBLE);

                hintBoardView.animate().alpha(1).setDuration(getResources().getInteger(R.integer.slide_duration)).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        hintBoardView.animate().alpha(0).setDuration(getResources().getInteger(R.integer.slide_duration));
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                return true;
            }
        });

        initBusSubscription();
    }

    private void initBusSubscription() {
        subscription = RxBus.getInstance().getBus().subscribe(new Action1<Object>() {
            static final String TAG = "Tutorial Subscription";

            @Override
            public void call(Object event) {

                Log.i(TAG, "call: " + event.getClass().toString());
                /*
                 *  Register the event and reflect the score
                 */
                if (event instanceof ScoreMadeEvent) {
                    section2.setAlpha(0);
                    section2.setVisibility(View.VISIBLE);
                    section2.animate().alpha(1).setDuration(getResources().getInteger(R.integer.slide_duration));
                }
                else if (event instanceof PlayerMoveEvent) {
                    PlayerMoveEvent playermoveEvent = (PlayerMoveEvent) event;
                    tutorialGame.makeAMove(playermoveEvent.playerMove.getDotStart(), playermoveEvent.playerMove.getDotEnd());
                    boardView.invalidate();
                }
            }
        });
    }

    private void completeTutorial() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME_GENERAL, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(Constants.TUTORIAL_COMPLETE, true).apply();

        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }
}