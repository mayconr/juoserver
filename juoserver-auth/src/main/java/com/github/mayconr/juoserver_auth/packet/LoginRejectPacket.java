package com.github.mayconr.juoserver_auth.packet;

import io.netty.buffer.ByteBuf;

public class LoginRejectPacket extends AbstractPacket {

    private static final int CODE = (byte) 0x53;

    public LoginRejectPacket() {
        super(CODE, 2);
    }

    @Override
    public void writesTo(ByteBuf buf) {
        buf.writeByte(CODE);
        buf.writeByte(0);
    }

}
