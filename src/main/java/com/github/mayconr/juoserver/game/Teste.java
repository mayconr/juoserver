package com.github.mayconr.juoserver.game;

import com.github.mayconr.juoserver.game.core.event.*;
import com.github.mayconr.juoserver.game.core.model.PointInTheWorld;
import com.github.mayconr.juoserver.game.core.session.game.GameSession;
import com.github.mayconr.juoserver.game.core.model.CursorType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Teste {

    @Autowired
    private EventBus eventBus;
    @Autowired
    private GameSession gameSession;

    @PostConstruct
    public void setUp() {
        //eventBus.register(MobileMove.class, this::onMove);
        eventBus.register(MobileSpeech.class, this::speech);
        eventBus.register(Prompt.class, this::createNpc, prompt -> prompt.name().equalsIgnoreCase("createnpc"));
        eventBus.register(Prompt.class, this::createItem, prompt -> prompt.name().equalsIgnoreCase("createitem"));
        eventBus.register(Prompt.class, this::move, prompt -> prompt.name().equalsIgnoreCase("goto"));
        eventBus.register(Prompt.class, this::where, prompt -> prompt.name().equalsIgnoreCase("where"));
        eventBus.register(Prompt.class, this::select, prompt -> prompt.name().equalsIgnoreCase("target"));
        eventBus.register(SelectedObject.class, this::objectSelected);
        eventBus.register(SelectedStatics.class, this::staticSelected);
    }

    public HandlerResult createItem(Prompt prompt) {
        gameSession.createItemAtLocation(prompt.arguments()[0], prompt.mobile());
        return HandlerResult.CONTINUE;
    }

    public HandlerResult onMove(MobileMove event) {
        System.out.println(event.mobile()+" andou");
        return HandlerResult.CONTINUE;
    }

    public HandlerResult speech(MobileSpeech spoke) {
        System.out.println("falou "+spoke.message());
        return HandlerResult.CONTINUE;
    }

    public HandlerResult createNpc(Prompt prompt) {
        gameSession.createNpcSession(Integer.parseInt(prompt.arguments()[0]), prompt.mobile());
        return HandlerResult.CONTINUE;
    }

    public HandlerResult move(Prompt prompt) {
        gameSession.getPlayerSession(prompt.mobile()).move(new PointInTheWorld(2516, 555, 0));
        return HandlerResult.CONTINUE;
    }

    public HandlerResult where(Prompt prompt) {
        log.info("Estou em x={}, y={}, z={}", prompt.mobile().getX(), prompt.mobile().getY(), prompt.mobile().getZ());
        return HandlerResult.CONTINUE;
    }

    public HandlerResult select(Prompt prompt) {
        final var session = gameSession.getPlayerSession(prompt.mobile());
        session.sendTarget(CursorType.HELPFUL);
        return HandlerResult.CONTINUE;
    }

    public HandlerResult objectSelected(SelectedObject selectedObject) {
        System.out.println("Selecinou "+selectedObject);
        return HandlerResult.CONTINUE;
    }

    public HandlerResult staticSelected(SelectedStatics statics) {
        System.out.println("Selecinou statics "+statics);
        return HandlerResult.CONTINUE;
    }

    public void updateStatus(Prompt prompt) {

    }
}
