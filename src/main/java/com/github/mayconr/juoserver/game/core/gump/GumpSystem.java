package com.github.mayconr.juoserver.game.core.gump;

import com.github.mayconr.juoserver.game.core.model.UOMobile;
import com.github.mayconr.juoserver.game.packet.GumpSelection;

import java.util.function.Consumer;

public interface GumpSystem {

    <T> void send(UOMobile mobile, DeclarativeGumpUI gumpUI, GumpHandler handler);

}
