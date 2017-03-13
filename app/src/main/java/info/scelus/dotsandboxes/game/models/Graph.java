package info.scelus.dotsandboxes.game.models;

import java.util.ArrayList;
import java.util.HashMap;

import info.scelus.dotsandboxes.game.controllers.Game;

/**
 * A class representing the game tree in form of a graph
 * the {@link info.scelus.dotsandboxes.game.models.Board} board is presented
 *
 * Created by scelus on 08.03.17
 */

public class Graph {

    private static final int MAX_LEVEL_MINIMAX = 2;
    private int MAX_SCORE;
    private HashMap<String, Edge> edges;
    private Board board;

    /**
     * Default constructor
     * @param rows rows of the board
     * @param columns columns of the board
     */
    public Graph(int rows, int columns) {
        this.edges = new HashMap<>();
        this.board = new Board(rows, columns);
        this.MAX_SCORE = rows * columns;
    }

    /**
     * Adds an edge to the edges' HashMap.
     * @param dotStart the first dotOf the line
     * @param dotEnd the last dorOf the line
     */
    public void addEdge(int dotStart, int dotEnd) {
        Edge edge = new Edge(dotStart, dotEnd);
        this.edges.put(edge.getKey(), edge);
    }

    /**
     * Calculates the next move based on the minimax algorithm combined with alpha-beta pruning.
     * @param player the player to make the move
     */
    public Edge getNextMove(Game.Player player) {
        // Validation if player 2 is on the move
        // He will be either an AI in this case
        if (player == Game.Player.PLAYER2) {
            return alphaBeta(board);
        }

        return null;
    }

    /**
     * Entry point for the alpha beta algorithm
     * @param board the board which is the current game board
     * @return the Edge that will represent the next move for the AI
     */
    private Edge alphaBeta(Board board) {
        // create a copy of the current board
        Board minimaxBoard = new Board(board.getRows(), board.getColumns());
        minimaxBoard.loadBoard(board.toString());

        // assume the next value to be from a maximizer
        return maxValue(0, board, Integer.MIN_VALUE, Integer.MAX_VALUE).edge;
    }

    /**
     * Wrapper class for the minimax algorithm
     */
    private class MiniMaxNode {
        int value;
        Edge edge;
    }

    /**
     * Servers as a maximizer function for the minimax algorithm
     * @param board the board on which to make the next move with a minimizer
     * @param alpha the alpha value
     * @param beta  the beta value
     * @return the best fitted move for <b>Player 2</b>
     */
    private MiniMaxNode maxValue(int level, Board board, int alpha, int beta) {
        if (board.isFinished() || level >= MAX_LEVEL_MINIMAX) {
            MiniMaxNode result = new MiniMaxNode();
            result.value = getValue(board);
            return result;
        }

        MiniMaxNode result = new MiniMaxNode();
        result.value = Integer.MIN_VALUE;

        for (Edge edge : getAvailableMoves()) {
            // make a move to continue with the depth first search
            board.setLineForDots(edge.dot_start, edge.dot_end, Game.Player.PLAYER2);
            addEdge(edge);

            // evaluate the current board state
            MiniMaxNode node = minValue(level + 1, board, alpha, beta);
            node.edge = edge;
            result.value = Math.max(result.value, node.value);
            if (result.value > beta)
                return node;

            // revert the move to continue with the next one
            board.setLineForDots(edge.dot_start, edge.dot_end, Game.Player.NONE);
            removeEdge(edge);
        }

        return result;
    }

    /**
     * Servers as a minimizer function for the minimax algorithm
     * @param board the board on which to make the next move with a minimizer
     * @param alpha the alpha value
     * @param beta  the beta value
     * @return the best fitted move for <b>Player 1</b>
     */
    private MiniMaxNode minValue(int level, Board board, int alpha, int beta) {
        if (board.isFinished() || level >= MAX_LEVEL_MINIMAX) {
            MiniMaxNode result = new MiniMaxNode();
            result.value = getValue(board);
            return result;
        }

        MiniMaxNode result = new MiniMaxNode();
        result.value = Integer.MIN_VALUE;

        for (Edge edge : getAvailableMoves()) {
            // make a move to continue with the depth first search
            board.setLineForDots(edge.dot_start, edge.dot_end, Game.Player.PLAYER1);
            addEdge(edge);

            // evaluate the current board state
            MiniMaxNode node = maxValue(level + 1, board, alpha, beta);
            node.edge = edge;
            result.value = Math.min(result.value, node.value);
            if (result.value > beta)
                return node;

            // revert the move to continue with the next one
            board.removeLineForDots(edge.dot_start, edge.dot_end);
            removeEdge(edge);
        }

        return result;
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
        return MAX_SCORE - board.getScore(Game.Player.PLAYER1);
    }

    /**
     * Gets all available moves for the minimizers and maximizers
     * @return available moves
     */
    private ArrayList<Edge> getAvailableMoves() {
        ArrayList<Edge> availableMoves = new ArrayList<>();
        int allDotsCount = board.getRows() * board.getColumns();
        int rows = board.getRows();
        int columns = board.getColumns();

        for (int i = 0; i < allDotsCount; i++) {
            // horizontal
            if (i % columns != columns - 1) {
                Edge edge = new Edge(i, i + 1);
                if (!edges.containsKey(edge.getKey()))
                    availableMoves.add(edge);
            }

            // vertical
            if (i % rows != rows - 1) {
                Edge edge = new Edge(i, i + columns);
                if (!edges.containsKey(edge.getKey()))
                    availableMoves.add(edge);
            }
        }
        return availableMoves;
    }

    public boolean hasEdge(int dotStart, int dotEnd) {
        return edges.containsKey(Edge.generateKey(dotStart, dotEnd));
    }
}
