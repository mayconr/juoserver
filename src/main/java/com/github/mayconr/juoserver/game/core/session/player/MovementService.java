package com.github.mayconr.juoserver.game.core.session.player;

import com.github.mayconr.juoserver.game.core.database.Database;
import com.github.mayconr.juoserver.game.core.database.MobileFilter;
import com.github.mayconr.juoserver.game.core.event.EventBus;
import com.github.mayconr.juoserver.game.core.event.MobileMove;
import com.github.mayconr.juoserver.game.core.model.Location;
import com.github.mayconr.juoserver.game.core.model.UOMobile;
import com.github.mayconr.juoserver.game.packet.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class MovementService {

    private final UOMobile mobile;
    private final EventBus eventBus;
    private final ChannelGroup channelGroup;
    private final ChannelHandlerContext ctx;
    private final Database database;

    public void handleMove(MoveRequest moveRequest) {
        final var direction = moveRequest.getDirection();

        if (mobile.getDirection().equals(direction)) {
            mobile.move(direction);
        }
        mobile.setRunning(moveRequest.isRunning());
        mobile.setDirection(direction);

        ctx.write(new MovementAck(moveRequest.getSequence(), mobile.getNotoriety()));
        database.getMobilesInRange(mobile, MobileFilter.ALL_VISIBLE)
                .filter(someone->!someone.equals(mobile)) // avoid unnecessary packet
                .forEach(someone->ctx.write(new DrawObject(someone)));
        database.getItemsInRange(mobile)
                .forEach(item->ctx.write(new ObjectInfo(item)));
        ctx.flush();
        channelGroup.writeAndFlush(new DrawObject(mobile), channel -> !channel.equals(ctx.channel())); // TODO only for close mobiles


        eventBus.publish(new MobileMove(mobile, direction));
    }

    public void handleMove(Location location) {
        mobile.setLocation(location.getX(), location.getY(), location.getZ());
        ctx.write(new DrawGamePlayer(mobile));
        database.getMobilesInRange(mobile, MobileFilter.ALL_VISIBLE)
                .filter(someone->!someone.equals(mobile)) // avoid unnecessary packet
                .forEach(someone->ctx.write(new DrawObject(someone)));
        ctx.flush();
        channelGroup.writeAndFlush(new DrawObject(mobile)); // TODO only for close mobiles
    }
}
