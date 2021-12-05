package com.github.imdabigboss.kitduels.common.commands;

import com.github.imdabigboss.kitduels.common.KitDuels;
import com.github.imdabigboss.kitduels.common.interfaces.CommonCommandSender;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.interfaces.Location;
import com.github.imdabigboss.kitduels.common.managers.TextManager;
import com.github.imdabigboss.kitduels.common.util.GameMode;
import com.github.imdabigboss.kitduels.common.util.YMLUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KitDuelsCommand {
    private KitDuels plugin;
    private YMLUtils mapsYML;
    private TextManager textManager;

    public KitDuelsCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
        this.mapsYML = plugin.getMapsYML();
        this.textManager = plugin.getTextManager();
    }

    public boolean runCommand(CommonCommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
        } else {
            if (args[0].equals("create")) {
                if (args.length == 2) {
                    if (!mapsYML.contains(args[1] + ".world")) {
                        if (sender.isConsole()) {
                            sender.sendMessage(textManager.get("general.errors.notPlayer"));
                            return true;
                        }
                        CommonPlayer player = sender.getPlayer();

                        sender.sendMessage(textManager.get("general.info.creatingWorld"));
                        plugin.getGameManager().createMap(args[1]);
                        sender.sendMessage(textManager.get("general.info.done"));
                        player.setGameMode(GameMode.CREATIVE);
                        player.teleport(plugin.getWorldUtils().getSpawnLocation(args[1]));

                        plugin.getGameManager().editModePlayers.put(player.getName(), args[1]);
                        plugin.getGameManager().inUseMaps.put(args[1], 0);
                        mapsYML.setLocation(args[1] + ".world", player.getLocation());
                        sender.sendMessage(textManager.get("general.info.mapAdded"));
                    } else {
                        sender.sendMessage(textManager.get("general.errors.mapAlreadyExists"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("delete")) {
                if (args.length == 2) {
                    if (mapsYML.contains(args[1] + ".world")) {
                        if (!sender.isConsole()) {
                            plugin.getGameManager().editModePlayers.remove(sender.getPlayer().getName());
                            plugin.getGameManager().sendToSpawn(sender.getPlayer());
                        }

                        plugin.getGameManager().inUseMaps.remove(args[1]);
                        plugin.getGameManager().enabledMaps.remove(args[1]);
                        List<String> keys = new ArrayList<>(plugin.getGameManager().enabledMaps.keySet());
                        plugin.getConfigYML().set("enabledMaps", keys);

                        if (plugin.getGameManager().allMaps.containsKey(args[1])) {
                            plugin.getGameManager().allMaps.remove(args[1]);
                            plugin.getConfigYML().set("allMaps", plugin.getGameManager().allMaps);
                        }

                        mapsYML.set(args[1], null);

                        plugin.getWorldUtils().unloadWorld(args[1]);
                        plugin.getWorldUtils().deleteWorld(args[1]);

                        sender.sendMessage(textManager.get("general.info.mapDeleted"));
                    } else {
                        sender.sendMessage(textManager.get("general.error.notAMap"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("maxPlayers")) {
                if (sender.isConsole()) {
                    sender.sendMessage(textManager.get("general.errors.notPlayer"));
                    return true;
                }

                CommonPlayer player = sender.getPlayer();

                if (!plugin.getGameManager().editModePlayers.containsKey(player.getName())) {
                    sender.sendMessage(textManager.get("general.errors.notEditingMap"));
                    return true;
                }

                if (args.length == 2) {
                    try {
                        int num = Integer.parseInt(args[1]);
                        if (num < 2) {
                            sender.sendMessage(textManager.get("general.errors.lessThanTwoPlayers"));
                            return true;
                        }

                        mapsYML.set(plugin.getGameManager().editModePlayers.get(player.getName()) + ".maxPlayers", num);
                        sender.sendMessage(textManager.get("general.info.maxPlayersSet", num));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(textManager.get("general.errors.notANum"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("spawn")) {
                if (sender.isConsole()) {
                    sender.sendMessage(textManager.get("general.errors.notPlayer"));
                    return true;
                }

                CommonPlayer player = sender.getPlayer();
                if (!plugin.getGameManager().editModePlayers.containsKey(player.getName())) {
                    sender.sendMessage(textManager.get("general.errors.notEditingMap"));
                    return true;
                }

                if (args.length == 2) {
                    if (mapsYML.contains(plugin.getGameManager().editModePlayers.get(player.getName()) + ".maxPlayers")) {
                        int maxnum = mapsYML.getInt(plugin.getGameManager().editModePlayers.get(player.getName()) + ".maxPlayers");

                        try {
                            int num = Integer.parseInt(args[1]);
                            if (num <= 0) {
                                sender.sendMessage(textManager.get("general.errors.negativeOrNullNumber"));
                                return true;
                            }
                            if (num > maxnum) {
                                sender.sendMessage(textManager.get("general.info.maxPlayersSet", maxnum));
                                return true;
                            }

                            mapsYML.setLocation(plugin.getGameManager().editModePlayers.get(player.getName()) + ".spawn" + num, player.getLocation());
                            sender.sendMessage(textManager.get("general.info.spawnPointSet", num, maxnum));
                        } catch (NumberFormatException e) {
                            sender.sendMessage(textManager.get("general.errors.notANum"));
                        }
                    } else {
                        sender.sendMessage(textManager.get("general.errors.maxPlayersNotSet"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("save")) {
                if (sender.isConsole()) {
                    sender.sendMessage(textManager.get("general.errors.notPlayer"));
                    return true;
                }

                CommonPlayer player = sender.getPlayer();
                if (!plugin.getGameManager().editModePlayers.containsKey(player.getName())) {
                    sender.sendMessage(textManager.get("general.errors.notEditingMap"));
                    return true;
                }

                String worldName = plugin.getGameManager().editModePlayers.get(player.getName());

                if (!(mapsYML.contains(worldName + ".pos1") && mapsYML.contains(worldName + ".pos2"))) {
                    sender.sendMessage(textManager.get("general.errors.resetPosNotSet"));
                    return true;
                }

                sender.sendMessage(textManager.get("general.info.mapSaving"));

                Location pos1 = mapsYML.getLocation(worldName + ".pos1");
                Location pos2 = mapsYML.getLocation(worldName + ".pos2");
                plugin.getWorldUtils().resetClone(pos1, pos2);

                plugin.getGameManager().inUseMaps.remove(worldName);
                plugin.getGameManager().editModePlayers.remove(player.getName());
                plugin.getGameManager().sendToSpawn(player);
                sender.sendMessage(textManager.get("general.info.mapSaved"));
            } else if (args[0].equals("edit")) {
                if (args.length == 2) {
                    if (mapsYML.contains(args[1] + ".world")) {
                        if (sender.isConsole()) {
                            sender.sendMessage(textManager.get("general.errors.notPlayer"));
                            return true;
                        }

                        CommonPlayer player = sender.getPlayer();
                        if (plugin.getGameManager().editModePlayers.containsKey(player.getName())) {
                            sender.sendMessage(textManager.get("general.errors.editingMap"));
                            return true;
                        }
                        if (plugin.getGameManager().inUseMaps.containsKey(args[1])) {
                            sender.sendMessage(textManager.get("general.warning.mapInUse"));
                        }

                        sender.sendMessage(textManager.get("general.info.mapLoading"));
                        plugin.getGameManager().inUseMaps.put(args[1], 0);
                        plugin.getGameManager().editModePlayers.put(player.getName(), args[1]);

                        if (mapsYML.contains(args[1] + ".pos1") && mapsYML.contains(args[1] + ".pos2")) {
                            Location pos1 = mapsYML.getLocation(args[1] + ".pos1");
                            Location pos2 = mapsYML.getLocation(args[1] + ".pos2");
                            plugin.getWorldUtils().removeClone(pos1, pos2);
                        }

                        Location location = mapsYML.getLocation(args[1] + ".world");
                        player.teleport(location);
                        player.setGameMode(GameMode.CREATIVE);
                        player.setFlying(true);
                        player.clearInventory();
                        sender.sendMessage(textManager.get("general.info.mapLoaded", args[1]));
                    } else {
                        sender.sendMessage(textManager.get("general.errors.notAMap"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("lobbySpawn")) {
                if (sender.isConsole()) {
                    sender.sendMessage(textManager.get("general.errors.notPlayer"));
                    return true;
                }

                CommonPlayer player = sender.getPlayer();
                plugin.getConfigYML().setLocation("lobbySpawn", player.getLocation());
                sender.sendMessage(textManager.get("general.info.lobbySpawnPointSet"));
            } else if (args[0].equals("list")) {
                Set<String> keys = mapsYML.getKeys(false);
                String maps = textManager.get("general.info.mapList.header");
                for (String key : keys) {
                    int max = plugin.getGameManager().getMapMaxPlayers(key);

                    String kitName = plugin.getGameManager().allMaps.get(key);
                    if (kitName.equalsIgnoreCase("")) {
                        kitName = "None";
                    }
                    maps += "\n " + textManager.get("general.info.mapList.line", key, max, kitName);
                    if (plugin.getGameManager().enabledMaps.containsKey(key)) {
                        maps += textManager.get("general.info.mapList.enabled");
                    } else {
                        maps += textManager.get("general.info.mapList.disabled");
                    }
                }
                sender.sendMessage(maps);
            } else if (args[0].equals("enable")) {
                if (args.length == 2) {
                    if (!plugin.getGameManager().enabledMaps.containsKey(args[1])) {
                        if (plugin.getGameManager().allMaps.containsKey(args[1])) {
                            plugin.getGameManager().enabledMaps.put(args[1], new ArrayList<>());
                            List<String> keys = new ArrayList<>(plugin.getGameManager().enabledMaps.keySet());
                            plugin.getConfigYML().set("enabledMaps", keys);
                            sender.sendMessage(textManager.get("general.info.mapEnabled"));
                        } else {
                            sender.sendMessage(textManager.get("general.errors.notAMap"));
                        }
                    } else {
                        sender.sendMessage(textManager.get("general.errors.mapAlreadyEnabled"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("disable")) {
                if (args.length == 2) {
                    if (plugin.getGameManager().enabledMaps.containsKey(args[1])) {
                        plugin.getGameManager().enabledMaps.remove(args[1]);
                        List<String> keys = new ArrayList<>(plugin.getGameManager().enabledMaps.keySet());
                        plugin.getConfigYML().set("enabledMaps", keys);
                        sender.sendMessage(textManager.get("general.info.mapDisabled"));
                    } else {
                        sender.sendMessage(textManager.get("general.errors.mapNotEnabled"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("pos1") || args[0].equals("pos2")) {
                if (sender.isConsole()) {
                    sender.sendMessage(textManager.get("general.errors.notPlayer"));
                    return true;
                }

                CommonPlayer player = sender.getPlayer();
                if (!plugin.getGameManager().editModePlayers.containsKey(player.getName())) {
                    sender.sendMessage(textManager.get("general.errors.notEditingMap"));
                    return true;
                }
                String playerMap = plugin.getGameManager().editModePlayers.get(player.getName());

                mapsYML.setLocation(playerMap + "." + args[0], player.getLocation());
                sender.sendMessage(textManager.get("general.info.resetPosSet", args[0]));
            } else if (args[0].equalsIgnoreCase("setHolo")) {
                if (!plugin.getHologramsEnabled()) {
                    sender.sendMessage(textManager.get("general.errors.hologramsNotEnabled"));
                    return true;
                }
                if (sender.isConsole()) {
                    sender.sendMessage(textManager.get("general.errors.notPlayer"));
                    return true;
                }

                CommonPlayer player = sender.getPlayer();
                plugin.getHologramManager().updateHolo(player.getLocation());
                plugin.getConfigYML().set("hologramPos", player.getLocation());
                sender.sendMessage(textManager.get("general.info.hologramPosSet"));
            } else if (args[0].equalsIgnoreCase("delHolo")) {
                plugin.getHologramManager().deleteAllHolos();
                plugin.getConfigYML().set("hologramPos", null);
                sender.sendMessage(textManager.get("general.info.hologramRemoved"));
            } else if (args[0].equalsIgnoreCase("setKit")) {
                if (args.length == 2) {
                    if (plugin.getKitManager().getKits().contains(args[1]) || args[1].equalsIgnoreCase("random")) {
                        CommonPlayer player = sender.getPlayer();
                        if (!plugin.getGameManager().editModePlayers.containsKey(player.getName())) {
                            sender.sendMessage(textManager.get("general.errors.notEditingMap"));
                            return true;
                        }
                        String playerMap = plugin.getGameManager().editModePlayers.get(player.getName());
                        String newKit = args[1].equalsIgnoreCase("random") ? "" : args[1];

                        mapsYML.set(playerMap + ".mapKit", newKit);
                        plugin.getGameManager().allMaps.replace(playerMap, newKit);
                        sender.sendMessage(textManager.get("general.info.mapKitSet", args[1]));
                    } else {
                        sender.sendMessage(textManager.get("general.errors.kitDoesntExist"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else {
                sendHelp(sender);
            }
        }

        plugin.getConfigYML().saveConfig();
        plugin.getKitsYML().saveConfig();
        mapsYML.saveConfig();
        return true;
    }

    public void sendHelp(CommonCommandSender sender) {
        sender.sendMessage("The correct usage is:\n - create <name>\n - delete <name>\n - maxPlayers <number>\n - spawn <number>\n - save\n - edit <name>\n - lobbySpawn\n - list\n - enable <name>\n - disable <name>\n - pos1\n - pos2\n - setHolo\n - delHolo\n setKit <name>");
    }
}
