package com.github.imdabigboss.kitduels.managers;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.util.InventorySerialization;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;

public class GameManager {
    public static void sendToSpawn(Player player) {
        if (KitDuels.getInstance().getConfig().contains("lobbySpawn")) {
            Location location = (Location) KitDuels.getInstance().getConfig().get("lobbySpawn");
            player.teleport(location);
        } else {
            Location location = KitDuels.getInstance().getServer().getWorld("world").getSpawnLocation();
            player.teleport(location);
        }

        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
    }

    public static boolean loadKitToPlayer(Player player, String kitName) {
        player.getInventory().clear();

        Inventory content;
        ItemStack[] armor;

        try {
            content = InventorySerialization.fromBase64(KitDuels.getKitsYML().getConfig().getString(kitName + ".content"));
            armor = InventorySerialization.itemStackArrayFromBase64(KitDuels.getKitsYML().getConfig().getString(kitName + ".armor"));
        } catch (IOException e) {
            return false;
        }

        player.getInventory().setContents(content.getContents());
        player.getInventory().setArmorContents(armor);
        return true;
    }

    public static void openKitSelectGUIPlayer(Player player) {
        String[] kitLayout = {
                "    a    ",
                "ggggggggg",
                "ggggggggg",
                "ggggggggg",
                "b   h   f"
        };

        InventoryGui inventoryGui = new InventoryGui(KitDuels.getInstance(), player, KitDuels.getTextManager().get("ui.titles.kitSelect"), kitLayout);

        GuiElementGroup group = new GuiElementGroup('g');
        for (String kit : KitDuels.allKits) {
            ItemStack item = new ItemStack(Material.ENDER_CHEST);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(kit);
            item.setItemMeta(meta);

            group.addElement(new StaticGuiElement('e', item, click -> {
                inventoryGui.playClickSound();
                String kitName = click.getEvent().getCurrentItem().getItemMeta().getDisplayName();
                KitDuels.playerKits.remove(player);
                KitDuels.playerKits.put(player, kitName);
                player.sendMessage(KitDuels.getTextManager().get("general.info.kitSelected", kitName));
                inventoryGui.close();
                return true;
            }));
        }

        ItemStack item = new ItemStack(Material.CYAN_SHULKER_BOX);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(KitDuels.getTextManager().get("ui.randomKit"));
        item.setItemMeta(meta);

        group.addElement(new StaticGuiElement('e', item, click -> {
            inventoryGui.playClickSound();
            KitDuels.playerKits.remove(player);
            player.sendMessage(KitDuels.getTextManager().get("general.info.kitRandomSelected"));
            inventoryGui.close();
            return true;
        }));
        inventoryGui.addElement(group);

        inventoryGui.addElement(new GuiPageElement('b', new ItemStack(Material.ARROW), GuiPageElement.PageAction.PREVIOUS, KitDuels.getTextManager().get("ui.buttons.previousPage")));
        inventoryGui.addElement(new GuiPageElement('f', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, KitDuels.getTextManager().get("ui.buttons.nextPage")));

        inventoryGui.addElement(new StaticGuiElement('a', new ItemStack(Material.CHEST), KitDuels.getTextManager().get("ui.titles.kitSelect")));
        inventoryGui.addElement(new StaticGuiElement('h', new ItemStack(Material.PAPER), click -> {
            inventoryGui.playClickSound();
            inventoryGui.close();
            return true;
        }, KitDuels.getTextManager().get("ui.buttons.close")));
        inventoryGui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));

        inventoryGui.show(player, true);
    }
}
