package com.github.imdabigboss.kitduels.nukkit.interfaces;

import cn.nukkit.IPlayer;

public class NukkitOfflinePlayer implements com.github.imdabigboss.kitduels.common.interfaces.CommonOfflinePlayer {
    private IPlayer player;

    public NukkitOfflinePlayer(IPlayer player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return this.player.getName();
    }
}
