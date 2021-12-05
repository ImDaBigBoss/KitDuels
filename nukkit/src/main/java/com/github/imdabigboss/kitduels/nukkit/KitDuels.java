package com.github.imdabigboss.kitduels.nukkit;

import com.github.imdabigboss.kitduels.common.ChatColor;
import com.github.imdabigboss.kitduels.common.commands.*;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.managers.GameManager;
import com.github.imdabigboss.kitduels.common.managers.StatsManager;
import com.github.imdabigboss.kitduels.common.managers.TextManager;

import com.github.imdabigboss.kitduels.common.util.Sounds;
import com.github.imdabigboss.kitduels.nukkit.interfaces.NukkitCommandSender;
import com.github.imdabigboss.kitduels.nukkit.managers.KitManager;
import com.github.imdabigboss.kitduels.nukkit.managers.HologramManager;
import com.github.imdabigboss.kitduels.nukkit.managers.GUIManager;
import com.github.imdabigboss.kitduels.nukkit.util.*;
import com.github.imdabigboss.kitduels.nukkit.interfaces.Logger;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.List;

public class KitDuels extends PluginBase implements com.github.imdabigboss.kitduels.common.KitDuels {
    private Logger log;

    private YMLUtils configYML = null;
    private YMLUtils mapsYML = null;
    private YMLUtils kitsYML = null;
    private YMLUtils statsYML = null;
    private YMLUtils messagesYML = null;
    private boolean hologramsEnabled = false;

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

    @Override
    public void onEnable() {
        log = new Logger(this.getLogger());
        Generator.addGenerator(EmptyWorldGen.class, "EmptyWorldGen", Generator.TYPE_INFINITE);

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
        //TODO: Add holograms

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

        log.info(String.format("Enabled Version %s", getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        log.info(String.format("Disabled Version %s", getDescription().getVersion()));

        if (hologramsEnabled) {
            //TODO: Unregister placeholders?
            hologramManager.deleteAllHolos();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kd")) {
            return new KitDuelsCommand(this).runCommand(new NukkitCommandSender(sender), label, args);
        } else if (command.getName().equalsIgnoreCase("kdkits")) {
            return new KitDuelsKitsCommand(this).runCommand(new NukkitCommandSender(sender), label, args);
        } else if (command.getName().equalsIgnoreCase("joingame")) {
            return new JoinGameCommand(this).runCommand(new NukkitCommandSender(sender), label, args);
        } else if (command.getName().equalsIgnoreCase("leavegame")) {
            return new LeaveGameCommand(this).runCommand(new NukkitCommandSender(sender), label, args);
        } else if (command.getName().equalsIgnoreCase("kitselect")) {
            return new KitSelectCommand(this).runCommand(new NukkitCommandSender(sender), label, args);
        } else if (command.getName().equalsIgnoreCase("kdstats")) {
            return new KitDuelsStatsCommand(this).runCommand(new NukkitCommandSender(sender), label, args);
        } else {
            return false;
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
        return this.log;
    }

    @Override
    public YMLUtils getConfigYML() {
        return this.configYML;
    }
    @Override
    public YMLUtils getMapsYML() {
        return this.mapsYML;
    }
    @Override
    public YMLUtils getKitsYML() {
        return this.kitsYML;
    }
    @Override
    public YMLUtils getStatsYML() {
        return this.statsYML;
    }
    @Override
    public YMLUtils getMessagesYML() {
        return this.messagesYML;
    }
    @Override
    public boolean getHologramsEnabled() {
        return this.hologramsEnabled;
    }

    @Override
    public StatsManager getStatsManager() {
        return this.statsManager;
    }
    @Override
    public TextManager getTextManager() {
        return this.textManager;
    }
    @Override
    public KitManager getKitManager() {
        return this.kitManager;
    }
    @Override
    public GameManager getGameManager() {
        return this.gameManager;
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
        return this.playerUtils;
    }
    @Override
    public WorldUtils getWorldUtils() {
        return this.worldUtils;
    }
    @Override
    public EntityUtils getEntityUtils() {
        return this.entityUtils;
    }
    @Override
    public InventorySerialization getInventorySerialization() {
        return inventorySerialization;
    }
}
