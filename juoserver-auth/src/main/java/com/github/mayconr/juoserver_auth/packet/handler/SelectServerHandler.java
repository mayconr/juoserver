package com.github.mayconr.juoserver_auth.packet.handler;

import com.github.mayconr.juoserver_auth.model.core.Core;
import com.github.mayconr.juoserver_auth.packet.SelectServer;
import com.github.mayconr.juoserver_auth.packet.ServerConnectPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ThreadLocalRandom;

@ChannelHandler.Sharable
public class SelectServerHandler extends SimpleChannelInboundHandler<SelectServer> {

    private final Core core;

    public SelectServerHandler(Core core) {
        this.core = core;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SelectServer msg) throws Exception {
        var server = core.getGameServers().get(msg.getIndex());
        var authKey = ThreadLocalRandom.current().nextInt(9999);
        ctx.writeAndFlush(new ServerConnectPacket(server.address(), server.port(), authKey));
    }
}
