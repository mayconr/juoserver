package com.github.mayconr.juoserver_auth.packet;

import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.ArrayUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LoginSeedPacket extends AbstractPacket {

    public static final int CODE = (byte) 0xEF;
    private InetAddress address;
    private String clientVersion;

    public LoginSeedPacket(ByteBuf buf) {
        super(CODE, 21);

        buf.readByte(); // ignore code

        byte[] addressBytes = new byte[4];
        buf.readBytes(addressBytes);
        try {
            ArrayUtils.reverse(addressBytes);
            address = InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        clientVersion = buf.readInt()+"."+buf.readInt()+"."+buf.readInt()+"."+buf.readInt();
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    @Override
    public String toString() {
        return "LoginSeedPacket{" +
                "address=" + address +
                ", clientVersion='" + clientVersion + '\'' +
                '}';
    }
}
