package com.github.imdabigboss.kitduels.common.managers;

import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;

import java.util.List;
import java.util.Map;

public interface KitManager {
    void addKit(String kitName);
    void addKits(List<String> kits);
    List<String> getKits();

    void setPlayerKit(CommonPlayer player, String kitName);
    Map<String, String> getPlayerKits();

    boolean loadKitToPlayer(CommonPlayer player, String kitName);
}
