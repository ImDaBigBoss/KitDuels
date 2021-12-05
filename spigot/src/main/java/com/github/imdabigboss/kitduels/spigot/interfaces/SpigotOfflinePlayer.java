package com.github.imdabigboss.kitduels.spigot.interfaces;

import org.bukkit.OfflinePlayer;

public class SpigotOfflinePlayer implements com.github.imdabigboss.kitduels.common.interfaces.CommonOfflinePlayer {
    private OfflinePlayer player;

    public SpigotOfflinePlayer(OfflinePlayer player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return this.player.getName();
    }
}
