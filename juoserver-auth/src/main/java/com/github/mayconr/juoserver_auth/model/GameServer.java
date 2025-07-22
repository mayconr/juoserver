package com.github.mayconr.juoserver_auth.model;

import java.net.InetAddress;

public record GameServer(String name, InetAddress address, int port) {
}
