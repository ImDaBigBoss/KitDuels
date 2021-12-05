package com.github.imdabigboss.kitduels.spigot.util;

import com.github.imdabigboss.kitduels.common.interfaces.CommonOfflinePlayer;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.spigot.KitDuels;

import com.github.imdabigboss.kitduels.spigot.interfaces.SpigotOfflinePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class PlayerUtils implements com.github.imdabigboss.kitduels.common.util.PlayerUtils {
    private KitDuels plugin;

    public PlayerUtils(KitDuels plugin) {
        this.plugin = plugin;
    }

    public CommonOfflinePlayer getOfflinePlayer(String name) {
        if (plugin.getConfigYML().contains("registeredPlayers." + name)) {
            return getOfflinePlayer(UUID.fromString(plugin.getConfigYML().getString("registeredPlayers." + name)));
        } else {
            return null;
        }
    }

    public CommonOfflinePlayer getOfflinePlayer(UUID uuid) {
        return new SpigotOfflinePlayer(plugin.getServer().getOfflinePlayer(uuid));
    }

    public boolean registerPlayer(CommonPlayer player) {
        if (plugin.getConfigYML().contains("registeredPlayers." + player.getName())) {
            return false;
        } else {
            plugin.getConfigYML().set("registeredPlayers." + player.getName(), player.getUUID().toString());
            plugin.getConfigYML().saveConfig();
            return true;
        }
    }

    public boolean doesPlayerHaveTotem(CommonPlayer player) {
        Player spigotPlayer = plugin.getServer().getPlayer(player.getName());
        if (spigotPlayer.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) {
            return true;
        } else if (spigotPlayer.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void givePlayerLeaveGameItem(CommonPlayer player) {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plugin.getTextManager().get("items.leaveGame"));
        item.setItemMeta(meta);

        Player spigotPlayer = plugin.getServer().getPlayer(player.getName());
        spigotPlayer.getInventory().setItem(8, item);
    }

    @Override
    public void givePlayerKitSelectItem(CommonPlayer player) {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plugin.getTextManager().get("items.kitSelect"));
        item.setItemMeta(meta);

        Player spigotPlayer = plugin.getServer().getPlayer(player.getName());
        spigotPlayer.getInventory().setItem(0, item);
    }
}
