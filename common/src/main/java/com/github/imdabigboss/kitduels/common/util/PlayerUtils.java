package com.github.imdabigboss.kitduels.common.util;

import com.github.imdabigboss.kitduels.common.interfaces.CommonOfflinePlayer;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;

import java.util.UUID;

public interface PlayerUtils {
    CommonOfflinePlayer getOfflinePlayer(String name);
    CommonOfflinePlayer getOfflinePlayer(UUID uuid);

    boolean registerPlayer(CommonPlayer player);
    boolean doesPlayerHaveTotem(CommonPlayer player);

    void givePlayerLeaveGameItem(CommonPlayer player);
    void givePlayerKitSelectItem(CommonPlayer player);
}
