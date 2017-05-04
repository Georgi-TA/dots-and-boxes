package com.touchawesome.dotsandboxes.event_bus.events;

import com.touchawesome.dotsandboxes.game.controllers.Game;

/**
 * Created by scelus on 24.04.17
 */

public class ScoreMadeEvent {
    public final Game.Player scoringPlayer;
    public final int score;

    public ScoreMadeEvent(Game.Player scoringPlayer, int score) {
        this.scoringPlayer = scoringPlayer;
        this.score = score;
    }

    @Override
    public String toString() {
        return "ScoreMadeEvent{" +
                "scoringPlayer=" + scoringPlayer +
                ", score=" + score +
                '}';
    }
}
