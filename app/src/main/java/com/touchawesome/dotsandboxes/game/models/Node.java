package com.touchawesome.dotsandboxes.game.models;

import java.util.ArrayList;

/**
 * Created by scelus on 28.03.17
 */
class Node {
    private String board;           // name used for hashing
    private ArrayList<Edge> links; // the links to child nodes

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private int value;             // the score value of the node

    public Node(String board, int value) {
        this.links = new ArrayList<>();
        this.value = value;
        this.board = board;
    }
}
