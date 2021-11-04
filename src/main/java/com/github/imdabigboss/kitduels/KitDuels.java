package com.github.imdabigboss.kitduels;

import com.github.imdabigboss.kitduels.commands.*;

import com.github.imdabigboss.kitduels.managers.HologramManager;
import com.github.imdabigboss.kitduels.managers.MapManager;
import com.github.imdabigboss.kitduels.managers.StatsManager;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

public final class KitDuels extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static KitDuels instance = null;
    private static YMLUtils mapsYML = null;
    private static YMLUtils kitsYML = null;
    private static YMLUtils statsYML = null;
    private static boolean hologramsEnabled = false;

    public static Map<Player, String> editModePlayers = new HashMap<>();

    public static Map<String, List<Player>> enabledMaps = new HashMap<>();
    public static Map<String, List<Player>> mapAlivePlayers = new HashMap<>();
    public static List<String> allMaps = new ArrayList<>();
    public static Map<Player, String> playerMaps = new HashMap<>();
    public static List<String> ongoingMaps = new ArrayList<>();
    public static List<String> inUseMaps = new ArrayList<>();

    public static List<String> allKits = new ArrayList<>();
    public static Map<Player, String> playerKits = new HashMap<>();

    public static boolean disableDamageWhenNotInGame = true;
    public static boolean disableDamageInSelectWorlds = true;
    public static List<String> lobbyWorlds = new ArrayList<>();

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
                    wc.generator("VoidGen");
                    wc.type(WorldType.NORMAL);
                    World world = wc.createWorld();
                    MapManager.setMapGameRules(world);
                    allMaps.add(map);
                }
            }
        }

        log.info(String.format("[%s] Finishing setup...", getDescription().getName()));

        mapsYML = new YMLUtils("maps.yml");
        kitsYML = new YMLUtils("kits.yml");
        statsYML = new YMLUtils("stats.yml");

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

        StatsManager.loadStats();

        if (!getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            log.warning(String.format("[%s] HolographicDisplays is not installed or enabled on this server! If you want holograms, please add it to your plugins.", getDescription().getName()));
        } else {
            hologramsEnabled = true;
            if (this.getConfig().contains("leaderboardLines")) {
                HologramManager.leaderboardLines = this.getConfig().getInt("leaderboardLines");
            }

            HologramManager.registerPlaceholders();
            if (this.getConfig().contains("hologramPos")) {
                Location location = this.getConfig().getLocation("hologramPos");
                if (location != null) {
                    HologramManager.updateHolo(location);
                }
            }
        }

        if (this.getConfig().contains("disableDamageWhenNotInGame")) {
            disableDamageWhenNotInGame = this.getConfig().getBoolean("disableDamageWhenNotInGame");

            if (disableDamageWhenNotInGame) {
                if (this.getConfig().contains("disableDamageInSelectWorlds")) {
                    disableDamageInSelectWorlds = this.getConfig().getBoolean("disableDamageInSelectWorlds");
                }

                if (disableDamageInSelectWorlds) {
                    if (this.getConfig().contains("lobbyWorlds")) {
                        if (this.getConfig().get("lobbyWorlds") != null) {
                            lobbyWorlds = this.getConfig().getStringList("lobbyWorlds");
                        }
                    }
                }
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

        if (hologramsEnabled) {
            HologramsAPI.unregisterPlaceholders(this);
            HologramManager.deleteAllHolos();
        }
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
    public static YMLUtils getStatsYML() {
        return statsYML;
    }
    public static boolean getHologramsEnabled() {
        return hologramsEnabled;
    }
}
