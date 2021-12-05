package com.github.imdabigboss.kitduels.nukkit.managers;

import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;

import com.github.imdabigboss.kitduels.nukkit.KitDuels;
import com.github.imdabigboss.kitduels.nukkit.util.InventorySerialization;

import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.Player;

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

        Map<Integer, Item> content;
        Item[] armor;

        try {
            content = plugin.getInventorySerialization().fromBase64(plugin.getKitsYML().getString(kitName + ".content"));
            armor = plugin.getInventorySerialization().itemStackArrayFromBase64(plugin.getKitsYML().getString(kitName + ".armor"));
        } catch (IOException e) {
            return false;
        }

        Player nkPlayer = plugin.getServer().getPlayer(player.getName());
        for (int i = 0; i < content.size(); i++) {
            nkPlayer.getInventory().setItem(i, content.get(i));
        }
        nkPlayer.getInventory().setArmorContents(armor);
        return true;
    }
}
