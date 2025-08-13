package com.github.mayconr.juoserver.game.packet;

import com.github.mayconr.juoserver.game.core.model.UOMobile;
import com.github.mayconr.juoserver.game.core.model.UOPlayer;
import com.github.mayconr.juoserver.game.server.AbstractPacket;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class SendGumpDialog extends AbstractPacket {

    public static final int CODE = 0xB0;
    private final UOPlayer player;
    private final int gumpId;
    private final int x;
    private final int y;
    private final String layout;        // ex: "page 0\nresizepic 0 0 5054 200 150\nbutton 20 20 4005 4007 1 0 1\ntext 50 50 0 0"
    private final List<String> texts;   // textos referenciados pelo layout (indices 0..N-1)

    public SendGumpDialog(UOPlayer player, int gumpId, int x, int y, String layout, List<String> texts) {
        super(CODE, computeLength(Objects.requireNonNull(layout), Objects.requireNonNull(texts)));
        this.player = Objects.requireNonNull(player);
        this.gumpId = gumpId;
        this.x = x;
        this.y = y;
        this.layout = layout;
        this.texts = texts;
    }

    private static int computeLength(String layout, List<String> texts) {
        // opcode(1) + size(2) + sender(4) + gumpId(4) + x(4) + y(4) + cmdLen(2)
        int len = 1 + 2 + 4 + 4 + 4 + 4 + 2;

        // layout ASCII (sem 0-terminator)
        int layoutBytes = layout.getBytes(CharsetUtil.US_ASCII).length;
        len += layoutBytes;

        // textLinesCount (2)
        len += 2;

        // cada linha: length(2) em chars + dados UTF-16BE (2 bytes por char)
        for (String s : texts) {
            len += 2;               // length (chars)
            len += s.length() * 2;  // dados
        }
        return len;
    }

    @Override
    public void writesTo(ByteBuf buf) {
        buf.writeByte(CODE);
        buf.writeShort(getLength());

        buf.writeInt(player.getSerialId()); // sender (UInt32 BE)
        buf.writeInt(gumpId);               // gumpID
        buf.writeInt(x);                    // x
        buf.writeInt(y);                    // y

        // layout
        byte[] cmd = layout.getBytes(CharsetUtil.US_ASCII);
        buf.writeShort(cmd.length); // cmdLen (ushort)
        buf.writeBytes(cmd);        // sem terminador

        // textos
        buf.writeShort(texts.size()); // textLinesCount
        for (String s : texts) {
            buf.writeShort(s.length());          // length em chars
            buf.writeBytes(s.getBytes(StandardCharsets.UTF_16BE)); // UTF-16BE, sem terminador
        }
    }
}
