package com.github.mayconr.juoserver.game.packet.handler;

import com.github.mayconr.juoserver.game.core.model.UOPlayer;
import com.github.mayconr.juoserver.game.core.session.player.PlayerSession;
import com.github.mayconr.juoserver.game.packet.SendGumpDialog;
import com.github.mayconr.juoserver.game.packet.UnicodeSpeachRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@ChannelHandler.Sharable
public
class UnicodeSpeachRequestHandler extends PlayerSessionChannelInboundHandler<UnicodeSpeachRequest> {
    @Override
    protected void channelRead0(PlayerSession session, ChannelHandlerContext ctx, UnicodeSpeachRequest msg) {
        if (msg.getText().equals(".teste")) {
            ctx.writeAndFlush(new SendGumpDialog((UOPlayer) session.getPlayer(), new Random().nextInt(), 100,100, "page 0\r\n" +
                    "resizepic 100 10 5120 350 355\r\n" +
                    "text 180 25 995 0\r\n", List.of("test")));
            //X Y GUMPID W H
        }
        session.speech(msg);
    }
}
