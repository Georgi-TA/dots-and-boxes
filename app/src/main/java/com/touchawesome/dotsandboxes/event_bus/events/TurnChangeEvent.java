package com.touchawesome.dotsandboxes.event_bus.events;

import com.touchawesome.dotsandboxes.game.controllers.Game;

/**
 * Created by scelus on 24.04.17
 */

public class TurnChangeEvent {
    private final Game.Player nextPlayer;

    public TurnChangeEvent(Game.Player nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    @Override
    public String toString() {
        return "TurnChangeEvent{" +
                "nextPlayer=" + nextPlayer +
                '}';
    }
}
