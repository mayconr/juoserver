package com.github.mayconr.juoserver_auth.packet;

import io.netty.buffer.ByteBuf;

public abstract class AbstractPacket implements Packet {

    private final int code;
    private final int length;

    public AbstractPacket(int code, int length) {
        this.code = code;
        this.length = length;
    }

    public void writesTo(ByteBuf buf) {
        throw new UnsupportedOperationException("Packet does not support serialization");
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getLength() {
        return length;
    }
}
