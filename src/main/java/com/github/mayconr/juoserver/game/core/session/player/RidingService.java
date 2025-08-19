package com.github.mayconr.juoserver.game.core.session.player;

import com.github.mayconr.juoserver.game.core.database.Database;
import com.github.mayconr.juoserver.game.core.model.Layer;
import com.github.mayconr.juoserver.game.core.model.UOPlayer;
import com.github.mayconr.juoserver.game.packet.DeleteObject;
import com.github.mayconr.juoserver.game.packet.EquipItem;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RidingService {

    private final UOPlayer player;
    private final ChannelGroup channelGroup;
    private final Database database;

    public void handleMount(int modelId) {
        final var item =  database.createItem("Horse", player);
        player.equipItem(Layer.MOUNT, item);
        channelGroup.writeAndFlush(new EquipItem(player, Layer.MOUNT, item)); // TODO filter by channels in range
    }

    public void handleUnmount() {
        final var mount = player.getEquippedItems().get(Layer.MOUNT);
        if (mount != null) {
            player.unequipItem(mount);
            channelGroup.writeAndFlush(new DeleteObject(mount)); // TODO filter by channels in range
        }
    }
}
