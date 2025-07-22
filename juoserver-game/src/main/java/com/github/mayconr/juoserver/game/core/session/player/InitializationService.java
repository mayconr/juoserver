package com.github.mayconr.juoserver.game.core.session.player;

import com.github.mayconr.juoserver.game.core.database.Database;
import com.github.mayconr.juoserver.game.core.database.MobileFilter;
import com.github.mayconr.juoserver.game.core.event.EventBus;
import com.github.mayconr.juoserver.game.core.event.PlayerSessionStarted;
import com.github.mayconr.juoserver.game.core.model.Season;
import com.github.mayconr.juoserver.game.core.model.UOMobile;
import com.github.mayconr.juoserver.game.packet.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class InitializationService {

    private final UOMobile mobile;
    private final EventBus eventBus;
    private final ChannelGroup channelGroup;
    private final ChannelHandlerContext ctx;
    private final Database database;

    public void initialize(PlayerSession session, String clientVersion) {
        ctx.write(new LoginConfirm(mobile, 7168, 4096));
        ctx.write(new SeasonalInformation(Season.Summer, true));
        ctx.write(new DrawGamePlayer(mobile));
        ctx.write(new DrawObject(mobile));
        ctx.write(new LoginComplete());
        database.getMobilesInRange(mobile, MobileFilter.ALL_VISIBLE)
                .filter(someone->!someone.equals(mobile)) // avoid unnecessary packet
                .forEach(someone->ctx.write(new DrawObject(someone)));
        ctx.flush();

        channelGroup.writeAndFlush(new DrawObject(mobile), channel -> !channel.equals(ctx.channel()));
        eventBus.publish(new PlayerSessionStarted(session));
    }

}
