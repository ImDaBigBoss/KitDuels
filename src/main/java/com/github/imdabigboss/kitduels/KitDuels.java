package com.github.imdabigboss.kitduels;

import com.github.imdabigboss.kitduels.commands.*;
import com.github.imdabigboss.kitduels.util.InventorySerialization;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public final class KitDuels extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static KitDuels instance = null;
    private static YMLUtils mapsYML = null;
    private static YMLUtils kitsYML = null;

    public static Map<Player, String> editModePlayers = new HashMap<>();

    public static Map<String, List<Player>> enabledMaps = new HashMap<>();
    public static Map<String, List<Player>> mapAlivePlayers = new HashMap<>();
    public static List<String> allMaps = new ArrayList<>();
    public static Map<Player, String> playerMaps = new HashMap<>();
    public static List<String> ongoingMaps = new ArrayList<>();
    public static List<String> inUseMaps = new ArrayList<>();

    public static List<String> allKits = new ArrayList<>();
    public static Map<Player, String> playerKits = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        log.info(String.format("[%s] Loading worlds...", getDescription().getName(), getDescription().getVersion()));

        if (this.getConfig().contains("allMaps")) {
            if (this.getConfig().get("allMaps") != null) {
                List<String> maps = this.getConfig().getStringList("allMaps");
                for (String map : maps) {
                    WorldCreator wc = new WorldCreator(map);
                    wc.generator("VoidGenerator");
                    wc.type(WorldType.NORMAL);
                    wc.createWorld();
                    allMaps.add(map);
                }
            }
        }

        log.info(String.format("[%s] Finishing setup...", getDescription().getName(), getDescription().getVersion()));

        mapsYML = new YMLUtils("maps.yml");
        kitsYML = new YMLUtils("kits.yml");

        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        if (this.getConfig().contains("enabledMaps")) {
            if (this.getConfig().get("enabledMaps") != null) {
                List<String> maps = this.getConfig().getStringList("enabledMaps");
                for (String map : maps) {
                    enabledMaps.put(map, new ArrayList<>());
                }
            }
        }

        if (kitsYML.getConfig().contains("allKits")) {
            if (kitsYML.getConfig().get("allKits") != null) {
                List<String> kits = kitsYML.getConfig().getStringList("allKits");
                allKits.addAll(kits);
            }
        }

        this.getCommand("kd").setExecutor(new KitDuelsCommand(this));
        this.getCommand("kdkits").setExecutor(new KitDuelsKitsCommand(this));
        this.getCommand("joingame").setExecutor(new JoinGameCommand(this));
        this.getCommand("leavegame").setExecutor(new LeaveGameCommand(this));
        this.getCommand("kitselect").setExecutor(new KitSelectCommand(this));

        log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    public static void sendToSpawn(Player player) {
        if (instance.getConfig().contains("lobbySpawn")) {
            Location location = (Location) instance.getConfig().get("lobbySpawn");
            player.teleport(location);
        } else {
            Location location = instance.getServer().getWorld("world").getSpawnLocation();
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
            content = InventorySerialization.fromBase64(kitsYML.getConfig().getString(kitName + ".content"));
            armor = InventorySerialization.itemStackArrayFromBase64(kitsYML.getConfig().getString(kitName + ".armor"));
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

        InventoryGui inventoryGui = new InventoryGui(instance, player, "Kit select", kitLayout);

        GuiElementGroup group = new GuiElementGroup('g');
        for (String kit : allKits) {
            ItemStack item = new ItemStack(Material.ENDER_CHEST);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(kit);
            item.setItemMeta(meta);

            group.addElement(new StaticGuiElement('e', item, click -> {
                inventoryGui.playClickSound();
                String kitName = click.getEvent().getCurrentItem().getItemMeta().getDisplayName();
                playerKits.remove(player);
                playerKits.put(player, kitName);
                player.sendMessage(ChatColor.AQUA + "Set your kit to " + kitName);
                inventoryGui.close();
                return true;
            }));
        }

        ItemStack item = new ItemStack(Material.CYAN_SHULKER_BOX);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Random kit");
        item.setItemMeta(meta);

        group.addElement(new StaticGuiElement('e', item, click -> {
            inventoryGui.playClickSound();
            playerKits.remove(player);
            player.sendMessage(ChatColor.AQUA + "Your kit is now random");
            inventoryGui.close();
            return true;
        }));
        inventoryGui.addElement(group);

        inventoryGui.addElement(new GuiPageElement('b', new ItemStack(Material.ARROW), GuiPageElement.PageAction.PREVIOUS, "Go to previous page (%prevpage%)"));
        inventoryGui.addElement(new GuiPageElement('f', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, "Go to next page (%nextpage%)"));

        inventoryGui.addElement(new StaticGuiElement('a', new ItemStack(Material.CHEST), "Kit select"));
        inventoryGui.addElement(new StaticGuiElement('h', new ItemStack(Material.PAPER), click -> {
            inventoryGui.playClickSound();
            inventoryGui.close();
            return true;
        }, "Close"));
        inventoryGui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));

        inventoryGui.show(player, true);
    }

    public static Logger getLog() {
        return log;
    }
    public static KitDuels getInstance() {
        return instance;
    }
    public static YMLUtils getMapsYML() {
        return mapsYML;
    }
    public static YMLUtils getKitsYML() {
        return kitsYML;
    }
}
