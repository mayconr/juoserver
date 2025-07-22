package com.github.mayconr.juoserver.game.core.agent;

import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgentConfiguration {

    @Bean
    public ExecutorService executor() {
        return Executors.newFixedThreadPool(2);
    }

}
