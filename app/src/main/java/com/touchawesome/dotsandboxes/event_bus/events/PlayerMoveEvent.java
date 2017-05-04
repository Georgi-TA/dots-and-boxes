package com.touchawesome.dotsandboxes.event_bus.events;

import com.touchawesome.dotsandboxes.game.models.Edge;

/**
 * Created by scelus on 25.04.17
 */

public class PlayerMoveEvent {

    public final Edge playerMove;

    public PlayerMoveEvent(Edge playerMove) {
        this.playerMove = playerMove;
    }

    @Override
    public String toString() {
        return "PlayerMoveEvent{" +
                "playerMove=" + playerMove +
                '}';
    }
}
