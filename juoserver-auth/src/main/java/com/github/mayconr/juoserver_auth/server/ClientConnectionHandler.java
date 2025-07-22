package com.github.mayconr.juoserver_auth.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientConnectionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UOProtocolDecoder.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("New client connected: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.info("Client disconnected: " + ctx.channel().remoteAddress());
    }
}
