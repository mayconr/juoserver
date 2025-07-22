package com.github.mayconr.juoserver.game.packet;

import com.github.mayconr.juoserver.game.server.AbstractPacket;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class DropItem extends AbstractPacket {

    public static final int CODE = (byte) 0x08;
    private int serialId;
    private int x;
    private int y;
    private int z;
    private int containerGridIndex;
    private int containerSerialId;

    public DropItem(ByteBuf buf) {
        super(CODE, 15);
        buf.readByte(); // CODE
        this.serialId = buf.readInt();
        this.x = buf.readShort();
        this.y = buf.readShort();
        this.z = buf.readByte();
        this.containerGridIndex = buf.readByte();
        this.containerSerialId = buf.readInt();
    }

    public boolean isContainerDrop() {
        return containerSerialId > -1;
    }
}
