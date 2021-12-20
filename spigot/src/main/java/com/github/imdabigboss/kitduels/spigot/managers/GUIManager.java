package com.github.imdabigboss.kitduels.spigot.managers;

import com.github.imdabigboss.kitduels.spigot.KitDuels;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.ArrayList;
import java.util.List;

public class GUIManager implements com.github.imdabigboss.kitduels.common.managers.GUIManager {
    private KitDuels plugin;

    public GUIManager(KitDuels plugin) {
        this.plugin = plugin;
    }

    @Override
    public void showKitSelector(CommonPlayer player) {
        Player spigotPlayer = plugin.getServer().getPlayer(player.getName());

        if (plugin.isFloodgateEnabled()) {
            if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUUID())) {
                FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUUID());
                showKitSelectorBedrock(floodgatePlayer, spigotPlayer);
                return;
            }
        }

        showKitSelectorJava(spigotPlayer);
    }

    @Override
    public void showMapSelector(CommonPlayer player, boolean byKit) {
        if (plugin.isFloodgateEnabled()) {
            if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUUID())) {
                FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUUID());
                showMapSelectorBedrock(floodgatePlayer, player, byKit);
                return;
            }
        }

        showMapSelectorJava(player, byKit);
    }

    private String randomIcon = "textures/ui/refresh.png";
    private String kitIcon = "textures/ui/new_offer_symbol.png";
    private String mapIcon = "textures/ui/new_offer_symbol.png";
    private String inUseIcon = "textures/ui/accessibility_glyph_color.png";


    private void showKitSelectorJava(Player player) {
        String[] kitLayout = {
                "    a    ",
                "ggggggggg",
                "ggggggggg",
                "ggggggggg",
                "b   h   f"
        };

        Player spigotPlayer = plugin.getServer().getPlayer(player.getName());

        InventoryGui inventoryGui = new InventoryGui(plugin, spigotPlayer, plugin.getTextManager().get("ui.titles.kitSelect"), kitLayout);

        GuiElementGroup group = new GuiElementGroup('g');
        for (String kit : plugin.getKitManager().getKits()) {
            ItemStack item = new ItemStack(Material.ENDER_CHEST);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(kit);
            item.setItemMeta(meta);

            group.addElement(new StaticGuiElement('e', item, click -> {
                inventoryGui.playClickSound();
                String kitName = click.getEvent().getCurrentItem().getItemMeta().getDisplayName();
                plugin.getKitManager().getPlayerKits().remove(player.getName());
                plugin.getKitManager().getPlayerKits().put(player.getName(), kitName);
                player.sendMessage(plugin.getTextManager().get("general.info.kitSelected", kitName));
                inventoryGui.close();
                return true;
            }));
        }

        ItemStack item = new ItemStack(Material.CYAN_SHULKER_BOX);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plugin.getTextManager().get("ui.randomKit"));
        item.setItemMeta(meta);

        group.addElement(new StaticGuiElement('e', item, click -> {
            inventoryGui.playClickSound();
            plugin.getKitManager().getPlayerKits().remove(player.getName());
            player.sendMessage(plugin.getTextManager().get("general.info.kitRandomSelected"));
            inventoryGui.close();
            return true;
        }));
        inventoryGui.addElement(group);

        inventoryGui.addElement(new GuiPageElement('b', new ItemStack(Material.ARROW), GuiPageElement.PageAction.PREVIOUS, plugin.getTextManager().get("ui.buttons.previousPage")));
        inventoryGui.addElement(new GuiPageElement('f', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, plugin.getTextManager().get("ui.buttons.nextPage")));

        inventoryGui.addElement(new StaticGuiElement('a', new ItemStack(Material.CHEST), plugin.getTextManager().get("ui.titles.kitSelect")));
        inventoryGui.addElement(new StaticGuiElement('h', new ItemStack(Material.PAPER), click -> {
            inventoryGui.playClickSound();
            inventoryGui.close();
            return true;
        }, plugin.getTextManager().get("ui.buttons.close")));
        inventoryGui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));

        inventoryGui.show(plugin.getServer().getPlayer(player.getName()), true);
    }

    private void showKitSelectorBedrock(FloodgatePlayer fgPlayer, Player player) {
        Runnable runnable = () -> {
            SimpleForm.Builder outform = SimpleForm.builder();
            outform = outform.title(plugin.getTextManager().get("ui.titles.kitSelect"));
            outform = outform.button(plugin.getTextManager().get("ui.randomKit"), FormImage.Type.PATH, randomIcon);

            for (String kit : plugin.getKitManager().getKits()) {
                outform = outform.button(kit, FormImage.Type.PATH, kitIcon);
            }

            outform = outform.responseHandler((form, responseData) -> {
                SimpleFormResponse response = form.parseResponse(responseData);
                if (!response.isCorrect()) {
                    // player closed the form or returned invalid info (see FormResponse)
                    return;
                }

                int id = response.getClickedButtonId();
                if (id == 0) {
                    plugin.getKitManager().getPlayerKits().remove(player.getName());
                    player.sendMessage(plugin.getTextManager().get("general.info.kitRandomSelected"));
                } else {
                    String kitName = plugin.getKitManager().getKits().get(id - 1);
                    plugin.getKitManager().getPlayerKits().remove(player.getName());
                    plugin.getKitManager().getPlayerKits().put(player.getName(), kitName);
                    player.sendMessage(plugin.getTextManager().get("general.info.kitSelected", kitName));
                }
            });

            fgPlayer.sendForm(outform.build());
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    private void showMapSelectorJava(CommonPlayer player, boolean byKit) {
        String[] mapLayout = {
                "    a    ",
                "ggggggggg",
                "ggggggggg",
                "ggggggggg",
                "b   h   f"
        };

        Player spigotPlayer = plugin.getServer().getPlayer(player.getName());

        InventoryGui inventoryGui = new InventoryGui(plugin, spigotPlayer, plugin.getTextManager().get("ui.titles.mapSelect"), mapLayout);

        GuiElementGroup group = new GuiElementGroup('g');
        if (byKit) {
            for (String map : plugin.getGameManager().enabledMaps.keySet()) {
                if (!plugin.getGameManager().allMaps.get(map).equalsIgnoreCase("")) {
                    ItemStack item = new ItemStack(Material.GREEN_TERRACOTTA);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(plugin.getGameManager().allMaps.get(map));
                    List<String> lore = new ArrayList<>();
                    lore.add(plugin.getTextManager().get("ui.mapName", map));
                    lore.add(plugin.getTextManager().get("ui.maxPlayerNum", plugin.getGameManager().getMapMaxPlayers(map)));
                    if (plugin.getGameManager().ongoingMaps.contains(map) || plugin.getGameManager().inUseMaps.containsKey(map)) {
                        lore.add(plugin.getTextManager().get("ui.mapInUse"));
                        item.setType(Material.RED_TERRACOTTA);
                    }
                    lore.add(plugin.getTextManager().get("ui.playing", plugin.getGameManager().enabledMaps.get(map).size()));
                    meta.setLore(lore);
                    item.setItemMeta(meta);

                    group.addElement(new StaticGuiElement('e', item, click -> {
                        inventoryGui.playClickSound();
                        String mapName = click.getEvent().getCurrentItem().getItemMeta().getLore().get(0).replace(plugin.getTextManager().get("ui.mapName", ""), "");
                        plugin.getGameManager().tryMap(mapName, player);
                        inventoryGui.close();
                        return true;
                    }));
                }
            }
        } else {
            for (String map : plugin.getGameManager().enabledMaps.keySet()) {
                ItemStack item = new ItemStack(Material.GREEN_TERRACOTTA);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(map);
                List<String> lore = new ArrayList<>();
                if (!plugin.getGameManager().allMaps.get(map).equalsIgnoreCase("")) {
                    lore.add(plugin.getTextManager().get("ui.kitName", plugin.getGameManager().allMaps.get(map)));
                }
                lore.add(plugin.getTextManager().get("ui.maxPlayerNum", plugin.getGameManager().getMapMaxPlayers(map)));
                if (plugin.getGameManager().ongoingMaps.contains(map) || plugin.getGameManager().inUseMaps.containsKey(map)) {
                    lore.add(plugin.getTextManager().get("ui.mapInUse"));
                    item.setType(Material.RED_TERRACOTTA);
                }
                lore.add(plugin.getTextManager().get("ui.playing", plugin.getGameManager().enabledMaps.get(map).size()));
                meta.setLore(lore);
                item.setItemMeta(meta);

                group.addElement(new StaticGuiElement('e', item, click -> {
                    inventoryGui.playClickSound();
                    String mapName = click.getEvent().getCurrentItem().getItemMeta().getDisplayName();
                    plugin.getGameManager().tryMap(mapName, player);
                    inventoryGui.close();
                    return true;
                }));
            }
        }

        ItemStack item = new ItemStack(Material.YELLOW_TERRACOTTA);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plugin.getTextManager().get("ui.randomMap"));
        item.setItemMeta(meta);

        group.addElement(new StaticGuiElement('e', item, click -> {
            inventoryGui.playClickSound();
            plugin.getGameManager().tryMap(plugin.getGameManager().getNextMap(), player);
            inventoryGui.close();
            return true;
        }));
        inventoryGui.addElement(group);

        inventoryGui.addElement(new GuiPageElement('b', new ItemStack(Material.ARROW), GuiPageElement.PageAction.PREVIOUS, plugin.getTextManager().get("ui.buttons.previousPage")));
        inventoryGui.addElement(new GuiPageElement('f', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, plugin.getTextManager().get("ui.buttons.nextPage")));

        inventoryGui.addElement(new StaticGuiElement('a', new ItemStack(Material.BOOK), plugin.getTextManager().get("ui.titles.mapSelect")));
        inventoryGui.addElement(new StaticGuiElement('h', new ItemStack(Material.PAPER), click -> {
            inventoryGui.playClickSound();
            inventoryGui.close();
            return true;
        }, plugin.getTextManager().get("ui.buttons.close")));
        inventoryGui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));

        inventoryGui.show(spigotPlayer, true);
    }

    private void showMapSelectorBedrock(FloodgatePlayer fgPlayer, CommonPlayer player, boolean byKit) {
        List<String> mapsDisplayed = new ArrayList<>();

        Runnable runnable = () -> {
            SimpleForm.Builder outform = SimpleForm.builder();
            outform = outform.title(plugin.getTextManager().get("ui.titles.mapSelect"));

            outform = outform.button(plugin.getTextManager().get("ui.randomMap"), FormImage.Type.PATH, randomIcon);

            if (byKit) {
                for (String map : plugin.getGameManager().enabledMaps.keySet()) {
                    if (!plugin.getGameManager().allMaps.get(map).equalsIgnoreCase("")) {
                        boolean inUse = false;
                        String buttonText = plugin.getTextManager().get("ui.kitName", plugin.getGameManager().allMaps.get(map)) + "; " + plugin.getTextManager().get("ui.mapName", map) + "; " + plugin.getTextManager().get("ui.maxPlayerNum", plugin.getGameManager().getMapMaxPlayers(map)) + "; ";
                        if (plugin.getGameManager().ongoingMaps.contains(map) || plugin.getGameManager().inUseMaps.containsKey(map)) {
                            buttonText += plugin.getTextManager().get("ui.mapInUse") + "; ";
                            inUse = true;
                        }
                        buttonText += plugin.getTextManager().get("ui.playing", plugin.getGameManager().enabledMaps.get(map).size());

                        if (inUse) {
                            outform = outform.button(buttonText, FormImage.Type.PATH, inUseIcon);
                        } else {
                            outform = outform.button(buttonText, FormImage.Type.PATH, mapIcon);
                        }

                        mapsDisplayed.add(map);
                    }
                }
            } else {
                for (String map : plugin.getGameManager().enabledMaps.keySet()) {
                    boolean inUse = false;
                    String buttonText = plugin.getTextManager().get("ui.mapName", map) + "; " + plugin.getTextManager().get("ui.maxPlayerNum", plugin.getGameManager().getMapMaxPlayers(map)) + "; ";
                    if (!plugin.getGameManager().allMaps.get(map).equalsIgnoreCase("")) {
                        buttonText += plugin.getTextManager().get("ui.kitName", plugin.getGameManager().allMaps.get(map)) + "; ";
                    }
                    if (plugin.getGameManager().ongoingMaps.contains(map) || plugin.getGameManager().inUseMaps.containsKey(map)) {
                        buttonText += plugin.getTextManager().get("ui.mapInUse") + "; ";
                        inUse = true;
                    }
                    buttonText += plugin.getTextManager().get("ui.playing", plugin.getGameManager().enabledMaps.get(map).size());

                    if (inUse) {
                        outform = outform.button(buttonText, FormImage.Type.PATH, inUseIcon);
                    } else {
                        outform = outform.button(buttonText, FormImage.Type.PATH, mapIcon;
                    }

                    mapsDisplayed.add(map);
                }
            }

            outform = outform.responseHandler((form, responseData) -> {
                SimpleFormResponse response = form.parseResponse(responseData);
                if (!response.isCorrect()) {
                    // player closed the form or returned invalid info (see FormResponse)
                    return;
                }

                int id = response.getClickedButtonId();
                if (id == 0) {
                    plugin.getGameManager().tryMap(plugin.getGameManager().getNextMap(), player);
                } else {
                    String mapName = mapsDisplayed.get(id - 1);
                    plugin.getGameManager().tryMap(mapName, player);
                }
            });

            fgPlayer.sendForm(outform.build());
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
