package com.github.mayconr.juoserver_auth.packet;

import com.github.mayconr.juoserver_auth.model.GameServer;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class ServerListPacket extends AbstractPacket {

    public static final int CODE = (byte) 0xA8;
    private static final int SERVER_NAME_SIZE = 32;

    private List<GameServer> gameServers;

    public ServerListPacket(List<GameServer> gameServers) {
        super(CODE, 6 + gameServers.size() * (2 + SERVER_NAME_SIZE + 2 + 4));
        this.gameServers = gameServers;
    }

    @Override
    public void writesTo(ByteBuf buf) {
        buf.writeByte(getCode());
        buf.writeShort(getLength());
        buf.writeByte((byte) 0x5D); // Unknown
        buf.writeShort(gameServers.size());

        short counter = 0;
        for (GameServer gameServer : gameServers) {
            buf.writeShort(counter++);
            buf.writeBytes(Arrays.copyOf(gameServer.name().getBytes(StandardCharsets.UTF_8), SERVER_NAME_SIZE));
            buf.writeByte(0); // percent full
            buf.writeByte(0); // timezone

            byte[] ipBytes = gameServer.address().getAddress();
            ArrayUtils.reverse(ipBytes);
            buf.writeBytes(Arrays.copyOf(ipBytes, 4));
        }
    }
}
