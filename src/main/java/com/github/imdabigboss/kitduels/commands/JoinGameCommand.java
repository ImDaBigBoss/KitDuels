package com.github.imdabigboss.kitduels.commands;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.managers.MapManager;

import com.github.imdabigboss.kitduels.managers.TextManager;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;

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
    private TextManager textManager;

    public JoinGameCommand(KitDuels plugin) {
        super();
        this.textManager = KitDuels.getTextManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(textManager.get("general.errors.notPlayer"));
            return true;
        }

        if (KitDuels.editModePlayers.containsKey((Player) sender)) {
            sender.sendMessage(textManager.get("general.errors.editingMap"));
        }
        if (KitDuels.playerMaps.containsKey((Player) sender)) {
            sender.sendMessage(textManager.get("general.errors.alreadyInGame"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("random")) {
                String map = MapManager.getNextMap();
                if (map.equals("")) {
                    sender.sendMessage(textManager.get("general.errors.noMapsAvailable"));
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

            InventoryGui inventoryGui = new InventoryGui(KitDuels.getInstance(), player, textManager.get("ui.titles.mapSelect"), mapLayout);

            GuiElementGroup group = new GuiElementGroup('g');
            for (String map : KitDuels.enabledMaps.keySet()) {
                ItemStack item = new ItemStack(Material.GREEN_TERRACOTTA);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(map);
                List<String> lore = new ArrayList<>();
                lore.add(textManager.get("ui.maxPlayerNum", MapManager.getMapMaxPlayers(map)));
                if (KitDuels.ongoingMaps.contains(map) || KitDuels.inUseMaps.containsKey(map)) {
                    lore.add(textManager.get("ui.mapInUse"));
                    item.setType(Material.RED_TERRACOTTA);
                }
                lore.add(textManager.get("ui.playing", KitDuels.enabledMaps.get(map).size()));
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
            meta.setDisplayName(textManager.get("ui.randomMap"));
            item.setItemMeta(meta);

            group.addElement(new StaticGuiElement('e', item, click -> {
                inventoryGui.playClickSound();
                tryMap(MapManager.getNextMap(), player);
                inventoryGui.close();
                return true;
            }));
            inventoryGui.addElement(group);

            inventoryGui.addElement(new GuiPageElement('b', new ItemStack(Material.ARROW), GuiPageElement.PageAction.PREVIOUS, textManager.get("ui.buttons.previousPage")));
            inventoryGui.addElement(new GuiPageElement('f', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, textManager.get("ui.buttons.nextPage")));

            inventoryGui.addElement(new StaticGuiElement('a', new ItemStack(Material.BOOK), textManager.get("ui.title.mapSelect")));
            inventoryGui.addElement(new StaticGuiElement('h', new ItemStack(Material.PAPER), click -> {
                inventoryGui.playClickSound();
                inventoryGui.close();
                return true;
            }, textManager.get("ui.buttons.close")));
            inventoryGui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));

            inventoryGui.show(player, true);
        }

        return true;
    }

    private void tryMap(String map, Player player) {
        if (map.equals("")) {
            player.sendMessage(textManager.get("general.errors.noMapsAvailable"));
        }

        if (KitDuels.enabledMaps.containsKey(map)) {
            if (KitDuels.enabledMaps.get(map).size() >= MapManager.getMapMaxPlayers(map)) {
                player.sendMessage(textManager.get("general.errors.noMapSlotsAvailable"));
                return;
            }
        } else {
            player.sendMessage(textManager.get("general.errors.notAMap"));
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
