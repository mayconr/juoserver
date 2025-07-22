package com.github.mayconr.juoserver_auth.packet;

import io.netty.buffer.ByteBuf;

import java.net.InetAddress;

public class ServerConnectPacket extends AbstractPacket {

    public static final int CODE = (byte) 0x8C;
    private final InetAddress host;
    private final int port;
    private final int authKey;

    public ServerConnectPacket(InetAddress host, int port, int authKey) {
        super(CODE, 11);
        this.host = host;
        this.port = port;
        this.authKey = authKey;
    }

    @Override
    public void writesTo(ByteBuf buf) {
        buf.writeByte(CODE)
            .writeBytes(host.getAddress())
            .writeShort(port)
            .writeInt(authKey);
    }

    public InetAddress getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getAuthKey() {
        return authKey;
    }
}
