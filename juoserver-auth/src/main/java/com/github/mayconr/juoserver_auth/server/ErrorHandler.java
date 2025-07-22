package com.github.mayconr.juoserver_auth.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ErrorHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            LOGGER.error("I/O error: {}", cause.getMessage());
        } else if (cause instanceof DecoderException) {
            LOGGER.error("Protocol error: {}", cause.getMessage(), cause);
        } else {
            LOGGER.error("Unexpected error: {}", cause.getMessage(), cause);
        }
    }
}
