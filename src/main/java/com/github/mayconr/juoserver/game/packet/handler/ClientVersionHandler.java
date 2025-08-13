package com.github.mayconr.juoserver.game.packet.handler;

import com.github.mayconr.juoserver.game.core.session.player.PlayerSession;
import com.github.mayconr.juoserver.game.packet.ClientVersion;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public
class ClientVersionHandler extends PlayerSessionChannelInboundHandler<ClientVersion> {

    @Override
    protected void channelRead0(PlayerSession session, ChannelHandlerContext ctx, ClientVersion msg) {
        session.initialize(msg.getClientVersion());
    }
}
