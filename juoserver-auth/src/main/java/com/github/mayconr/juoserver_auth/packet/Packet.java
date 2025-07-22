package com.github.mayconr.juoserver_auth.packet;

import io.netty.buffer.ByteBuf;

public interface Packet {

    int getCode();

    int getLength();

    void writesTo(ByteBuf buf);

}
