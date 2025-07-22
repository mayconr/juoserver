package com.github.mayconr.juoserver.game.core.prototype;

import com.github.mayconr.juoserver.game.core.model.Layer;
import com.github.mayconr.juoserver.game.core.model.Notoriety;
import com.github.mayconr.juoserver.game.core.model.NpcType;
import lombok.Data;

import java.util.Map;

@Data
public class NpcPrototype {
    private int npcId;
    private NpcType type;
    private int modelId;
    private String name;
    private Notoriety notoriety;
    private int hue;
    private int speechFont;
    private int speechHue;
    private Map<Layer, String> equippedItems;

}
