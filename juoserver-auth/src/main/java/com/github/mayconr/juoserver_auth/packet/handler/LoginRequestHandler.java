package com.github.mayconr.juoserver_auth.packet.handler;

import com.github.mayconr.juoserver_auth.model.core.Core;
import com.github.mayconr.juoserver_auth.packet.LoginRejectPacket;
import com.github.mayconr.juoserver_auth.packet.LoginRequestPacket;
import com.github.mayconr.juoserver_auth.packet.ServerListPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    private final Core core;

    public LoginRequestHandler(Core core) {
        this.core = core;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket msg) throws Exception {
        if (core.authenticate(msg.getUsername(), msg.getPassword())) {
            var gameServers = core.getGameServers();
            ctx.writeAndFlush(new ServerListPacket(gameServers));
        } else {
            ctx.writeAndFlush(new LoginRejectPacket());
        }
    }
}
