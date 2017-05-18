package com.touchawesome.dotsandboxes.event_bus.events;

import com.touchawesome.dotsandboxes.game.controllers.Game;

/**
 * Created by scelus on 24.04.17
 */

public class GameEndEvent {
    private final Game.Player winner;

    public GameEndEvent(Game.Player winner) {
        this.winner = winner;
    }

    @Override
    public String toString() {
        return "GameEndEvent{" +
                "winner=" + winner +
                '}';
    }
}
