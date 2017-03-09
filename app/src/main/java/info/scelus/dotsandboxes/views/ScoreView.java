package info.scelus.dotsandboxes.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import info.blackbear.scelus.dotsandboxes.R;
import info.scelus.dotsandboxes.game.controllers.Game;

/**
 * Created by SceLus on 15/10/2014
 */
public class ScoreView extends LinearLayout implements Game.GameListener {
    private TextView player1;
    private TextView player2;

    public ScoreView(Context context) {
        super(context);
        init();
    }

    public ScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScoreView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        player1 = (TextView) findViewById(R.id.scorePlayer1);
        player2 = (TextView) findViewById(R.id.scorePlayer2);
    }

    @Override
    public void onScoreChange(int p1Score, int p2Score) {
        player1.setText(String.format(Locale.getDefault(), "%d", p1Score));
        player2.setText(String.format(Locale.getDefault(), "%d", p2Score));
    }

    @Override
    public void onTurnChange(Game.Player nextToMove) {
        player1.setSelected(nextToMove == Game.Player.PLAYER1);
        player2.setSelected(nextToMove == Game.Player.PLAYER2);
     }

    @Override
    public void onGameEnd(Game.Player winner) {
        if (winner == Game.Player.PLAYER1) {
            player1.setBackgroundResource(R.drawable.bg_score_winner);
        }
        else {
            player2.setBackgroundResource(R.drawable.bg_score_winner);
        }
    }
}
