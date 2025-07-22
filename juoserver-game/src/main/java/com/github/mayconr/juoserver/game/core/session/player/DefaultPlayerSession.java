package com.github.mayconr.juoserver.game.core.session.player;

import com.github.mayconr.juoserver.game.core.model.*;
import com.github.mayconr.juoserver.game.packet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DefaultPlayerSession implements PlayerSession {

    private final UOMobile mobile;
    private final InitializationService initializationService;
    private final SpeechService speechService;
    private final MovementService movementService;
    private final ItemInteractionService itemInteractionService;
    private final DoubleClickService doubleClickService;
    private final MegaClilocService megaClilocService;
    private final TargetService targetService;

    private String clientVersion;


    @Override
    public UOMobile getMobile() {
        return mobile;
    }

    @Override
    public void initialize(String clientVersion) {
        this.clientVersion = clientVersion;
        initializationService.initialize(this, clientVersion);
    }

    @Override
    public void speech(UnicodeSpeachRequest request) {
        speechService.handleSpeech(request);
    }

    @Override
    public void move(MoveRequest moveRequest) {
        movementService.handleMove(moveRequest);
    }

    @Override
    public void move(Location location) {
        movementService.handleMove(location);
    }

    @Override
    public void showMegaCliloc(List<Integer> serialList) {
        megaClilocService.handleMegaCliloc(serialList);
    }

    @Override
    public void pickUpItem(PickUpItem pickedUpItem) {
        itemInteractionService.handlePickUpItem(pickedUpItem);
    }

    @Override
    public void dropItemOnTheGround(DropItem droppedItem) {
        itemInteractionService.handleDropItemOnTheGround(droppedItem);
    }

    @Override
    public void dropItemInContainer(DropItem droppedItem) {
        itemInteractionService.handleDropItemInContainer(droppedItem);
    }

    @Override
    public void doubleClick(DoubleClick doubleClick) {
        doubleClickService.handleDoubleClick(doubleClick);
    }

    @Override
    public void equipItem(EquipItem equipItem) {
        itemInteractionService.handleEquipItem(equipItem);
    }

    @Override
    public void openContainerInRange(Container container) {
        itemInteractionService.handleOpenContainer(container);
    }

    @Override
    public void sendTarget(CursorType type) {
        targetService.handleSendTarget(type);
    }

    @Override
    public void handleTarget(Target target) {
        targetService.handleTarget(target);
    }
}
