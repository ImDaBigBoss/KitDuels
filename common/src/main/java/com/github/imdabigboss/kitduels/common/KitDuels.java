package com.github.imdabigboss.kitduels.common;

import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.interfaces.Logger;
import com.github.imdabigboss.kitduels.common.managers.*;
import com.github.imdabigboss.kitduels.common.util.*;

import java.util.List;

public interface KitDuels {
    void startCountdownAndStartGame(String map);

    default boolean isPlayerInList(List<CommonPlayer> list, String name) {
        for (CommonPlayer player : list) {
            if (player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    default boolean removePlayerFromList(List<CommonPlayer> list, String name) {
        for (CommonPlayer player : list) {
            if (player.getName().equalsIgnoreCase(name)) {
                list.remove(player);
                return true;
            }
        }
        return false;
    }

    Logger getLog();

    YMLUtils getConfigYML();
    YMLUtils getMapsYML();
    YMLUtils getKitsYML();
    YMLUtils getStatsYML();
    YMLUtils getMessagesYML();
    boolean getHologramsEnabled();

    StatsManager getStatsManager();
    TextManager getTextManager();
    KitManager getKitManager();
    GameManager getGameManager();
    GUIManager getGUIManager();
    HologramManager getHologramManager();

    PlayerUtils getPlayerUtils();
    WorldUtils getWorldUtils();
    EntityUtils getEntityUtils();
    InventorySerialization getInventorySerialization();
}
