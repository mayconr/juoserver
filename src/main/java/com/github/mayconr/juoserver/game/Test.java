package com.github.mayconr.juoserver.game;

import com.github.mayconr.juoserver.game.core.event.*;
import com.github.mayconr.juoserver.game.core.gump.DeclarativeGumpUI;
import com.github.mayconr.juoserver.game.core.gump.GumpSystem;
import com.github.mayconr.juoserver.game.core.model.CursorType;
import com.github.mayconr.juoserver.game.core.model.PointInTheWorld;
import com.github.mayconr.juoserver.game.core.session.game.GameSession;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.github.mayconr.juoserver.game.core.gump.DeclarativeGumpUI.*;
import static com.github.mayconr.juoserver.game.core.gump.DeclarativeGumpUI.Label;

@Component
@Slf4j
public class Test {

    @Autowired
    private EventBus eventBus;
    @Autowired
    private GameSession gameSession;
    @Autowired
    private GumpSystem gumpSystem;

    @PostConstruct
    public void setUp() {
        //eventBus.register(MobileMove.class, this::onMove);
        eventBus.register(MobileSpeech.class, this::speech);
        eventBus.register(Prompt.class, this::createNpc, prompt -> prompt.name().equalsIgnoreCase("createnpc"));
        eventBus.register(Prompt.class, this::createItem, prompt -> prompt.name().equalsIgnoreCase("createitem"));
        eventBus.register(Prompt.class, this::move, prompt -> prompt.name().equalsIgnoreCase("goto"));
        eventBus.register(Prompt.class, this::where, prompt -> prompt.name().equalsIgnoreCase("where"));
        eventBus.register(Prompt.class, this::select, prompt -> prompt.name().equalsIgnoreCase("target"));
        eventBus.register(Prompt.class, this::mount, prompt->prompt.name().equalsIgnoreCase("mount"));
        eventBus.register(Prompt.class, this::unmound, prompt->prompt.name().equalsIgnoreCase("unmount"));
        eventBus.register(Prompt.class, this::sendGump, prompt->prompt.name().equalsIgnoreCase("gump"));
        eventBus.register(SelectedObject.class, this::objectSelected);
        eventBus.register(SelectedStatics.class, this::staticSelected);
    }

    public HandlerResult sendGump(Prompt prompt) {
        final var gump = new DeclarativeGumpUI(Page(1,
                Panel(300,200,5100,0,
                    Form(
                        Field(Label("Teste"), Button("teste", 1)),
                        Field(Label("Teste"), Button("teste", 2)),
                        InlineField(Label("Teste"), Button("teste", 3)),
                        Field(Label("Nome"), TextField(0, 200)),
                        Divider(100),
                        ItemIcon(14088, 0),
                        InlineField(Label("AGe"), TextField(0, 200)),
                        InlineField(Label("Teste Maycon"), TextField(0, 200)),
                        InlineField(Radio(210, 211, 22),Label("Teste Maycon"))
                    )
                )
        ));

        gumpSystem.send(prompt.mobile(), gump, (ctx,selection)->{
            System.out.println("Recebido "+selection.getText(0));
        });
        return HandlerResult.CONTINUE;
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
        gameSession.createNpcSession(prompt.arguments()[0], prompt.mobile());
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

    public HandlerResult mount(Prompt prompt) {
        gameSession.getPlayerSession(prompt.mobile()).mount(prompt.arguments()[0]);
        return HandlerResult.CONTINUE;
    }

    public HandlerResult unmound(Prompt prompt) {
        gameSession.getPlayerSession(prompt.mobile()).unmount();
        return HandlerResult.CONTINUE;
    }
}
