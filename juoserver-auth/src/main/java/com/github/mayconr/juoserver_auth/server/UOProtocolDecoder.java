package com.github.mayconr.juoserver_auth.server;

import com.github.mayconr.juoserver_auth.packet.AbstractPacket;
import com.github.mayconr.juoserver_auth.packet.LoginRequestPacket;
import com.github.mayconr.juoserver_auth.packet.LoginSeedPacket;
import com.github.mayconr.juoserver_auth.packet.SelectServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

public class UOProtocolDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(UOProtocolDecoder.class);
    private final Map<Integer, Class<? extends AbstractPacket>> packetsClass = new HashMap<>();
    private boolean hasSeed;

    public UOProtocolDecoder() {
        packetsClass.put(LoginSeedPacket.CODE, LoginSeedPacket.class);
        packetsClass.put(LoginRequestPacket.CODE, LoginRequestPacket.class);
        packetsClass.put(SelectServer.CODE, SelectServer.class);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!hasSeed) {
            var seed = new LoginSeedPacket(in);
            LOGGER.info("Seed received");
            this.hasSeed = true;
        }
        boolean hasUnknownPacket = false;
        while (in.readableBytes() > 0 && !hasUnknownPacket) {
            var code = in.getByte(in.readerIndex());
            var hexCode = HexFormat.of().formatHex(new byte[]{code}).toUpperCase();

            var packetClass = packetsClass.get((int) code);
            if (packetClass != null) {
                out.add(packetClass.getConstructor(ByteBuf.class).newInstance(in));
                LOGGER.info("Packet received [0x{} - {}]", hexCode, packetClass.getSimpleName());
            } else {
                hasUnknownPacket = true;
                LOGGER.info("Unknown packet [0x{}] is not possible to decode remaining data.", hexCode);
            }
        }
    }
}
