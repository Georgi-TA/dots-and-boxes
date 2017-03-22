package com.touchawesome.dotsandboxes.game.controllers;

import java.util.HashSet;

import com.touchawesome.dotsandboxes.game.models.Board;
import com.touchawesome.dotsandboxes.game.models.Edge;
import com.touchawesome.dotsandboxes.game.models.Graph;

/**
 * Game class object responsible for handling the game as it develops.
 * It interfaces the game state and keeps track of the board, notifies the game listeners
 * and indicated when a turn has changed.
 */
public class Game {
    private Graph gameTree;    // graph object representing the game tree
    private Board board;       // the board on which the two players will play

    public void setNextPlayer(Player nextPlayer) {
        switch (nextPlayer) {
            case PLAYER1:
                gameState = State.PLAYER1_TURN;
                break;
            case PLAYER2:
                gameState = State.PLAYER2_TURN;
                break;
        }
    }

    public Graph getGameTree() {
        Graph graph = new Graph();
        for (Edge edge : gameTree.getEdges().values())
            graph.addEdge(edge);
        return graph;
    }

    public void takeATurn(int numberDotStart, int numberDotEnd) {
        switch (gameState) {
            case START:
                makeAMove(numberDotStart, numberDotEnd, Player.PLAYER1);
                break;
            case PLAYER1_TURN:
                makeAMove(numberDotStart, numberDotEnd, Player.PLAYER1);
                break;
            case PLAYER2_TURN:
                makeAMove(numberDotStart, numberDotEnd, Player.PLAYER2);
                break;
        }
    }

    /**
     * Enumeration for the game state. Could be any one of the described below.
     */
    private enum State {
        START, PLAYER1_TURN, PLAYER2_TURN, END
    }

    public State getGameState() {
        return gameState;
    }

    private State gameState = State.START;

    /**
     * Enumeration of the players, who own a square. Also used for turn based decisions.
     */
    public enum Player {
        PLAYER1, NONE, PLAYER2;
    }
    private Player nextToMove;  // the player that is next to move

    /**
     * Enumeration of the Mode which the user has selected to play as.
     */
    public enum Mode {
        PLAYER, CPU, NETWORK;
    }
    private Mode gameMode = Mode.PLAYER;

    /**
     * Listener interface for the {@link Game} class
     */
    public interface GameListener {
        void onScoreChange(Player player, int score);
        void onTurnChange(Player nextToMove);
        void onGameEnd(Player winner);
    }

    private HashSet<GameListener> listeners; // all listeners registered to monitor the game progress

    private int maxScore;       // pre-calculated field for keeping track of the current MAX Score

    /**
     * Basic default constructor setting initial values to zero
     * and placing PLAYER1 as the next to move.
     * @param rows the rows of the board
     * @param columns the columns of the board
     */
    public Game (int rows, int columns) {
        this.maxScore = rows * columns;

        this.board = new Board(rows, columns);
        this.gameTree = new Graph();
        this.listeners = new HashSet<>();

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
     * Connects two dots on the board, which counts as a move.
     * This method keeps track of the current state of the game.
     * @param dotStart starting point
     * @param dotEnd ending point
     */
    public boolean makeAMove(int dotStart, int dotEnd, Player player) {
        // you cannot make a move on an existing line
        if (gameTree.hasEdge(dotStart, dotEnd))
            return false;

        gameTree.addEdge(new Edge(dotStart, dotEnd));

        switch (gameState) {
            case START:
                takeTurnPlayer1(dotStart, dotEnd);
                return true;

            case PLAYER1_TURN:
                takeTurnPlayer1(dotStart, dotEnd);
                return true;

            case PLAYER2_TURN:
                takeTurnPlayer2(dotStart, dotEnd);
                break;

            case END:

                return false;
        }

        return false;
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
     * A move will be executed with Player1 being active
     */
    private void takeTurnPlayer1(int dotStart, int dotEnd) {
        // register the move on the board for the first player
        boolean boxCompleted = board.setLineForDots(dotStart, dotEnd, Player.PLAYER1);

        if (boxCompleted) {
            // calculate the score
            int player1Score = board.getScore(Player.PLAYER1);
            int player2Score = board.getScore(Player.PLAYER2);
            notifyScoreChange(Player.PLAYER1, player1Score);

            // if the board is filled
            if (player1Score + player2Score == maxScore) {
                if (player1Score == player2Score) {
                    notifyGameEnd(Player.NONE);
                }
                else if (player1Score > player2Score) {
                    notifyGameEnd(Player.PLAYER1);
                }
                else {
                    notifyGameEnd(Player.PLAYER2);
                }

                gameState = State.END;
            }
        }
        else {
            gameState = State.PLAYER2_TURN;
            notifyTurnChange(Player.PLAYER2);
        }
    }

    /**
     * A move will be executed with Player2 being active
     */
    private void takeTurnPlayer2(int dotStart, int dotEnd) {
        // register the move on the board for the first player
        boolean boxCompleted = board.setLineForDots(dotStart, dotEnd, Player.PLAYER2);

        if (boxCompleted) {
            // calculate the score
            int player1Score = board.getScore(Player.PLAYER1);
            int player2Score = board.getScore(Player.PLAYER2);
            notifyScoreChange(Player.PLAYER2, player2Score);

            // if the board is filled
            if (player1Score + player2Score == maxScore) {
                if (player1Score == player2Score) {
                    notifyGameEnd(Player.NONE);
                }
                else if (player1Score > player2Score) {
                    notifyGameEnd(Player.PLAYER1);
                }
                else {
                    notifyGameEnd(Player.PLAYER2);
                }

                gameState = State.END;
            }
        }
        else {
            gameState = State.PLAYER1_TURN;
            notifyTurnChange(Player.PLAYER1);
        }
    }

    /**
     * Notifies the listeners for a turn change
     * @param nextPlayer the player who is going the take the next turn
     */
    private void notifyTurnChange(Player nextPlayer) {
        for (GameListener listener : listeners)
            if (listener != null)
                listener.onTurnChange(nextPlayer);
    }

    /**
     * Notifies the listeners for a score change
     * @param player the player who's score is changing
     * @param score the player's score
     */
    private void notifyScoreChange(Player player, int score) {
        for (GameListener listener : listeners)
            if (listener != null)
                listener.onScoreChange(player, score);
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
