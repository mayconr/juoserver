package com.github.mayconr.juoserver_auth.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ConnectionLoggingHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionLoggingHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final var remoteAddress = ctx.channel().remoteAddress();
        LOGGER.info("New connection from: " + remoteAddress);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final var remoteAddress = ctx.channel().remoteAddress();
        LOGGER.info("Connection closed: " + remoteAddress);
        super.channelInactive(ctx);
    }
}
