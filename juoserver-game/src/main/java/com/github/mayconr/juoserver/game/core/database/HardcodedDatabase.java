package com.github.mayconr.juoserver.game.core.database;

import com.github.mayconr.juoserver.game.core.model.*;
import com.github.mayconr.juoserver.game.core.prototype.ItemPrototype;
import com.github.mayconr.juoserver.game.core.prototype.PrototypeManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HardcodedDatabase implements Database {

    private static final int MOBILES_MAX_SERIAL_ID = 0x3FFFFFFF;

    public static final int OBJECTS_MIN_SERIAL_ID = MOBILES_MAX_SERIAL_ID + 1;
    private static final int OBJECTS_MAX_SERIAL_ID = 0x7FFFFFFF;
    private static final List<UOAccount> ACCOUNTS = new ArrayList<>();
    private static final List<UOMobile> MOBILES = new ArrayList<>();
    private static final List<UOItem> OBJECTS = new ArrayList<>();
    private static final AtomicInteger MOBILE_COUNTER = new AtomicInteger(1);
    private static final AtomicInteger OBJECT_COUNTER = new AtomicInteger(OBJECTS_MIN_SERIAL_ID);

    private final PrototypeManager prototypeManager;

    public HardcodedDatabase(PrototypeManager prototypeManager) {
        this.prototypeManager = prototypeManager;
        createData();
    }

    private void createData() {
        final var admin = new UOAccount(UUID.randomUUID().toString(), "admin", "admin");
        ACCOUNTS.add(admin);

        final var elrond = new UOPlayer(MOBILE_COUNTER.getAndIncrement(), 0x190, 2514,550,0, "Elrond", Direction.NORTH,0x83EA,CharacterStatus.NORMAL,Notoriety.CRIMINAL,admin.getId(),"admin");
        final var elrondBackpack = new UOContainer(OBJECT_COUNTER.getAndIncrement(), prototypeManager.getItemById(4).orElseThrow(), new PointInTheWorld(0,0,0));
        elrond.setBackpack(elrondBackpack);
        equipItem(elrond, Layer.OUTER_TORSO, "robe2");
        OBJECTS.add(elrondBackpack);
        MOBILES.add(elrond);

        final var user = new UOAccount(UUID.randomUUID().toString(), "user", "user");
        ACCOUNTS.add(user);

        final var legolaz = new UOPlayer(MOBILE_COUNTER.getAndIncrement(), 0x190, 2514,550,0, "Legolaz", Direction.NORTH,0x83EA,CharacterStatus.NORMAL,Notoriety.CRIMINAL,user.getId(),"admin");
        legolaz.setStrength(10);
        legolaz.setDexterity(20);
        legolaz.setMana(11);
        legolaz.setStamina(100);
        legolaz.setMaxStamina(120);
        final var backpack = new UOContainer(OBJECT_COUNTER.getAndIncrement(), prototypeManager.getItemById(4).orElseThrow(), new PointInTheWorld(0,0,0));
        legolaz.setBackpack(backpack);
        equipItem(legolaz, Layer.OUTER_TORSO, "robe");
        OBJECTS.add(backpack);
        MOBILES.add(legolaz);
    }

    private void equipItem(UOMobile mobile, Layer layer, String name) {
        final var item = new UOItem(OBJECT_COUNTER.getAndIncrement(), prototypeManager.getItemByName(name).orElseThrow(), new PointInTheWorld(0,0,0));
        OBJECTS.add(item);
        mobile.equipItem(layer, item);
    }

    @Override
    public Optional<UOAccount> getAccount(String username, String password) {
        return ACCOUNTS.stream().filter(acct->acct.getUsername().equals(username) && acct.getPassword().equals(password))
                .findFirst();
    }

    @Override
    public Optional<UOAccount> getAccount(String accountId) {
        return ACCOUNTS.stream().filter(acct->acct.getId().equals(accountId)).findFirst();
    }

    @Override
    public List<UOPlayer> getPlayersByAccount(UOAccount uoAccount) {
        return MOBILES.stream()
                .filter(UOPlayer.class::isInstance)
                .map(UOPlayer.class::cast)
                .filter(character -> character.getAccountId().equals(uoAccount.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UOMobile> getMobileSerialId(int serialId) {
        return MOBILES.stream()
                .filter(character -> character.getSerialId() == serialId)
                .findFirst();
    }

    @Override
    public Optional<UOItem> getItemBySerialId(int serialId) {
        return OBJECTS.stream().filter(item -> item.getSerialId() == serialId).findFirst();
    }

    @Override
    public Optional<Container> getContainerById(int serialId) {
        if (isMobile(serialId)) {
            return getMobileSerialId(serialId)
                    .map(mobile-> mobile);
        }
        return getItemBySerialId(serialId)
                .filter(item->ItemType.CONTAINER.equals(item.getType()))
                .map(item->(Container) item);
    }

    @Override
    public List<UOCity> getCities() {
        return List.of(new UOCity("Vesper", "Vesper", new PointInTheWorld(2893, 686, 0)),
                new UOCity("Britain", "Britannia", new PointInTheWorld(1478,1711,0)));
    }

    @Override
    public Stream<UOMobile> getMobilesInRange(Location location, MobileFilter filter) {
        return MOBILES.stream()
                .filter(mobile-> switch (filter) {
                    case ALL -> true;
                    case ALL_VISIBLE -> (mobile instanceof UOPlayer player && player.isConnected()) || mobile instanceof UONpc;
                });
    }

    @Override
    public void deletePlayer(UOPlayer player) {
        MOBILES.remove(player);
    }

    @Override
    public UOPlayer createPlayer(PlayerDetails details) {
        final var mobile = new UOPlayer(MOBILE_COUNTER.getAndIncrement(), 0x190, 2514,550,0, details.name(), Direction.NORTH, 0x83EA, CharacterStatus.NORMAL, Notoriety.INNOCENT, details.account().getId(), "temp");
        MOBILES.add(mobile);
        return mobile;
    }

    @Override
    public UONpc createNpcAtLocation(int npcId, Location location) {
        final var npcPrototype = prototypeManager.getNpcById(npcId)
                .orElseThrow(()->new NpcPrototypeNotFoundException(npcId));
        final var npc = new UONpc(MOBILE_COUNTER.getAndIncrement(), npcPrototype, location);
        for (Map.Entry<Layer, String> entry : npcPrototype.getEquippedItems().entrySet()) {
            npc.equipItem(entry.getKey(), createItemAtLocation(entry.getValue(), location));
        }
        MOBILES.add(npc);
        return npc;
    }

    @Override
    public UOItem createItemAtLocation(int itemId, Location location) {
        final var prototype = prototypeManager.getItemById(itemId)
                .orElseThrow(()->new ItemPrototypeNotFoundException(itemId));
        return createItemByPrototype(prototype, location);
    }

    @Override
    public UOItem createItemAtLocation(String name, Location location) {
        final var prototype = prototypeManager.getItemByName(name)
                .orElseThrow(()->new ItemPrototypeNotFoundException(name));
        return createItemByPrototype(prototype, location);
    }

    private UOItem createItemByPrototype(ItemPrototype prototype, Location location) {
        UOItem item;
        if (ItemType.CONTAINER.equals(prototype.getType())) {
            item = new UOContainer(OBJECT_COUNTER.getAndIncrement(), prototype, location);
        } else {
            item = new UOItem(OBJECT_COUNTER.getAndIncrement(), prototype, location);
        }
        OBJECTS.add(item);
        return item;
    }

    @Override
    public boolean isMobile(int serialId) {
        return serialId <= MOBILES_MAX_SERIAL_ID;
    }

    @Override
    public void deleteItem(UOItem item) {
        OBJECTS.remove(item);
    }
}
