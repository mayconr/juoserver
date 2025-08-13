package com.github.mayconr.juoserver.game.packet.handler;

import com.github.mayconr.juoserver.game.core.session.player.PlayerSession;
import com.github.mayconr.juoserver.game.packet.EquipItem;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public class WearItemHandler extends PlayerSessionChannelInboundHandler<EquipItem> {
    @Override
    protected void channelRead0(PlayerSession session, ChannelHandlerContext ctx, EquipItem msg) {
        session.equipItem(msg);
    }
}
