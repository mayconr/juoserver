package com.github.mayconr.juoserver_auth.server;

import com.github.mayconr.juoserver_auth.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HexFormat;

@ChannelHandler.Sharable
public class UOProtocolEncoder extends MessageToByteEncoder<Packet> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UOProtocolEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {

        final var buf = Unpooled.buffer(msg.getLength());
        msg.writesTo(buf);

        if (LOGGER.isDebugEnabled()) {
            var hex = HexFormat.ofDelimiter(" ");
            LOGGER.info("Sending packet [Code: 0x{} - Content: {}] ", hex.formatHex(new byte[]{(byte) msg.getCode()}), hex.formatHex(buf.array()));
        } else {
            LOGGER.info("Sending packet [0x{}] ", msg.getCode());
        }

        out.writeBytes(buf);
    }
}
