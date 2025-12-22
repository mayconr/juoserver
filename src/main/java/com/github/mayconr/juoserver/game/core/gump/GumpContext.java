package com.github.mayconr.juoserver.game.core.gump;

public record GumpContext(
        int gumpId,
        int ownerSerial,
        long createdAt,
        GumpHandler handler
) {
}
