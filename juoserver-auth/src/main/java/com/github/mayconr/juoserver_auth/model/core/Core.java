package com.github.mayconr.juoserver_auth.model.core;

import com.github.mayconr.juoserver_auth.model.GameServer;

import java.util.List;

public interface Core {

    List<GameServer> getGameServers();

    boolean authenticate(String username, String password);
}
