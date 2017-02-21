package info.scelus.dotsandboxes.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import info.blackbear.scelus.dotsandboxes.R;
import info.scelus.dotsandboxes.external.Game;

/**
 * Created by SceLus on 15/10/2014.
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

        onScoreChange(0, 0);
        onTurnChange(Game.Player.PLAYER1);
    }

    @Override
    public void onScoreChange(int p1Score, int p2Score) {
        player1.setText(Integer.toString(p1Score));
        player2.setText(Integer.toString(p2Score));
    }

    @Override
    public void onTurnChange(Game.Player nextToMove) {
        if (nextToMove == Game.Player.PLAYER1) {
            player1.setBackgroundResource(R.color.boxPlayer1);
            player2.setBackgroundResource(android.R.color.transparent);
        }
        else {
            player1.setBackgroundResource(android.R.color.transparent);
            player2.setBackgroundResource(R.color.boxPlayer2);
        }
     }
}
