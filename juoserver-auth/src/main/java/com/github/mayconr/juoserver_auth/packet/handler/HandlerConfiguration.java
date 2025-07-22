package com.github.mayconr.juoserver_auth.packet.handler;

import com.github.mayconr.juoserver_auth.model.core.Core;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class HandlerConfiguration {

    @Bean
    public List<SimpleChannelInboundHandler<?>> packetHandlers(Core core) {
        return List.of(new LoginRequestHandler(core), new SelectServerHandler(core));
    }

}
