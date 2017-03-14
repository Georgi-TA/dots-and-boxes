package info.scelus.dotsandboxes.game.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import info.scelus.dotsandboxes.game.controllers.Game;

import static android.content.ContentValues.TAG;

/**
 * A class representing the game tree in form of a graph
 * the {@link info.scelus.dotsandboxes.game.models.Board} board is presented
 *
 * Created by scelus on 08.03.17
 */

public class Graph {
    private HashMap<String, Edge> edges;
    private Board board;

    /**
     * Default constructor
     *
     * @param rows    rows of the board
     * @param columns columns of the board
     */
    public Graph(int rows, int columns) {
        this.edges = new HashMap<>();
        this.board = new Board(rows, columns);
    }

    /**
     * Adds an edge to the edges' HashMap.
     *
     * @param dotStart the first dotOf the line
     * @param dotEnd   the last dorOf the line
     */
    public void addEdge(int dotStart, int dotEnd) {
        Edge edge = new Edge(dotStart, dotEnd);
        this.edges.put(edge.getKey(), edge);
    }

    /**
     * Calculates the next move based on the minimax algorithm combined with alpha-beta pruning.
     *
     * @param player the player to make the move
     */
    public Edge getNextMove(Game.Player player) {
        Board miniMaxBoard = new Board(board.getRows(), board.getColumns());
        miniMaxBoard.loadBoard(board.toString());

        MiniMaxNode result = minimax(0, miniMaxBoard, null, player, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Log.d(TAG, "getNextMove: " + result.value);
        return result.edge;
    }

    private MiniMaxNode minimax(int level, Board board, Edge edge, Game.Player player, int alpha, int beta) {
        ArrayList<Edge> availableMoves = getAvailableMoves(board);
        if (level >= 30 || availableMoves.size() == 0) {
            return new MiniMaxNode(edge, getValue(board));
        }

        Edge move = getCompletionMove(board);
        while (move != null) {
            addEdge(move);
            board.setLineForDots(move.dot_start, move.dot_end, player);
            move = getCompletionMove(board);
        }

        for (Edge nextMove : availableMoves) {
            Log.d(TAG, "minimax: nextmove: " + nextMove.getKey());
            // make a move to continue with the depth first search
            if (player == Game.Player.PLAYER1) { // minimizer

                MiniMaxNode successor = minimax(level + 1, board, nextMove, Game.Player.PLAYER2, alpha, beta);
                if (alpha > successor.value) {
                    alpha = successor.value;
                }

                if (alpha <= beta)
                    return successor;

                // undo move
                // revert the move to continue with the next one
                board.removeLineForDots(nextMove.dot_start, nextMove.dot_end);
                removeEdge(nextMove);

            }
            else if(player == Game.Player.PLAYER2) { // maximizer

                MiniMaxNode successor = minimax(level + 1, board, nextMove, Game.Player.PLAYER1, alpha, beta);
                if (beta < successor.value) {
                    beta = successor.value;
                }

                if (alpha <= beta)
                    return successor;

                // undo move
                // revert the move to continue with the next one
                board.removeLineForDots(nextMove.dot_start, nextMove.dot_end);
                removeEdge(nextMove);
            }
        }

        return new MiniMaxNode(edge, getValue(board));
    }

    /**
     * Wrapper class for the minimax algorithm
     */
    private class MiniMaxNode {
        int value;
        Edge edge;

        MiniMaxNode(Edge edge, int value) {
            this.value = value;
            this.edge = edge;
        }
    }

    /**
     * Add an edge to the game tree
     * @param edge the edge to be added
     */
    private void addEdge(Edge edge) {
        edges.put(edge.getKey(), edge);
    }

    /**
     * Remove an edge from the game tree
     * @param edge the edge to be removed
     */
    private void removeEdge(Edge edge) {
        edges.remove(edge.getKey());
    }

    /**
     * Serves as an utitlity function for the minimax algortihm
     * @param board the board which represents the current game state.
     * @return The integer value which is the maximum score that <b>Player 2</b> can make
     */
    private int getValue(Board board) {
        return board.getScore(Game.Player.PLAYER2) - board.getScore(Game.Player.PLAYER1);
    }

    /**
     * Gets all available moves for the minimizers and maximizers
     * @return available moves
     */
    private ArrayList<Edge> getAvailableMoves(Board board) {
        ArrayList<Edge> availableMoves = new ArrayList<>();

        int rows = board.getRows() + 1;
        int columns = board.getColumns() + 1;
        int allDotsCount = rows * columns;

        for (int i = 0; i < allDotsCount; i++) {
            // horizontal
            if (i % columns != columns - 1) {
                Edge edge = new Edge(i, i + 1);
                if (!edges.containsKey(edge.getKey()))
                    availableMoves.add(edge);
            }

            // vertical
            if (i / rows != rows - 1) {
                Edge edge = new Edge(i, i + columns);
                if (!edges.containsKey(edge.getKey()))
                    availableMoves.add(edge);
            }
        }
        return availableMoves;
    }

    private Edge getCompletionMove(Board board) {
        Edge completionEdge = null;

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                Board.Box box = board.getBoxAt(i, j);

                // left missing
                if (box.bottom & box.top & box.right) {
                    completionEdge = new Edge(i, i + board.getColumns() + 1);
                    break;
                }

                // right missing
                if (box.bottom & box.top & box.left) {
                    completionEdge = new Edge(i + 1, i + board.getColumns() + 2);
                    break;
                }

                // top missing
                if (box.bottom & box.left & box.right) {
                    completionEdge = new Edge(i, i + 1);
                    break;
                }

                // bottom missing
                if (box.left & box.top & box.right) {
                    completionEdge = new Edge(i + board.getColumns() + 1, i + board.getColumns() + 2);
                    break;
                }
            }
        }

        return completionEdge;
    }

    public boolean hasEdge(int dotStart, int dotEnd) {
        return edges.containsKey(Edge.generateKey(dotStart, dotEnd));
    }
}
