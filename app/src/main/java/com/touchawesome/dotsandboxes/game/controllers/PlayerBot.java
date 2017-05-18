package com.touchawesome.dotsandboxes.game.controllers;

import com.touchawesome.dotsandboxes.game.models.Board;
import com.touchawesome.dotsandboxes.game.models.Edge;
import com.touchawesome.dotsandboxes.game.models.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by scelus on 15.03.17
 */
public class PlayerBot {
    private Game game;

    public PlayerBot(Game game) {
        this.game = game;
    }

    public Edge getNextMove() {

        // get a box completion move
        ArrayList<Edge> completionMoves = getCompletionMoves(game.getGameTree(), game.getBoard());

        // return the completion move that will be most profitable if such exists
        if (completionMoves.size() > 0) {
            Collections.shuffle(completionMoves);
            return completionMoves.get(0);
        }
        // otherwise get a random available move
        else {
            int minOpponentScoreAfterMove = Integer.MAX_VALUE;
            int opponentScore;
            Edge nextMove = null;

            ArrayList<Edge> availableMoves = game.getGameTree().getAvailableEdges();
            // try every move and see how many points can the opponent make afterwards
            for (Edge moveToMake : availableMoves) {
                // work with  a copy of the board so that it will not affect the real game board
                Board boardCopy = game.getBoard().getCopy();
                Graph gameTreeCopy = game.getGameTree().getCopy();
                ArrayList<Edge> opponentCompletionMoves;

                // place the bot future move
                boardCopy.setLineForDots(moveToMake.getDotStart(), moveToMake.getDotEnd(), Game.Player.PLAYER2);

                // record the move in the game tree copy
                gameTreeCopy.addEdge(moveToMake);

                // start making all moves in favour of the opponent (human player) and see which move will lead to him making the least points
                opponentScore = 0;
                opponentCompletionMoves = getCompletionMoves(gameTreeCopy, boardCopy);

                Graph gameTreeOpponent = gameTreeCopy.getCopy();
                Board boardOpponent = boardCopy.getCopy();

                while (opponentCompletionMoves.size() > 0) {
                    opponentScore += opponentCompletionMoves.size();

                    for (Edge opponentMoveToMake : opponentCompletionMoves) {
                        boardOpponent.setLineForDots(opponentMoveToMake.getDotStart(), opponentMoveToMake.getDotEnd(), Game.Player.PLAYER1);
                        gameTreeOpponent.addEdge(opponentMoveToMake);
                    }

                    opponentCompletionMoves = getCompletionMoves(gameTreeOpponent, boardOpponent);
                }

                if (opponentScore < minOpponentScoreAfterMove) {
                    minOpponentScoreAfterMove = opponentScore;
                    nextMove = moveToMake;
                }
            }

            if (nextMove != null) {
                return nextMove;
            }
            else {
                Collections.shuffle(availableMoves);
                return availableMoves.get(0);
            }
        }
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
     * @return all edges that will complete a box
     */
    private ArrayList<Edge> getCompletionMoves(Graph gameTree, Board board) {

        ArrayList<Edge> completionMoves = new ArrayList<>();
        HashMap<String, Edge> madeMoves = gameTree.getEdges();
        ArrayList<Edge> availableMoves = gameTree.getAvailableEdges();

        for (Edge moveToMake : availableMoves) {
            if (!madeMoves.containsKey(moveToMake.getKey())) {
                Board tempBoard = board.getCopy();
                int scoreBefore = tempBoard.getScore(Game.Player.PLAYER2);
                tempBoard.setLineForDots(moveToMake.getDotStart(), moveToMake.getDotEnd(), Game.Player.PLAYER2);
                int scoreAfter = tempBoard.getScore(Game.Player.PLAYER2);

                if (scoreBefore < scoreAfter) {
                    completionMoves.add(moveToMake);
                }
            }
        }

        return completionMoves;
    }
}