package com.github.mayconr.juoserver.game.core.session.player;

import com.github.mayconr.juoserver.game.core.database.Database;
import com.github.mayconr.juoserver.game.core.model.Layer;
import com.github.mayconr.juoserver.game.core.model.UOPlayer;
import com.github.mayconr.juoserver.game.packet.DeleteObject;
import com.github.mayconr.juoserver.game.packet.EquipItem;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RidingService {

    private final UOPlayer player;
    private final ChannelHandlerContext ctx;
    private final Database database;

    public void handleMount(int modelId) {
        final var item =  database.createItem("Horse", player);
        player.equipItem(Layer.MOUNT, item);
        ctx.write(new EquipItem(player, Layer.MOUNT, item));
        ctx.flush();
    }

    public void handleUnmount() {
        final var mount = player.getEquippedItems().get(Layer.MOUNT);
        if (mount != null) {
            player.unequipItem(mount);
            ctx.writeAndFlush(new DeleteObject(mount));
        }
    }
}
