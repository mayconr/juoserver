package com.github.mayconr.juoserver_auth.packet;

import io.netty.buffer.ByteBuf;

public class SelectServer extends AbstractPacket {

    public static final int CODE = (byte) 0xA0;
    private final int index;

    public SelectServer(ByteBuf buf) {
        super(CODE, 3);
        buf.readByte(); // code
        this.index = buf.readShort();
    }

    public int getIndex() {
        return index;
    }
}
