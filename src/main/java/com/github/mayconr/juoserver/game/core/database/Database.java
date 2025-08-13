package com.github.mayconr.juoserver.game.core.database;

import com.github.mayconr.juoserver.game.core.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface Database {

    Optional<UOAccount> getAccount(String username, String password);

    Optional<UOAccount> getAccount(String accountId);

    List<UOPlayer> getPlayersByAccount(UOAccount UOAccount);

    Optional<UOMobile> getMobileSerialId(int serialId);
    Optional<UOItem> getItemBySerialId(int serialId);
    Optional<Container> getContainerById(int serialId);
    List<UOCity> getCities();

    Stream<UOMobile> getMobilesInRange(Location location, MobileFilter filter);

    void deletePlayer(UOPlayer character);

    UOPlayer createPlayer(PlayerDetails details);

    UONpc createNpcAtLocation(int npcId, Location location);

    UOItem createItemAtLocation(int itemId, Location location);

    UOItem createItemAtLocation(String name, Location location);

    void dropItemOnTheGround(UOItem item);

    void removeItemFromTheGround(UOItem item);

    List<UOItem> getItemsInRange(Location location);

    void deleteItem(UOItem item);

    boolean isMobile(int serialId);


}
