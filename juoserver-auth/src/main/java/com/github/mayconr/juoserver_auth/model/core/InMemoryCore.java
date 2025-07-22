package com.github.mayconr.juoserver_auth.model.core;

import com.github.mayconr.juoserver_auth.model.GameServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class InMemoryCore implements Core {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCore.class);

    @Override
    public List<GameServer> getGameServers() {
        try {
            return List.of(new GameServer("Local 1", InetAddress.getByName("localhost"), 9000), new GameServer("Local 2", InetAddress.getByName("127.0.0.1"), 9000));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean authenticate(String username, String password) {
        LOGGER.info("Logging: username: {} password: {}", username, password);
        return true;
    }
}
