package com.github.imdabigboss.kitduels.nukkit.util;

import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;

import com.github.imdabigboss.kitduels.nukkit.KitDuels;
import com.github.imdabigboss.kitduels.nukkit.interfaces.NukkitOfflinePlayer;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

import java.util.UUID;

public class PlayerUtils implements com.github.imdabigboss.kitduels.common.util.PlayerUtils {
    private KitDuels plugin;

    public PlayerUtils(KitDuels plugin) {
        this.plugin = plugin;
    }

    @Override
    public NukkitOfflinePlayer getOfflinePlayer(String name) {
        return new NukkitOfflinePlayer(this.plugin.getServer().getOfflinePlayer(name));
    }

    @Override
    public NukkitOfflinePlayer getOfflinePlayer(UUID uuid) {
        return new NukkitOfflinePlayer(this.plugin.getServer().getOfflinePlayer(uuid));
    }

    @Override
    public boolean registerPlayer(CommonPlayer player) {
        if (this.plugin.getConfigYML().contains("registeredPlayers." + player.getName())) {
            return false;
        } else {
            plugin.getConfigYML().set("registeredPlayers." + player.getName(), player.getUUID().toString());
            plugin.getConfigYML().saveConfig();
            return true;
        }
    }

    @Override
    public boolean doesPlayerHaveTotem(CommonPlayer inplayer) {
        Player player = this.plugin.getServer().getPlayer(inplayer.getName());
        if (player.getInventory().getItemInHand().getId() == Item.TOTEM) {
            return true;
        } else if (player.getOffhandInventory().getItem(0).getId() == Item.TOTEM) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void givePlayerLeaveGameItem(CommonPlayer player) {
        Item item = new Item(Item.DYE);
        item.setCustomName(plugin.getTextManager().get("items.leaveGame"));

        Player spigotPlayer = plugin.getServer().getPlayer(player.getName());
        spigotPlayer.getInventory().setItem(8, item);
    }

    @Override
    public void givePlayerKitSelectItem(CommonPlayer player) {
        Item item = new Item(Item.CHEST);
        item.setCustomName(plugin.getTextManager().get("items.kitSelect"));

        Player spigotPlayer = plugin.getServer().getPlayer(player.getName());
        spigotPlayer.getInventory().setItem(0, item);
    }
}
