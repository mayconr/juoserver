package com.github.mayconr.juoserver.game.core.session.player;

import com.github.mayconr.juoserver.game.core.database.Database;
import com.github.mayconr.juoserver.game.core.event.EventBus;
import com.github.mayconr.juoserver.game.core.gameloop.GameLoop;
import com.github.mayconr.juoserver.game.core.model.UOPlayer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerSessionFactory {

    private final ChannelGroup channelGroup;
    private final EventBus eventBus;
    private final Database database;
    private final GameLoop gameLoop;

    public PlayerSession createPlayerSession(UOPlayer mobile, ChannelHandlerContext ctx) {
        final var initializationServie = new InitializationService(mobile, eventBus, channelGroup, ctx, database);
        final var speechService = new SpeechService(mobile, eventBus, channelGroup);
        final var movementService = new MovementService(mobile, eventBus, channelGroup, ctx, database);
        final var itemIteractionService = new ItemInteractionService(mobile, channelGroup, ctx, database);
        final var clickService = new DoubleClickService(mobile, database, ctx);
        final var megaClilocService = new MegaClilocService(mobile, ctx, database);
        final var targetService = new TargetService(mobile, ctx, eventBus);
        final var session = new DefaultPlayerSession(mobile, initializationServie, speechService, movementService, itemIteractionService, clickService, megaClilocService, targetService);
        gameLoop.addTask(new PlayerVitalsTask(session));
        return session;
    }

}
