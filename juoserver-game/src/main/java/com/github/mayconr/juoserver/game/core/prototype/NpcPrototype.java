package com.github.mayconr.juoserver.game.core.prototype;

import com.github.mayconr.juoserver.game.core.model.*;
import lombok.Data;

import java.util.Map;

@Data
public class NpcPrototype {
    private int npcId;
    private NpcType type;
    private int modelId;
    private String name;
    private Notoriety notoriety;
    private Race race;
    private Gender gender;
    private int hue;
    private int speechFont;
    private int speechHue;
    private String ai;
    private Map<Layer, String> equippedItems;

}
