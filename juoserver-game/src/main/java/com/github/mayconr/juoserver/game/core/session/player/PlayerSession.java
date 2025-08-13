package com.github.mayconr.juoserver.game.core.session.player;

import com.github.mayconr.juoserver.game.core.model.*;
import com.github.mayconr.juoserver.game.packet.*;

import java.util.List;

public interface PlayerSession {

    UOMobile getPlayer();

    boolean isActive();

    void initialize(String clientVersion);

    void speech(UnicodeSpeachRequest request);

    void move(MoveRequest moveRequest);

    void showMegaCliloc(List<Integer> serialList);

    void pickUpItem(PickUpItem pickedUpItem);

    void dropItemOnTheGround(DropItem droppedItem);

    void dropItemInContainer(DropItem droppedItem);

    void doubleClick(DoubleClick doubleClick);

    void move(Location location);

    void equipItem(EquipItem equipItem);

    void openContainerInRange(Container container);

    void sendTarget(CursorType type);

    void handleTarget(Target target);

    void handleWarMode(WarModeType type);

    void attack(int opponentSerialId);
}
