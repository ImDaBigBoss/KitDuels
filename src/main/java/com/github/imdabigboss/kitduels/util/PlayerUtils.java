package com.github.imdabigboss.kitduels.util;

import com.github.imdabigboss.kitduels.KitDuels;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtils {
    public static OfflinePlayer getOfflinePlayer(String name) {
        if (KitDuels.getInstance().getConfig().contains("registeredPlayers." + name)) {
            return getOfflinePlayer(UUID.fromString(KitDuels.getInstance().getConfig().getString("registeredPlayers." + name)));
        } else {
            return null;
        }
    }

    public static OfflinePlayer getOfflinePlayer(UUID uuid) {
        return KitDuels.getInstance().getServer().getOfflinePlayer(uuid);
    }

    public static boolean registerPlayer(Player player) {
        if (KitDuels.getInstance().getConfig().contains("registeredPlayers." + player.getName())) {
            return false;
        } else {
            KitDuels.getInstance().getConfig().set("registeredPlayers." + player.getName(), player.getUniqueId().toString());
            KitDuels.getInstance().saveConfig();
            return true;
        }
    }

    public static boolean doesPlayerHaveTotem(Player player) {
        if (player.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) {
            return true;
        } else if (player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
            return true;
        } else {
            return false;
        }
    }
}
