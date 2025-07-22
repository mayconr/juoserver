package com.github.mayconr.juoserver_auth.model.core;

import org.springframework.context.annotation.Bean;

public class DatabaseConfiguration {

    @Bean
    public Core database() {
        return new InMemoryCore();
    }

}
