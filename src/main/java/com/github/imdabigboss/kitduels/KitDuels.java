package com.github.imdabigboss.kitduels;

import com.github.imdabigboss.kitduels.commands.*;
import com.github.imdabigboss.kitduels.managers.HologramManager;
import com.github.imdabigboss.kitduels.managers.MapManager;
import com.github.imdabigboss.kitduels.managers.StatsManager;
import com.github.imdabigboss.kitduels.managers.TextManager;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

public final class KitDuels extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static TextManager textManager = null;

    private static KitDuels instance = null;
    private static YMLUtils mapsYML = null;
    private static YMLUtils kitsYML = null;
    private static YMLUtils statsYML = null;
    private static YMLUtils messagesYML = null;
    private static boolean hologramsEnabled = false;

    public static Map<Player, String> editModePlayers = new HashMap<>();

    public static Map<String, List<Player>> enabledMaps = new HashMap<>();
    public static Map<String, List<Player>> mapAlivePlayers = new HashMap<>();
    public static Map<String, String> allMaps = new HashMap<>();
    public static Map<Player, String> playerMaps = new HashMap<>();
    public static List<String> ongoingMaps = new ArrayList<>();
    public static Map<String, Integer> inUseMaps = new HashMap<>();

    public static List<String> allKits = new ArrayList<>();
    public static Map<Player, String> playerKits = new HashMap<>();

    public static boolean disableDamageWhenNotInGame = true;
    public static boolean disableDamageInSelectWorlds = true;
    public static List<String> lobbyWorlds = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        log.info(String.format("[%s] Loading configurations...", getDescription().getName(), getDescription().getVersion()));
        this.saveDefaultConfig();

        mapsYML = new YMLUtils("maps.yml");
        kitsYML = new YMLUtils("kits.yml");
        statsYML = new YMLUtils("stats.yml");
        messagesYML = new YMLUtils("messages.yml");

        messagesYML.saveConfig();

        if (kitsYML.getConfig().contains("allKits")) {
            if (kitsYML.getConfig().get("allKits") != null) {
                List<String> kits = kitsYML.getConfig().getStringList("allKits");
                allKits.addAll(kits);
            }
        }

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

                    String kitName = "";
                    if (mapsYML.getConfig().contains(map + ".mapKit")) {
                        boolean hasKit = false;
                        String tmpKitName = mapsYML.getConfig().getString(map + ".mapKit");
                        for (String tmp : allKits) {
                            if (tmp.equalsIgnoreCase(tmpKitName)) {
                                hasKit = true;
                                break;
                            }
                        }

                        if (hasKit) {
                            kitName = tmpKitName;
                        }
                    }
                    allMaps.put(map, kitName);
                }
            }
        }

        log.info(String.format("[%s] Finishing setup...", getDescription().getName()));

        textManager = new TextManager();

        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        if (this.getConfig().contains("enabledMaps")) {
            if (this.getConfig().get("enabledMaps") != null) {
                List<String> maps = this.getConfig().getStringList("enabledMaps");
                for (String map : maps) {
                    enabledMaps.put(map, new ArrayList<>());
                }
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
        this.getCommand("kdstats").setExecutor(new KitDuelsStatsCommand(this));

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
    public static TextManager getTextManager() {
        return textManager;
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
    public static YMLUtils getMessagesYML() {
        return messagesYML;
    }

    public static boolean getHologramsEnabled() {
        return hologramsEnabled;
    }
}
