package com.github.mayconr.juoserver.game.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
public class UOMobile extends UOObject implements Container {
    private Direction direction;
    private int hue;
    private CharacterStatus status;
    private Notoriety notoriety;
    private boolean running;
    private UOContainer backpack;
    private Map<Layer, UOItem> equippedItems = new HashMap<>();

    public UOMobile(int serialId, int modelId, int x, int y, int z, String name, Direction direction, int hue, CharacterStatus status, Notoriety notoriety) {
        super(serialId, modelId, x, y, z, name);
        this.direction = direction;
        this.hue = hue;
        this.status = status;
        this.notoriety = notoriety;
    }

    public void equipItem(Layer layer, UOItem item) {
        equippedItems.put(layer, item);
    }

    public boolean isItemEquipped(UOItem item) {
        return equippedItems.containsValue(item);
    }
    public void unequipItem(UOItem unequippedItem) {
        Layer layer = null;
        for (Map.Entry<Layer, UOItem> entry : equippedItems.entrySet()) {
            if (Objects.equals(unequippedItem, entry.getValue())) {
                layer = entry.getKey();
            }
        }
        if (layer != null) {
            equippedItems.remove(layer);
        }
    }

    public void setBackpack(UOContainer backpack) {
        this.backpack = backpack;
        equippedItems.put(Layer.BACKPACK, backpack);
    }

    @Override
    public void addItemToContainer(UOItem item) {
        if (backpack == null) {
            throw new IllegalStateException("Backpack does not exist for mobile "+getName());
        }
        item.setContainer(backpack);
        backpack.addItemToContainer(item);
    }

    @Override
    public void removeItemFromContainer(UOItem item) {
        item.setContainer(null);
        backpack.removeItemFromContainer(item);
    }

    @Override
    public Collection<UOItem> getItemsInContainer() {
        return backpack.getItemsInContainer();
    }

    @Override
    public int getContainerGumpId() {
        return backpack.getContainerGumpId();
    }

    public void move(Direction direction) {
        this.direction = direction;
        setLocation(getX() + direction.getDx(), getY() + direction.getDy());
    }
}
