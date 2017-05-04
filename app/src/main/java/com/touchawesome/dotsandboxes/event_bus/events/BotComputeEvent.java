package com.touchawesome.dotsandboxes.event_bus.events;

import com.touchawesome.dotsandboxes.game.models.Edge;

/**
 * Created by scelus on 24.04.17
 */

public class BotComputeEvent {
    public final Edge botMove;

    public BotComputeEvent(Edge botMove) {
        this.botMove = botMove;
    }

    @Override
    public String toString() {
        return "BotComputeEvent{" +
                "botMove=" + botMove +
                '}';
    }
}
