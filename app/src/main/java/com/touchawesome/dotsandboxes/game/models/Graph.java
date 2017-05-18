package com.touchawesome.dotsandboxes.game.models;

import com.touchawesome.dotsandboxes.game.controllers.Game;

import java.util.ArrayList;
import java.util.HashMap;

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

    private int rows;
    private int columns;

    /**
     * Default constructor
     */
    public Graph(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.edges = new HashMap<>();
    }

    public Graph getCopy() {
        Graph graph = new Graph(rows, columns);
        for (Edge edge : edges.values())
            graph.addEdge(edge);

        return graph;
    }

    public ArrayList<Edge> getAvailableEdges() {
        ArrayList<Edge> availableEdges = new ArrayList<>();

        int dots_rows = rows + 1;
        int dots_columns = columns + 1;
        int allDotsCount = dots_rows * dots_columns;

        for (int i = 0; i < allDotsCount; i++) {
            // horizontal
            if (i % dots_columns != (dots_columns - 1)) {
                Edge edge = new Edge(i, i + 1);
                if (!hasEdge(edge.getKey()))
                    availableEdges.add(edge);
            }

            // vertical
            if (i / dots_rows != (dots_rows - 1)) {
                Edge edge = new Edge(i, i + dots_columns);
                if (!hasEdge(edge.getKey()))
                    availableEdges.add(edge);
            }
        }

        return availableEdges;
    }


    /**
     * Add an edge to the game tree
     *
     * @param edge the edge to be added
     */
    public void addEdge(Edge edge) {
        edges.put(edge.getKey(), edge);
    }

    public boolean hasEdge(int dotStart, int dotEnd) {
        return edges.containsKey(Edge.generateKey(dotStart, dotEnd));
    }

    public boolean hasEdge(String edgeKey) {
        return edges.containsKey(edgeKey);
    }
}

