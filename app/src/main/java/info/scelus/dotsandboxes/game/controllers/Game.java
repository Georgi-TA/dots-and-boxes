package info.scelus.dotsandboxes.game.controllers;

import android.util.Log;
import java.util.HashSet;
import info.scelus.dotsandboxes.game.models.Board;
import info.scelus.dotsandboxes.game.models.Graph;

import static android.content.ContentValues.TAG;

/**
 * Game class object responsible for handling the game as it develops.
 * It interfaces the game state and keeps track of the board, notifies the game listeners
 * and indicated when a turn has changed.
 */
public class Game {
    private Graph gameTree;    // graph object representing the game tree
    private Board board;       // the board on which the two players will play

    /**
     * Listener interface for the {@link Game} class
     */
    public interface GameListener {
        void onScoreChange(int p1Score, int p2Score);
        void onTurnChange(Player nextToMove);
        void onGameEnd(Player winner);
    }

    private HashSet<GameListener> listeners; // all listeners registered to monitor the game progress

    private int maxScore;       // pre-calculated field for keeping track of the current MAX Score
    private int player1Score;   // player one's score
    private int player2Score;   // player two's score


    private Player nextToMove;  // the player that is next to move
    /**
     * Enumeration of the players, who own a square. Also used for turn based decisions.
     */
    public enum Player {
        PLAYER1, NONE, PLAYER2;


    }
    /**
     * Enumeration of the Mode which the user has selected to play as.
     */
    public enum Mode {
        PLAYER, CPU, NETWORK;
    }

    /**
     * Basic default constructor setting initial values to zero
     * and placing PLAYER1 as the next to move.
     * @param rows the rows of the board
     * @param columns the columns of the board
     */
    public Game (int rows, int columns) {
        this.maxScore = rows * columns;

        this.board = new Board(rows, columns);
        this.gameTree = new Graph(rows, columns);
        this.listeners = new HashSet<>();

        this.player1Score = 0;
        this.player2Score = 0;
        this.nextToMove = Player.PLAYER1;
    }

    /**
     * Add a listener to the list of listeners
     * @param listener the listener to be added
     */
    public void registerListener (GameListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Connects two dots on the board, which counts as a move
     * @param dotStart starting point
     * @param dotEnd ending point
     */
    public void makeAMove(int dotStart, int dotEnd) {

        // register the move on the board
        board.setLineForDots(dotStart, dotEnd, this.nextToMove);

        // add the current move to the Graph object
        gameTree.addEdge(dotStart, dotEnd);

        int player1Score = board.getScore(Player.PLAYER1);
        int player2Score = board.getScore(Player.PLAYER2);
        // the case where player 1 reached the minimum points required to win
        if (player1Score > maxScore / 2 + maxScore % 2) {
            notifyGameEnd(Player.PLAYER1);
        }
        // the case where player 2 reached the minimum points required to win
        else if (player2Score > maxScore / 2 + maxScore % 2) {
            notifyGameEnd(Player.PLAYER2);
        }
        // the case where player one and player two have equal points
        if (this.player1Score == player1Score && this.player2Score == player2Score) {
            // switch the next player to move
            if (nextToMove == Player.PLAYER1) {
                nextToMove = Player.PLAYER2;

                Log.d(TAG, "makeAMove: " + gameTree.getNextMove(Player.PLAYER2));
                notifyTurnChange();
            }
            else {
                nextToMove = Player.PLAYER1;
                notifyTurnChange();
            }
        }
        // otherwise one of the players made a square and has an extra turn
        else {
            if (player1Score > this.player1Score && nextToMove == Player.PLAYER2) {
                nextToMove = Player.PLAYER1;
                notifyTurnChange();
            }
            else if (player2Score > this.player2Score && nextToMove == Player.PLAYER1) {
                nextToMove = Player.PLAYER2;

                Log.d(TAG, "makeAMove: " + gameTree.getNextMove(Player.PLAYER2));
                notifyTurnChange();
            }

            this.player1Score = player1Score;
            this.player2Score = player2Score;
            notifyScoreChange();
        }
    }

    /**
     * The getter for the board object
     * @return the current board on which the game is played
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Getter fot the next player to move
     * @return the player that is next to move
     */
    public Player getNext () {
        return nextToMove;
    }

    /**
     * Sets the initial turn and recalculated the score of each player
     * @param firstToMove the player to make the first move
     */
    public void setInitialTurn(Player firstToMove) {
        this.nextToMove = firstToMove;
        this.player1Score = getBoard().getScore(Player.PLAYER1);
        this.player2Score = getBoard().getScore(Player.PLAYER2);
    }

    /**
     * Notifies the listeners for a turn change
     */
    private void notifyTurnChange() {
        for (GameListener listener : listeners)
            if (listener != null)
                listener.onTurnChange(nextToMove);
    }

    /**
     * Notifies the listeners for a score change
     */
    private void notifyScoreChange() {
        for (GameListener listener : listeners)
            if (listener != null)
                listener.onScoreChange(player1Score, player2Score);
    }

    /**
     * Notify the end of the game
     * @param winner the winner of the game
     */
    private void notifyGameEnd(Player winner) {
        for (GameListener listener : listeners)
            if (listener != null)
                listener.onGameEnd(winner);
    }
}
