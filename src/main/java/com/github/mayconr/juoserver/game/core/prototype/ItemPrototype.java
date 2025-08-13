package com.github.mayconr.juoserver.game.core.prototype;

import com.github.mayconr.juoserver.game.core.model.ItemType;
import lombok.Data;

@Data
public class ItemPrototype {
    private int itemId;
    private ItemType type;
    private int modelId;
    private String name;
    private boolean movable;
    private int hue;
    private boolean hidden;
    private ContainerTypeProtototype container;

    @Data
    public static class ContainerTypeProtototype {
        private int gumpId;
    }
}
