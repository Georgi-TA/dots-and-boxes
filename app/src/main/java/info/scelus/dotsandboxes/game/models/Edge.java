package info.scelus.dotsandboxes.game.models;

import java.util.Locale;

/**
 * Created by scelus on 08.03.17.
 */

public class Edge {
    int dot_start;
    int dot_end;
    String key;

    public int getDot_start() {
        return dot_start;
    }

    public void setDot_start(int dot_start) {
        this.dot_start = dot_start;
    }

    public int getDot_end() {
        return dot_end;
    }

    public void setDot_end(int dot_end) {
        this.dot_end = dot_end;
    }

    public String getKey() {
        return key;
    }

    private void setKey(int dot_start, int dot_end) {
        this.key = String.format(Locale.US, "%d_%d", dot_start, dot_end);
    }

    public Edge(int dot_start, int dot_end) {
        this.dot_start = dot_start;
        this.dot_end = dot_end;
        setKey(dot_start, dot_end);
    }
}
