package com.touchawesome.dotsandboxes.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.game.controllers.Game;
import com.touchawesome.dotsandboxes.utils.Globals;
import com.touchawesome.dotsandboxes.views.BoardView;
import com.touchawesome.dotsandboxes.utils.Constants;

public class TutorialActivity extends AppCompatActivity {

    private BoardView boardView;
    private TextView textSection1;
    private LinearLayout section2;
    private Button buttonCompleteTutorial;
    private Game tutorialGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // add the toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textSection1 = (TextView) findViewById(R.id.instructions_part_1);
        boardView = (BoardView) findViewById(R.id.tutorial_board);

        section2 = (LinearLayout) findViewById(R.id.instructions_part_2);
        section2.setAlpha(0);
        section2.setVisibility(View.GONE);

        buttonCompleteTutorial = (Button) findViewById(R.id.buttonCompleteTutorial);
        buttonCompleteTutorial.setTypeface(Globals.kgTrueColors);
        buttonCompleteTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeTutorial();
            }
        });

        tutorialGame = new Game(2, 2);
        tutorialGame.getBoard().loadBoard("31,11,47,14");

        // add horizontal edges
        tutorialGame.getGameTree().addEdge(0, 1);
        tutorialGame.getGameTree().addEdge(1, 2);
        tutorialGame.getGameTree().addEdge(3, 4);
        tutorialGame.getGameTree().addEdge(6, 7);
        tutorialGame.getGameTree().addEdge(7, 8);

        // add vertical edges
        tutorialGame.getGameTree().addEdge(0, 3);
        tutorialGame.getGameTree().addEdge(1, 4);
        tutorialGame.getGameTree().addEdge(2, 5);
        tutorialGame.getGameTree().addEdge(3, 6);
        tutorialGame.getGameTree().addEdge(4, 7);
        tutorialGame.getGameTree().addEdge(5, 8);
        tutorialGame.setNextPlayer(Game.Player.PLAYER1);

        boardView.setGame(tutorialGame);
        boardView.enableInteraction();
        tutorialGame.registerListener(new Game.GameListener() {
            @Override
            public void onScoreChange(Game.Player player, int score) {
                section2.setAlpha(0);
                section2.setVisibility(View.VISIBLE);
                section2.animate().alpha(1).setDuration(getResources().getInteger(R.integer.slide_duration));
            }

            @Override
            public void onTurnChange(Game.Player nextToMove) {

            }

            @Override
            public void onGameEnd(Game.Player winner) {

            }
        });
    }

    private void completeTutorial() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME_GENERAL, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(Constants.TUTORIAL_COMLETE, true).apply();

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
}
