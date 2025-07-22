package com.github.mayconr.juoserver_auth.packet;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class LoginRequestPacket extends AbstractPacket {

    public static final int CODE = (byte) 0x80;
    private String username;
    private String password;

    public LoginRequestPacket(ByteBuf buf) {
        super(CODE, 62);
        buf.readByte(); // code
        username = buf.readCharSequence(30, StandardCharsets.UTF_8).toString();
        password = buf.readCharSequence(30, StandardCharsets.UTF_8).toString();
        buf.readByte(); // login key
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
