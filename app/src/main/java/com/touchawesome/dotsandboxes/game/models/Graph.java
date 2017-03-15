package com.touchawesome.dotsandboxes.game.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import com.touchawesome.dotsandboxes.game.controllers.Game;

import static android.content.ContentValues.TAG;

/**
 * A class representing the game tree in form of a graph
 * the {@link com.touchawesome.dotsandboxes.game.models.Board} board is presented
 *
 * Created by scelus on 08.03.17
 */

public class Graph {
    public HashMap<String, Edge> getEdges() {
        return edges;
    }

    private HashMap<String, Edge> edges;

    /**
     * Default constructor
     */
    public Graph() {
        this.edges = new HashMap<>();
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
     * Add an edge to the game tree
     * @param edge the edge to be added
     */
    public void addEdge(Edge edge) {
        edges.put(edge.getKey(), edge);
    }

    /**
     * Remove an edge from the game tree
     * @param edge the edge to be removed
     */
    public void removeEdge(Edge edge) {
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

    public boolean hasEdge(int dotStart, int dotEnd) {
        return edges.containsKey(Edge.generateKey(dotStart, dotEnd));
    }
}
