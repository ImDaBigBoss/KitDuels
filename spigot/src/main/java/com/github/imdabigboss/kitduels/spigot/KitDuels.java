package com.github.imdabigboss.kitduels.spigot;

import com.github.imdabigboss.kitduels.common.ChatColor;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.managers.GameManager;
import com.github.imdabigboss.kitduels.common.managers.StatsManager;
import com.github.imdabigboss.kitduels.common.managers.TextManager;
import com.github.imdabigboss.kitduels.common.util.Sounds;

import com.github.imdabigboss.kitduels.spigot.managers.CommandManager;
import com.github.imdabigboss.kitduels.spigot.managers.HologramManager;
import com.github.imdabigboss.kitduels.spigot.managers.GUIManager;
import com.github.imdabigboss.kitduels.spigot.managers.KitManager;
import com.github.imdabigboss.kitduels.spigot.interfaces.Logger;
import com.github.imdabigboss.kitduels.spigot.util.EntityUtils;
import com.github.imdabigboss.kitduels.spigot.util.PlayerUtils;
import com.github.imdabigboss.kitduels.spigot.util.WorldUtils;
import com.github.imdabigboss.kitduels.spigot.util.YMLUtils;
import com.github.imdabigboss.kitduels.spigot.util.InventorySerialization;
import com.github.imdabigboss.kitduels.spigot.util.CountdownTimer;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.cumulus.Forms;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.*;

public final class KitDuels extends JavaPlugin implements com.github.imdabigboss.kitduels.common.KitDuels {
    private static Logger log = null;

    private static YMLUtils configYML = null;
    private static YMLUtils mapsYML = null;
    private static YMLUtils kitsYML = null;
    private static YMLUtils statsYML = null;
    private static YMLUtils messagesYML = null;
    private static boolean hologramsEnabled = false;

    private StatsManager statsManager = null;
    private TextManager textManager = null;
    private KitManager kitManager = null;
    private GameManager gameManager = null;
    private GUIManager guiManager = null;
    private HologramManager hologramManager = null;

    private PlayerUtils playerUtils = null;
    private WorldUtils worldUtils = null;
    private EntityUtils entityUtils = null;
    private InventorySerialization inventorySerialization = null;

    public static boolean disableDamageWhenNotInGame = true;
    public static boolean disableDamageInSelectWorlds = true;
    public static List<String> lobbyWorlds = new ArrayList<>();

    private static boolean floodgateEnabled = false;

    @Override
    public void onEnable() {
        log = new Logger(java.util.logging.Logger.getLogger("Minecraft"), this.getDescription().getName());

        if (this.getServer().getPluginManager().getPlugin("floodgate") != null) {
            if (FloodgateApi.getInstance() != null) {
                floodgateEnabled = true;
                log.info("Floodgate found!");
            } else {
                log.info("Floodgate found, but API is null!");
            }
        }

        log.info("Loading configurations");

        configYML = new YMLUtils(this, "config.yml");
        configYML.saveConfig();

        kitsYML = new YMLUtils(this, "kits.yml");
        kitManager = new KitManager(this);
        if (kitsYML.contains("allKits")) {
            if (kitsYML.get("allKits") != null) {
                List<String> kits = kitsYML.getStringList("allKits");
                kitManager.addKits(kits);
            }
        }

        gameManager = new GameManager(this);

        worldUtils = new WorldUtils(this);

        log.info("Loading worlds");

        if (configYML.contains("allMaps")) {
            if (configYML.get("allMaps") != null) {
                List<String> maps = configYML.getStringList("allMaps");
                for (String map : maps) {
                    worldUtils.loadWorld(map);
                }

                mapsYML = new YMLUtils(this, "maps.yml");

                for (String map : maps) {
                    String kitName = "";
                    if (mapsYML.contains(map + ".mapKit")) {
                        boolean hasKit = false;
                        String tmpKitName = mapsYML.getString(map + ".mapKit");
                        for (String tmp : kitManager.getKits()) {
                            if (tmp.equalsIgnoreCase(tmpKitName)) {
                                hasKit = true;
                                break;
                            }
                        }

                        if (hasKit) {
                            kitName = tmpKitName;
                        }
                    }
                    gameManager.allMaps.put(map, kitName);
                }
            } else {
                mapsYML = new YMLUtils(this, "maps.yml");
            }
        } else {
            mapsYML = new YMLUtils(this, "maps.yml");
        }

        log.info("Finishing setup");

        statsYML = new YMLUtils(this, "stats.yml");
        statsManager = new StatsManager(this);

        messagesYML = new YMLUtils(this, "messages.yml");
        messagesYML.saveConfig();
        textManager = new TextManager(messagesYML);

        playerUtils = new PlayerUtils(this);
        entityUtils = new EntityUtils(this);
        inventorySerialization = new InventorySerialization(this);

        textManager = new TextManager(messagesYML);
        guiManager = new GUIManager(this);

        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        if (configYML.contains("enabledMaps")) {
            if (configYML.get("enabledMaps") != null) {
                List<String> maps = configYML.getStringList("enabledMaps");
                for (String map : maps) {
                    gameManager.enabledMaps.put(map, new ArrayList<>());
                }
            }
        }

        statsManager.loadStats();

        hologramManager = new HologramManager(this);

        if (!getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            log.warning(String.format("[%s] HolographicDisplays is not installed or enabled on this server! If you want holograms, please add it to your plugins.", getDescription().getName()));
        } else {
            hologramsEnabled = true;
            if (configYML.contains("leaderboardLines")) {
                hologramManager.setLeaderboardLines(configYML.getInt("leaderboardLines"));
            }

            hologramManager.registerPlaceholders();
            if (configYML.contains("hologramPos")) {
                Location location = configYML.getLocation("hologramPos").toBukkit();
                if (location != null) {
                    hologramManager.updateHolo(new com.github.imdabigboss.kitduels.spigot.interfaces.Location(location, this.getServer()));
                }
            }
        }

        if (configYML.contains("disableDamageWhenNotInGame")) {
            disableDamageWhenNotInGame = configYML.getBoolean("disableDamageWhenNotInGame");

            if (disableDamageWhenNotInGame) {
                if (configYML.contains("disableDamageInSelectWorlds")) {
                    disableDamageInSelectWorlds = configYML.getBoolean("disableDamageInSelectWorlds");
                }

                if (disableDamageInSelectWorlds) {
                    if (configYML.contains("lobbyWorlds")) {
                        if (configYML.get("lobbyWorlds") != null) {
                            lobbyWorlds = configYML.getStringList("lobbyWorlds");
                        }
                    }
                }
            }
        }

        this.getCommand("kd").setExecutor(new CommandManager(this));
        this.getCommand("kdkits").setExecutor(new CommandManager(this));
        this.getCommand("joingame").setExecutor(new CommandManager(this));
        this.getCommand("leavegame").setExecutor(new CommandManager(this));
        this.getCommand("kitselect").setExecutor(new CommandManager(this));
        this.getCommand("kdstats").setExecutor(new CommandManager(this));

        log.info(String.format("Enabled Version %s", getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        log.info(String.format("Disabled Version %s", getDescription().getVersion()));

        if (hologramsEnabled) {
            HologramsAPI.unregisterPlaceholders(this);
            hologramManager.deleteAllHolos();
        }
    }

    @Override
    public void startCountdownAndStartGame(String map) {
        new CountdownTimer(10, this) {
            @Override
            public void count(int current) {
                if (current == 0) {
                    gameManager.startGame(map);
                } else {
                    for (CommonPlayer mapPlayer : gameManager.enabledMaps.get(map)) {
                        mapPlayer.sendMessage(textManager.get("messages.gameStarting", current));
                        mapPlayer.sendTitle(ChatColor.RED.toString() + current, textManager.get("messages.gameStartingSoon"), 0, 30, 0);
                        mapPlayer.playSound(Sounds.NOTE_BLOCK,1, 2);
                    }
                }
            }
        }.start();
    }

    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public YMLUtils getConfigYML() {
        return configYML;
    }
    @Override
    public YMLUtils getMapsYML() {
        return mapsYML;
    }
    @Override
    public YMLUtils getKitsYML() {
        return kitsYML;
    }
    @Override
    public YMLUtils getStatsYML() {
        return statsYML;
    }
    @Override
    public YMLUtils getMessagesYML() {
        return messagesYML;
    }
    @Override
    public boolean getHologramsEnabled() {
        return hologramsEnabled;
    }

    @Override
    public StatsManager getStatsManager() {
        return statsManager;
    }
    @Override
    public TextManager getTextManager() {
        return textManager;
    }
    @Override
    public KitManager getKitManager() {
        return kitManager;
    }
    @Override
    public GameManager getGameManager() {
        return gameManager;
    }
    @Override
    public GUIManager getGUIManager() {
        return guiManager;
    }
    @Override
    public HologramManager getHologramManager() {
        return hologramManager;
    }

    @Override
    public PlayerUtils getPlayerUtils() {
        return playerUtils;
    }
    @Override
    public WorldUtils getWorldUtils() {
        return worldUtils;
    }
    @Override
    public EntityUtils getEntityUtils() {
        return entityUtils;
    }
    @Override
    public InventorySerialization getInventorySerialization() {
        return inventorySerialization;
    }

    public boolean isFloodgateEnabled() {
        return floodgateEnabled;
    }
}
