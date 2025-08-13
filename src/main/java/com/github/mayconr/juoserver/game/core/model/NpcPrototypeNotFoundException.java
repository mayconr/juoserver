package com.github.mayconr.juoserver.game.core.model;

public class NpcPrototypeNotFoundException extends RuntimeException {
    public NpcPrototypeNotFoundException(int npcId) {
        super("Prototype not found for npcId ["+npcId+"]");
    }
}
