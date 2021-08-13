package com.github.imdabigboss.kitduels;

import com.github.imdabigboss.kitduels.commands.JoinGameCommand;
import com.github.imdabigboss.kitduels.commands.KitDuelsCommand;

import com.github.imdabigboss.kitduels.commands.LeaveGameCommand;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

public final class KitDuels extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static KitDuels instance = null;
    private static YMLUtils mapsYML = null;

    public static Map<Player, String> editModePlayers = new HashMap<>();

    public static Map<String, List<Player>> enabledMaps = new HashMap<>();
    public static Map<String, List<Player>> mapAlivePlayers = new HashMap<>();
    public static List<String> allMaps = new ArrayList<>();
    public static Map<Player, String> playerMaps = new HashMap<>();
    public static List<String> ongoingMaps = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        log.info(String.format("[%s] Loading worlds...", getDescription().getName(), getDescription().getVersion()));

        if (this.getConfig().contains("allMaps")) {
            if (this.getConfig().get("allMaps") != null) {
                List<String> maps = this.getConfig().getStringList("allMaps");
                for (String map : maps) {
                    new WorldCreator(map).createWorld();
                    allMaps.add(map);
                }
            }
        }

        log.info(String.format("[%s] Finishing setup...", getDescription().getName(), getDescription().getVersion()));

        mapsYML = new YMLUtils("maps.yml");

        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        if (this.getConfig().contains("enabledMaps")) {
            if (this.getConfig().get("enabledMaps") != null) {
                List<String> maps = this.getConfig().getStringList("enabledMaps");
                for (String map : maps) {
                    enabledMaps.put(map, new ArrayList<>());
                }
            }
        }

        this.getCommand("kd").setExecutor(new KitDuelsCommand(this));
        this.getCommand("joingame").setExecutor(new JoinGameCommand(this));
        this.getCommand("leavegame").setExecutor(new LeaveGameCommand(this));

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

    public static Logger getLog() {
        return log;
    }
    public static KitDuels getInstance() {
        return instance;
    }
    public static YMLUtils getMapsYML() {
        return mapsYML;
    }
}
