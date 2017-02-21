package info.scelus.dotsandboxes.external;

import java.util.HashSet;

/**
 * Created by SceLus on 15/10/2014.
 */
public class Game {

    public interface GameListener {
        void onScoreChange(int p1Score, int p2Score);
        void onTurnChange(Player nextToMove);
    }
    private HashSet<GameListener> listeners;

    private int player1Score;
    private int player2Score;
    private Player nextToMove;
    public enum Player {
        PLAYER1, PLAYER2
    }

    public enum Mode {
        PLAYER, CPU, NETWORK
    }

    public Game () {
        listeners = new HashSet<GameListener>();
        player1Score = 0;
        player2Score = 0;

        nextToMove = Player.PLAYER1;
    }

    public void registerListener (GameListener listener) {
        this.listeners.add(listener);
    }

    public Player getNext () {
        return nextToMove;
    }

    public void setScore(int p1Score, int p2Score) {

        if (p1Score == player1Score && p2Score == player2Score) {
            if (nextToMove == Player.PLAYER1) {
                nextToMove = Player.PLAYER2;
                notifyTurnChange();
            }
            else {
                nextToMove = Player.PLAYER1;
                notifyTurnChange();
            }
        }
        else {
            if (p1Score > player1Score && nextToMove == Player.PLAYER2) {
                nextToMove = Player.PLAYER1;
                notifyTurnChange();
            } else if (p2Score > player2Score && nextToMove == Player.PLAYER1) {
                nextToMove = Player.PLAYER2;
                notifyTurnChange();
            }
        }
        player1Score = p1Score;
        player2Score = p2Score;
        notifyScoreChange();
    }

    private void notifyTurnChange() {
        for (GameListener listener : listeners)
            if (listener != null)
                listener.onTurnChange(nextToMove);
    }

    private void notifyScoreChange() {
        for (GameListener listener : listeners)
            if (listener != null)
                listener.onScoreChange(player1Score, player2Score);
    }
}
