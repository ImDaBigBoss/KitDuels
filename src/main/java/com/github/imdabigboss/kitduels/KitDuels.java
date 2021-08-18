package com.github.imdabigboss.kitduels;

import com.github.imdabigboss.kitduels.commands.*;
import com.github.imdabigboss.kitduels.util.InventorySerialization;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
