package com.github.mayconr.juoserver_auth;

import com.github.mayconr.juoserver_auth.model.core.DatabaseConfiguration;
import com.github.mayconr.juoserver_auth.packet.handler.HandlerConfiguration;
import com.github.mayconr.juoserver_auth.server.ServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({HandlerConfiguration.class, ServerConfiguration.class, DatabaseConfiguration.class})
public class JUOServerConfiguration {
}
