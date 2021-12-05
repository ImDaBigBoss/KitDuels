package com.github.imdabigboss.kitduels.spigot.managers;

import com.github.imdabigboss.kitduels.spigot.KitDuels;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitManager implements com.github.imdabigboss.kitduels.common.managers.KitManager {
    private KitDuels plugin;

    private List<String> allKits = new ArrayList<>();
    private Map<String, String> playerKits = new HashMap<>();

    public KitManager(KitDuels plugin) {
        this.plugin = plugin;
    }

    @Override
    public void addKit(String kitName) {
        allKits.add(kitName);
    }

    @Override
    public void addKits(List<String> kits) {
        allKits.addAll(kits);
    }

    @Override
    public List<String> getKits() {
        return allKits;
    }

    @Override
    public void setPlayerKit(CommonPlayer player, String kitName) {
        playerKits.remove(player.getName());
        playerKits.put(player.getName(), kitName);
    }

    @Override
    public Map<String, String> getPlayerKits() {
        return playerKits;
    }

    @Override
    public boolean loadKitToPlayer(CommonPlayer player, String kitName) {
        player.clearInventory();

        Inventory content;
        ItemStack[] armor;

        try {
            content = plugin.getInventorySerialization().fromBase64(plugin.getKitsYML().getString(kitName + ".content"));
            armor = plugin.getInventorySerialization().itemStackArrayFromBase64(plugin.getKitsYML().getString(kitName + ".armor"));
        } catch (IOException e) {
            return false;
        }

        Player spigotPlayer = plugin.getServer().getPlayer(player.getName());
        spigotPlayer.getInventory().setContents(content.getContents());
        spigotPlayer.getInventory().setArmorContents(armor);
        return true;
    }
}
