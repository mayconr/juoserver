package com.github.mayconr.juoserver.game.core.session;

import com.github.mayconr.juoserver.game.core.ai.ollama.OllanaClient;
import com.github.mayconr.juoserver.game.core.database.Database;
import com.github.mayconr.juoserver.game.core.event.EventBus;
import com.github.mayconr.juoserver.game.core.model.*;
import com.github.mayconr.juoserver.game.core.session.npc.DefaultNpcSession;
import com.github.mayconr.juoserver.game.core.session.npc.NpcSession;
import com.github.mayconr.juoserver.game.core.session.player.PlayerSession;
import com.github.mayconr.juoserver.game.core.session.player.PlayerSessionFactory;
import com.github.mayconr.juoserver.game.packet.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DefaultGameSession implements GameSession {

    private final Database database;
    private final ChannelGroup channelGroup;
    private final EventBus eventBus;
    private final PlayerSessionFactory playerSessionFactory;
    private final OllanaClient ollanaClient;
    private final Map<UONpc, NpcSession> npcNpcSessionMap = new HashMap<>();
    private final Map<UOPlayer, PlayerSession> playerSessionMap = new HashMap<>();

    @Override
    public void sendSystemMessage(String message) {
        channelGroup.writeAndFlush(new SendSpeech(TextType.BROADCAST, 2046, 0, 0, 1, "System", message));
    }

    @Override
    public PlayerSession getPlayerSession(UOMobile mobile) {
        return playerSessionMap.get(mobile);
    }

    @Override
    public NpcSession createNpcSession(int npcId, Location location) {
        final var npc = database.createNpcAtLocation(npcId, location);
        try {
            final var npcAI = npc.getAiClass().getConstructor(GameSession.class, Database.class, OllanaClient.class).newInstance(this, database, ollanaClient);
            final var session = npcNpcSessionMap.putIfAbsent(npc, new DefaultNpcSession(npc, channelGroup, eventBus, npcAI));
            channelGroup.writeAndFlush(new DrawObject(npc));
            return session;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create AI for ["+npc.getAiClass()+"]", e);
        }
    }

    @Override
    public PlayerSession createPlayerSession(UOPlayer player, ChannelHandlerContext ctx) {
        return playerSessionMap.computeIfAbsent(player, pl -> {
            pl.setConnected(true);

            ctx.channel().closeFuture().addListener(future -> {
                playerSessionMap.remove(pl);
                pl.setConnected(false);
                log.info("Session closed for mobile [{}-{}]", pl.getSerialId(), pl.getName());
            });

            return playerSessionFactory.createPlayerSession(pl, ctx);
        });
    }

    @Override
    public UOItem createItemAtLocation(int itemId, Location location) {
        final var item = database.createItemAtLocation(itemId, location);
        updateItem(item, location);
        return item;
    }

    @Override
    public UOItem createItemAtLocation(String name, Location location) {
        final var item = database.createItemAtLocation(name, location);
        updateItem(item, location);
        return item;
    }

    private void updateItem(UOItem item, Location location) {
        channelGroup.write(new ObjectInfo(item));
        channelGroup.write(new ObjectRevision(item));
        channelGroup.flush();
        if (log.isDebugEnabled())
            log.debug("Item [{}] created a location [{},{},{}] with serialId [{}]", item, location.getX(), location.getY(), location.getZ(), item.getSerialId());
    }

    @Override
    public void deleteItem(UOItem item) {
        database.deleteItem(item);
        channelGroup.writeAndFlush(new DeleteObject(item)); // TODO filter by range
    }

    @Override
    public void moveItem(UOItem item, Location location) {
        item.setLocation(location);
        channelGroup.writeAndFlush(new ObjectInfo(item));
    }
}
