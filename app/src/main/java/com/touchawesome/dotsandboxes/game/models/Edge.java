package com.touchawesome.dotsandboxes.game.models;

import java.util.Locale;

/**
 * Created by scelus on 08.03.17
 */

public class Edge {
    private int dot_start;
    private int dot_end;
    private String key;

    public Node from;

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public Node to;

    public String getKey() {
        return key;
    }

    private void setKey(int dot_start, int dot_end) {
        this.key = generateKey(dot_start, dot_end);
    }

    static String generateKey(int dot_start, int dot_end) {
        return String.format(Locale.US, "%d_%d", dot_start, dot_end);
    }

    public Edge(int dot_start, int dot_end) {
        this.dot_start = dot_start;
        this.dot_end = dot_end;
        setKey(dot_start, dot_end);
    }

    public int getDotStart() {
        return dot_start;
    }

    public int getDotEnd() {
        return dot_end;
    }
}
