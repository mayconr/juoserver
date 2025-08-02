package com.github.mayconr.juoserver_auth.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import java.util.List;

public class ServerConfiguration {

    @Bean
    public ConnectionLoggingHandler connectionLoggingHandler() {
        return new ConnectionLoggingHandler();
    }

    @DependsOn({"connectionLoggingHandler", "packetHandlers"})
    @Bean
    public UOChannelInitializer channelInitializer(ConnectionLoggingHandler connectionLoggingHandler,
                                                   List<SimpleChannelInboundHandler<?>> packetHandlers) {
        return new UOChannelInitializer(connectionLoggingHandler, packetHandlers);
    }

    @Qualifier("parent")
    @Bean
    public NioEventLoopGroup parentNioEventLoopGroup() {
        return new NioEventLoopGroup(1);
    }

    @Qualifier("child")
    @Bean
    public NioEventLoopGroup childNioEventLoopGroup() {
        return new NioEventLoopGroup(1);
    }
    @Bean
    public ServerBootstrap serverBootstrap(UOChannelInitializer channelInitializer, @Qualifier("parent") NioEventLoopGroup parentNioEventLoopGroup, @Qualifier("child") NioEventLoopGroup childNioEventLoopGroup) {
        return new ServerBootstrap()
                .group(parentNioEventLoopGroup, childNioEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    @Bean
    public ServerStartup serverStartup(ServerBootstrap serverBootstrap, @Qualifier("parent") NioEventLoopGroup parentNioEventLoopGroup, @Qualifier("child") NioEventLoopGroup childNioEventLoopGroup) {
        return new ServerStartup(serverBootstrap, parentNioEventLoopGroup, childNioEventLoopGroup);
    }
}
