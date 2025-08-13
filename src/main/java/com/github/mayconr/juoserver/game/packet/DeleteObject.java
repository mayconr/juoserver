package com.github.mayconr.juoserver.game.packet;

import com.github.mayconr.juoserver.game.core.model.UOItem;
import com.github.mayconr.juoserver.game.server.AbstractPacket;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class DeleteObject extends AbstractPacket {
    public static final int CODE = (byte) 0x1D;

    private final UOItem uoItem;

    public DeleteObject(UOItem uoItem) {
        super(CODE, 5);
        this.uoItem = uoItem;
    }

    @Override
    public void writesTo(ByteBuf buf) {
        buf.writeByte(CODE);
        buf.writeInt(uoItem.getSerialId());
    }
}
