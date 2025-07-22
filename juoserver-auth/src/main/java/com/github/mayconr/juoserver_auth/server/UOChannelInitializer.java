package com.github.mayconr.juoserver_auth.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

import java.util.List;

public class UOChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ConnectionLoggingHandler connectionLoggingHandler;
    private final List<SimpleChannelInboundHandler<?>> packetHandlers;

    public UOChannelInitializer(ConnectionLoggingHandler connectionLoggingHandler, List<SimpleChannelInboundHandler<?>> packetHandlers) {
        this.connectionLoggingHandler = connectionLoggingHandler;
        this.packetHandlers = packetHandlers;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ClientConnectionHandler());
        ch.pipeline().addLast(connectionLoggingHandler);
        ch.pipeline().addLast(new UOProtocolDecoder());
        ch.pipeline().addLast(new UOProtocolEncoder());
        for (SimpleChannelInboundHandler<?> handler : packetHandlers) {
            ch.pipeline().addLast(handler);
        }
        ch.pipeline().addLast(new ErrorHandler());
    }
}
