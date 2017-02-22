package info.scelus.dotsandboxes.external;

import java.util.HashSet;

/**
 * Created by SceLus on 15/10/2014
 */
public class Game {

    public interface GameListener {
        void onScoreChange(int p1Score, int p2Score);
        void onTurnChange(Player nextToMove);
        void onGameEnd(Player winner);
    }
    private HashSet<GameListener> listeners;

    private int maxScore;
    private int player1Score;
    private int player2Score;
    private Player nextToMove;
    public enum Player {
        PLAYER1, PLAYER2
    }

    public enum Mode {
        PLAYER, CPU, NETWORK
    }

    public Game (int maxScore) {
        this.listeners = new HashSet<>();
        this.player1Score = 0;
        this.player2Score = 0;
        this.maxScore = maxScore;

        this.nextToMove = Player.PLAYER1;
    }

    public void registerListener (GameListener listener) {
        this.listeners.add(listener);
    }

    public void setInitScoreAndTurn(int p1Score, int p2Score, Player player) {
        this.player1Score = p1Score;
        this.player2Score = p2Score;
        notifyScoreChange();

        this.nextToMove = player;
        notifyTurnChange();
    }

    public void setScore(int p1Score, int p2Score) {

        if (p1Score > maxScore / 2) {
            notifyGameEnd(Player.PLAYER1);
        }
        else if (p2Score > maxScore / 2) {
            notifyGameEnd(Player.PLAYER2);
        }
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

    public Player getNext () {
        return nextToMove;
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

    private void notifyGameEnd(Player winner) {
        for (GameListener listener : listeners)
            if (listener != null)
                listener.onGameEnd(winner);
    }
}
