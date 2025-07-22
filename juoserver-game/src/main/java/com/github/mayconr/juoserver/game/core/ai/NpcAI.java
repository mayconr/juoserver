package com.github.mayconr.juoserver.game.core.ai;

import com.github.mayconr.juoserver.game.core.event.EventBus;
import com.github.mayconr.juoserver.game.core.session.npc.NpcSession;

public interface NpcAI {

    void initialize(NpcSession npcSession, EventBus eventBus);

}
