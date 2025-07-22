package com.github.mayconr.juoserver.game.core.session.npc;

import com.github.mayconr.juoserver.game.core.ai.NpcAI;
import com.github.mayconr.juoserver.game.core.event.EventBus;
import com.github.mayconr.juoserver.game.core.model.Direction;
import com.github.mayconr.juoserver.game.core.model.Location;
import com.github.mayconr.juoserver.game.core.model.UONpc;
import com.github.mayconr.juoserver.game.packet.DrawObject;
import com.github.mayconr.juoserver.game.packet.SendSpeech;
import io.netty.channel.group.ChannelGroup;

import java.util.Collections;

public class DefaultNpcSession implements NpcSession {

    private final UONpc npc;
    private final ChannelGroup channelGroup;
    private final EventBus eventBus;
    private final NpcAI npcAI;

    public DefaultNpcSession(UONpc npc, ChannelGroup channelGroup, EventBus eventBus, NpcAI npcAI) {
        this.npc = npc;
        this.channelGroup = channelGroup;
        this.eventBus = eventBus;
        this.npcAI = npcAI;
        this.npcAI.initialize(this, eventBus);
    }

    @Override
    public UONpc getNpc() {
        return npc;
    }

    @Override
    public void walk(Direction direction) {
        System.out.println("npc andando");
    }

    @Override
    public void speech(String message) {
        channelGroup.writeAndFlush(new SendSpeech(npc, message));
    }

    @Override
    public void move(Direction direction) {
        npc.move(direction);
        channelGroup.writeAndFlush(new DrawObject(npc));
    }

    @Override
    public void move(Location location) {
        var map = new boolean[2600][600];
        for (int x = 0; x<2600; x++) {
            for (int y=0; y<600; y++) {
                map[x][y] = x >= 2512 && x<= 2518 && y>=442 && y<=550;
            }
        }
        try {
            final var pathfinder = new Pathfinder(map, Collections.emptySet());
            pathfinder.findNextDirection(npc, location).ifPresent(npc::move);
            channelGroup.writeAndFlush(new DrawObject(npc));
        } catch (Exception exception) {
            exception.printStackTrace();;
        }

    }
}
