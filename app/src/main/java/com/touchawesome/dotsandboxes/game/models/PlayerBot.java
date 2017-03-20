package com.touchawesome.dotsandboxes.game.models;

import android.util.Log;

import com.touchawesome.dotsandboxes.game.controllers.Game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


import static android.content.ContentValues.TAG;

/**
 * Created by scelus on 15.03.17
 */
public class PlayerBot {

    private Game.Player ID = Game.Player.PLAYER2;
    private Graph gameTree;

    public PlayerBot () {
        gameTree = new Graph();
    }

    public Edge getNextMove(Game game) {
        // get a reference to the game tree
        gameTree = game.getGameTree();

        // get a copy of the game board
        Board board = game.getBoard();
        Board miniMaxBoard = new Board(board.getRows(), board.getColumns());
        miniMaxBoard.loadBoard(board.toString());

        // pre-generate the moves
        ArrayList<Edge> availableMoves = getAvailableMoves(gameTree, board.getRows(), board.getColumns());
        // assume the first to be the next one
        Edge next = availableMoves.get(0);
        // worst case scenario for the outcome
        int value = Integer.MIN_VALUE;
        // try every move and see which one is best
        for (Edge move : availableMoves) {
            int successorValue = minimax(0, miniMaxBoard, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (successorValue > value) {
                next = move;
                value = successorValue;
            }
        }

        return next;
    }

    /**
     * Serves as an utility function for the minimax algorithm
     * @param board the board which represents the current game state.
     * @return The integer value which is the maximum score that <b>Player 2</b> can make
     */
    private int getValue(Board board) {
        int value = board.getScore(Game.Player.PLAYER2) - board.getScore(Game.Player.PLAYER1);
        Log.d(TAG, "getValue: " + value);
        return value;
    }

    private int minimax(int level, Board board, boolean isMaximizer, int alpha, int beta) {
        // pre-generate the moves
        ArrayList<Edge> availableMoves = getAvailableMoves(gameTree, board.getRows(), board.getColumns());

        // don't go too deep into recursion
        if (level >= 5 || availableMoves.size() == 0) {
            return getValue(board);
        }

        int result = isMaximizer ? alpha : beta;
        for (Edge nextMove : availableMoves) {
            Log.d(TAG, "minimax: nextmove: " + nextMove.getKey());
            // make a move to continue with the depth first search
            if (isMaximizer) { // maximizer

                // prefer the moves that will complete boxes
                gameTree.addEdge(nextMove);
                board.setLineForDots(nextMove.getDotStart(), nextMove.getDotEnd(), ID);
                int successorValue = minimax(level + 1, board, false, alpha, beta);
                board.removeLineForDots(nextMove.dot_start, nextMove.dot_end);
                gameTree.removeEdge(nextMove);

                result = Math.max(result, successorValue);
                if (result >= beta)
                    return result;

                alpha = Math.max(alpha, result);
            }
            else { // minimizer
                // prefer the moves that will complete boxes
                gameTree.addEdge(nextMove);
                board.setLineForDots(nextMove.getDotStart(), nextMove.getDotEnd(), ID);
                int successorValue = minimax(level + 1, board, true, alpha, beta);
                board.removeLineForDots(nextMove.dot_start, nextMove.dot_end);
                gameTree.removeEdge(nextMove);

                result = Math.min(result, successorValue);
                if (result <= alpha)
                    return result;

                beta = Math.min(beta, result);
            }
        }

        return result;
    }

    /**
     * Gets all available moves for the minimizers and maximizers
     * @return available moves
     */

    private ArrayList<Edge> getAvailableMoves(Graph gameTree, int boardRows, int boardColumns) {
        ArrayList<Edge> availableMoves = new ArrayList<>();

        int rows = boardRows + 1;
        int columns = boardColumns + 1;
        int allDotsCount = rows * columns;

        for (int i = 0; i < allDotsCount; i++) {
            // horizontal
            if (i % columns != columns - 1) {
                Edge edge = new Edge(i, i + 1);
                if (!gameTree.hasEdge(edge.getDotStart(), edge.getDotEnd()))
                    availableMoves.add(edge);
            }

            // vertical
            if (i / rows != rows - 1) {
                Edge edge = new Edge(i, i + columns);
                if (!gameTree.hasEdge(edge.getDotStart(), edge.getDotEnd()))
                    availableMoves.add(edge);
            }
        }

        Collections.shuffle(availableMoves);
        return availableMoves;
    }

    /**
     * The method searches by a greedy algorithm for a box with a fourth wall missing.
     * It will be right before completion.
     * @param board
     * @return first found Edge that will complete a box
     */
    private Edge getCompletionMove(Board board) {
        Edge completionEdge = null;
        int dots_rows = board.getRows();
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                Board.Box box = board.getBoxAt(i, j);

                // left missing
                if (box.bottom & box.top & box.right) {
                    completionEdge = new Edge(i * dots_rows + j, (i + 1) * dots_rows + j);
                    break;
                }

                // right missing
                if (box.bottom & box.top & box.left) {
                    completionEdge = new Edge(i * dots_rows + j + 1, (i + 1) * dots_rows + j + 1);
                    break;
                }

                // top missing
                if (box.bottom & box.left & box.right) {
                    completionEdge = new Edge(i * dots_rows + j, i * dots_rows + j + 1);
                    break;
                }

                // bottom missing
                if (box.left & box.top & box.right) {
                    completionEdge = new Edge((i + 1) * dots_rows + j, (i + 1) * dots_rows + j + 1);
                    break;
                }
            }
        }
        return completionEdge;
    }
}
