package com.touchawesome.dotsandboxes.game.models;

import com.touchawesome.dotsandboxes.game.controllers.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by scelus on 15.03.17
 */
public class PlayerBot {

    private Game.Player ID = Game.Player.PLAYER2;
    private Game game;
    private int maxResult;

    public PlayerBot(Game game) {
        this.game = game;
    }

    public Edge getNextMove() {
        // get a copy of the game board
        Board board = game.getBoard();
        maxResult = board.getColumns() * board.getRows();
        Board miniMaxBoard = new Board(board.getRows(), board.getColumns());
        miniMaxBoard.loadBoard(board.toString());

        ArrayList<Edge> completionMoves = getCompletionMoves();
        Collections.shuffle(completionMoves);
        if (completionMoves.size() > 0)
            return completionMoves.get(0);

        ArrayList<Edge> availableMoves = game.getGameTree().getAvailableEdges();
        Collections.shuffle(availableMoves);
        return availableMoves.get(0);
    }

//    /**
//     * Previous version of the method above
//     */
//        Graph gameTree = game.getGameTree();
//
//        // pre-generate the moves
//        Collection<Edge> availableMoves = gameTree.getAvailableEdges();
//
//        // assume the first to be the next one
//        Edge next = null;
//
//        // worst case scenario for the outcome
//        int value = Integer.MIN_VALUE;
//        int alpha = Integer.MIN_VALUE;
//        int beta = Integer.MAX_VALUE;
//
//        // try every move and see which one is best
//        for (Edge move : availableMoves) {
//            if (next == null)
//                next = move;
//
//            // prefer the moves that will complete boxes
//            gameTree.addEdge(move);
//
//            value = Math.max(value, minimax(0, miniMaxBoard, false, alpha, beta));
//            miniMaxBoard.removeLineForDots(move.getDotStart(), move.getDotEnd());
//            alpha = Math.max(alpha, value);
//
//            if (beta <= alpha) {
//                Log.d(TAG, "getNextMove: " + next.getKey() + "  value: " + value);
//                gameTree.removeEdge(move);
//
//                return move;
//            }
//
//            gameTree.removeEdge(move);
//        }

//
//    /**
//     * Serves as an utility function for the minimax algorithm
//     *
//     * @param board the board which represents the current game state.
//     * @return The integer value which is the maximum score that <b>Player 2</b> can make
//     */
//    private int getValue(Board board) {
//        return maxResult + board.getScore(Game.Player.PLAYER2) - board.getScore(Game.Player.PLAYER1);
//    }

//    /**
//     *  Partially implemented minimax algorithm
//     */
//    private int minimax(int level, Board board, boolean isMaximizer, int alpha, int beta) {
//        Graph gameTree = game.getGameTree();
//
//        // pre-generate the moves
//        Collection<Edge> availableMoves = gameTree.getAvailableEdges();
//
//        // don't go too deep into recursion
//        if (level >= 3 || availableMoves.size() == 0) {
//            return getValue(board);
//        }
//
//        if (isMaximizer) { // maximizer
//            int result = Integer.MIN_VALUE;
//            for (Edge nextMove : availableMoves) {
//                // prefer the moves that will complete boxes
//                gameTree.addEdge(nextMove);
//
//                Board miniMaxBoard = new Board(board.getRows(), board.getColumns());
//                miniMaxBoard.loadBoard(board.toString());
//                miniMaxBoard.setLineForDots(nextMove.getDotStart(), nextMove.getDotEnd(), ID);
//
//                result = Math.max(result, minimax(level + 1, miniMaxBoard, false, alpha, beta));
//                alpha = Math.max(alpha, result);
//
//                if (beta <= alpha) {
//                    gameTree.removeEdge(nextMove);
//                    return alpha;
//                }
//
//                miniMaxBoard.removeLineForDots(nextMove.getDotStart(), nextMove.getDotEnd());
//                gameTree.removeEdge(nextMove);
//            }
//
//            return result;
//        } else { // minimizer
//            int result = Integer.MAX_VALUE;
//            for (Edge nextMove : availableMoves) {
//                gameTree.addEdge(nextMove);
//
//                Board miniMaxBoard = new Board(board.getRows(), board.getColumns());
//                miniMaxBoard.loadBoard(board.toString());
//                miniMaxBoard.setLineForDots(nextMove.getDotStart(), nextMove.getDotEnd(), ID);
//
//                result = Math.min(result, minimax(level + 1, miniMaxBoard, true, alpha, beta));
//                beta = Math.min(beta, result);
//
//                if (beta <= alpha) {
//                    gameTree.removeEdge(nextMove);
//                    return beta;
//                }
//
//                miniMaxBoard.removeLineForDots(nextMove.getDotStart(), nextMove.getDotEnd());
//                gameTree.removeEdge(nextMove);
//            }
//
//            return result;
//        }
//    }

    /**
     * The method searches by a greedy algorithm for a box with a fourth wall missing.
     * It will be right before completion.
     *
     * @return first found Edge that will complete a box
     */
    private ArrayList<Edge> getCompletionMoves() {

//
//        /**
//         * Previous imlpementation of this method
//         */
//        Board board = game.getBoard();
//        int dots_rows = board.getRows();
//
//        for (int i = 0; i < board.getRows(); i++) {
//            for (int j = 0; j < board.getColumns(); j++) {
//                Board.Box box = board.getBoxAt(i, j);
//
//                if (box.player != Game.Player.NONE)
//                    continue;
//
//                // left missing
//                if (box.bottom & box.top & box.right) {
//                    completionEdges.add(new Edge(i * dots_rows + j, (i + 1) * dots_rows + j + 1));
//                }
//
//                // top missing
//                if (box.bottom & box.left & box.right) {
//                    completionEdges.add(new Edge(i * dots_rows + j, i * dots_rows + j + 1));
//                }
//
//                if (i == board.getRows() - 1) {
//                    // right missing
//                    if (box.bottom & box.top & box.left) {
//                        completionEdges.add(new Edge(i * dots_rows + j + 1, (i + 1) * dots_rows + j + 1));
//                    }
//                }
//
//                if (j == board.getColumns() - 1) {
//                    // bottom missing
//                    if (box.top & box.left & box.right) {
//                        int bottomStart = (i + 1) * dots_rows + j;
//                        int bottomEnd =  (i + 1) * dots_rows + j + 1;
//                        Edge bottomEdge = new Edge(bottomStart, bottomEnd);
//                        completionEdges.add(bottomEdge);
//                    }
//                }
//            }


        ArrayList<Edge> completionMoves = new ArrayList<>();

        ArrayList<Edge> availableMoves = game.getGameTree().getAvailableEdges();
        Board tempBoard = new Board(game.getBoard().getRows(), game.getBoard().getColumns());

        HashMap<String, Edge> madeMoves = game.getGameTree().getEdges();
        for (Edge moveMade : madeMoves.values()) {
            tempBoard.setLineForDots(moveMade.getDotStart(), moveMade.getDotEnd(), Game.Player.NONE);
        }

        for (Edge moveToMake : availableMoves) {
            if (!madeMoves.containsKey(moveToMake.getKey())) {
                int scoreBefore = tempBoard.getScore(Game.Player.PLAYER2);
                tempBoard.setLineForDots(moveToMake.getDotStart(), moveToMake.getDotEnd(), Game.Player.PLAYER2);
                int scoreAfter = tempBoard.getScore(Game.Player.PLAYER2);

                if (scoreBefore != scoreAfter) {
                    completionMoves.add(moveToMake);
                }

                tempBoard.removeLineForDots(moveToMake.getDotStart(), moveToMake.getDotEnd());
            }
        }

        return completionMoves;
    }
}