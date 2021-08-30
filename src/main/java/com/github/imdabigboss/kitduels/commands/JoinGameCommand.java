package com.github.imdabigboss.kitduels.commands;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.managers.MapManager;
import com.github.imdabigboss.kitduels.YMLUtils;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class JoinGameCommand implements CommandExecutor, TabExecutor {
    private KitDuels plugin;
    private YMLUtils mapsYML;

    public JoinGameCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
        this.mapsYML = KitDuels.getMapsYML();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }

        if (KitDuels.editModePlayers.containsKey((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "You are currently editing a map!");
        }
        if (KitDuels.playerMaps.containsKey((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "You are already in a game!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("random")) {
                String map = MapManager.getNextMap();
                if (map == "") {
                    sender.sendMessage(ChatColor.RED + "Either no slots or no maps are available!");
                } else {
                    MapManager.queuePlayerToMap(player, map);
                }
            }
        } else {
            String[] mapLayout = {
                    "    a    ",
                    "ggggggggg",
                    "ggggggggg",
                    "ggggggggg",
                    "b   h   f"
            };

            InventoryGui inventoryGui = new InventoryGui(KitDuels.getInstance(), player, "Map select", mapLayout);

            GuiElementGroup group = new GuiElementGroup('g');
            for (String map : KitDuels.enabledMaps.keySet()) {
                ItemStack item = new ItemStack(Material.GREEN_TERRACOTTA);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(map);
                List<String> lore = new ArrayList<>();
                lore.add(MapManager.getMapMaxPlayers(map) + " players");
                if (KitDuels.ongoingMaps.contains(map) || KitDuels.inUseMaps.contains(map)) {
                    lore.add("MAP IS IN USE!");
                    item.setType(Material.RED_TERRACOTTA);
                }
                meta.setLore(lore);
                item.setItemMeta(meta);

                group.addElement(new StaticGuiElement('e', item, click -> {
                    inventoryGui.playClickSound();
                    String mapName = click.getEvent().getCurrentItem().getItemMeta().getDisplayName();
                    tryMap(mapName, player);
                    inventoryGui.close();
                    return true;
                }));
            }

            ItemStack item = new ItemStack(Material.YELLOW_TERRACOTTA);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("Random map");
            item.setItemMeta(meta);

            group.addElement(new StaticGuiElement('e', item, click -> {
                inventoryGui.playClickSound();
                tryMap(MapManager.getNextMap(), player);
                inventoryGui.close();
                return true;
            }));
            inventoryGui.addElement(group);

            inventoryGui.addElement(new GuiPageElement('b', new ItemStack(Material.ARROW), GuiPageElement.PageAction.PREVIOUS, "Go to previous page (%prevpage%)"));
            inventoryGui.addElement(new GuiPageElement('f', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, "Go to next page (%nextpage%)"));

            inventoryGui.addElement(new StaticGuiElement('a', new ItemStack(Material.BOOK), "Map select"));
            inventoryGui.addElement(new StaticGuiElement('h', new ItemStack(Material.PAPER), click -> {
                inventoryGui.playClickSound();
                inventoryGui.close();
                return true;
            }, "Close"));
            inventoryGui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));

            inventoryGui.show(player, true);
        }

        return true;
    }

    private void tryMap(String map, Player player) {
        if (map == "") {
            player.sendMessage(ChatColor.RED + "Either no slots or no maps are available!");
        }

        if (KitDuels.enabledMaps.containsKey(map)) {
            if (KitDuels.enabledMaps.get(map).size() >= MapManager.getMapMaxPlayers(map)) {
                player.sendMessage(ChatColor.RED + "That map has no slots available!");
                return;
            }
        } else {
            player.sendMessage(ChatColor.RED + "That map does not exist!");
            return;
        }

        MapManager.queuePlayerToMap(player, map);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> cmds = new ArrayList<>();
        if (args.length == 1) {
            cmds.add("random");
        }
        return cmds;
    }
}
