package com.github.imdabigboss.kitduels.commands;

import com.github.imdabigboss.kitduels.managers.GameManager;
import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.managers.HologramManager;
import com.github.imdabigboss.kitduels.managers.MapManager;
import com.github.imdabigboss.kitduels.YMLUtils;
import com.github.imdabigboss.kitduels.managers.TextManager;
import com.github.imdabigboss.kitduels.util.WorldEditUtils;
import com.github.imdabigboss.kitduels.util.WorldUtils;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KitDuelsCommand implements CommandExecutor, TabExecutor {
    private KitDuels plugin;
    private YMLUtils mapsYML;
    private TextManager textManager;

    public KitDuelsCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
        this.mapsYML = KitDuels.getMapsYML();
        this.textManager = KitDuels.getTextManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
        } else {
            if (args[0].equals("create")) {
                if (args.length == 2) {
                    if (!mapsYML.getConfig().contains(args[1] + ".world")) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(textManager.get("general.errors.notPlayer"));
                            return true;
                        }
                        Player player = (Player) sender;

                        sender.sendMessage(textManager.get("general.info.creatingWorld"));
                        MapManager.createMap(args[1]);
                        sender.sendMessage(textManager.get("general.info.done"));
                        player.setGameMode(GameMode.CREATIVE);
                        player.teleport(KitDuels.getInstance().getServer().getWorld(args[1]).getSpawnLocation());

                        KitDuels.editModePlayers.put(player, args[1]);
                        KitDuels.inUseMaps.put(args[1], 0);
                        mapsYML.getConfig().set(args[1] + ".world", player.getLocation());
                        sender.sendMessage(textManager.get("general.info.mapAdded"));
                    } else {
                        sender.sendMessage(textManager.get("general.errors.mapAlreadyExists"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("delete")) {
                if (args.length == 2) {
                    if (mapsYML.getConfig().contains(args[1] + ".world")) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            KitDuels.editModePlayers.remove(player);
                            GameManager.sendToSpawn(player);
                        }
                        KitDuels.inUseMaps.remove(args[1]);
                        KitDuels.enabledMaps.remove(args[1]);
                        List<String> keys = new ArrayList<>(KitDuels.enabledMaps.keySet());
                        plugin.getConfig().set("enabledMaps", keys);

                        if (KitDuels.allMaps.contains(args[1])) {
                            KitDuels.allMaps.remove(args[1]);
                            plugin.getConfig().set("allMaps", KitDuels.allMaps);
                        }

                        mapsYML.getConfig().set(args[1], null);

                        World world = KitDuels.getInstance().getServer().getWorld(args[1]);
                        WorldUtils.unloadWorld(world);
                        WorldUtils.deleteWorld(world.getWorldFolder());

                        sender.sendMessage(textManager.get("general.info.mapDeleted"));
                    } else {
                        sender.sendMessage(textManager.get("general.error.notAMap"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("maxPlayers")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(textManager.get("general.errors.notPlayer"));
                    return true;
                }

                Player player = (Player) sender;
                if (!KitDuels.editModePlayers.containsKey(player)) {
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

                        mapsYML.getConfig().set(KitDuels.editModePlayers.get(player) + ".maxPlayers", num);
                        sender.sendMessage(textManager.get("general.info.maxPlayersSet", num));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(textManager.get("general.errors.notANum"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("spawn")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(textManager.get("general.errors.notPlayer"));
                    return true;
                }

                Player player = (Player) sender;
                if (!KitDuels.editModePlayers.containsKey(player)) {
                    sender.sendMessage(textManager.get("general.errors.notEditingMap"));
                    return true;
                }

                if (args.length == 2) {
                    if (mapsYML.getConfig().contains(KitDuels.editModePlayers.get(player) + ".maxPlayers")) {
                        int maxnum = mapsYML.getConfig().getInt(KitDuels.editModePlayers.get(player) + ".maxPlayers");

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

                            mapsYML.getConfig().set(KitDuels.editModePlayers.get(player) + ".spawn" + num, player.getLocation());
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
                if (!(sender instanceof Player)) {
                    sender.sendMessage(textManager.get("general.errors.notPlayer"));
                    return true;
                }

                Player player = (Player) sender;
                if (!KitDuels.editModePlayers.containsKey(player)) {
                    sender.sendMessage(textManager.get("general.errors.notEditingMap"));
                    return true;
                }

                String worldName = KitDuels.editModePlayers.get(player);

                if (!(mapsYML.getConfig().contains(worldName + ".pos1") && mapsYML.getConfig().contains(worldName + ".pos2"))) {
                    sender.sendMessage(textManager.get("general.errors.resetPosNotSet"));
                    return true;
                }

                sender.sendMessage(textManager.get("general.info.mapSaving"));

                Location pos1 = mapsYML.getConfig().getLocation(worldName + ".pos1");
                Location pos2 = mapsYML.getConfig().getLocation(worldName + ".pos2");
                WorldEditUtils.removeMapClone(pos1, pos2);
                WorldEditUtils.cloneRegion(pos1, pos2, WorldEditUtils.getCloneRegion(pos1, pos2)[0]);

                KitDuels.inUseMaps.remove(worldName);
                KitDuels.editModePlayers.remove(player);
                GameManager.sendToSpawn(player);
                sender.sendMessage(textManager.get("general.info.mapSaved"));
            } else if (args[0].equals("edit")) {
                if (args.length == 2) {
                    if (mapsYML.getConfig().contains(args[1] + ".world")) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(textManager.get("general.errors.notPlayer"));
                            return true;
                        }
                        if (KitDuels.inUseMaps.containsKey(args[1])) {
                            sender.sendMessage(textManager.get("general.warning.mapInUse"));
                        }
                        Player player = (Player) sender;

                        sender.sendMessage(textManager.get("general.info.mapLoading"));
                        KitDuels.inUseMaps.put(args[1], 0);
                        KitDuels.editModePlayers.put(player, args[1]);

                        if (mapsYML.getConfig().contains(args[1] + ".pos1") && mapsYML.getConfig().contains(args[1] + ".pos2")) {
                            Location pos1 = mapsYML.getConfig().getLocation(args[1] + ".pos1");
                            Location pos2 = mapsYML.getConfig().getLocation(args[1] + ".pos2");
                            WorldEditUtils.removeMapClone(pos1, pos2);
                        }

                        Location location = (Location) mapsYML.getConfig().get(args[1] + ".world");
                        player.teleport(location);
                        player.setGameMode(GameMode.CREATIVE);
                        player.setFlying(true);
                        player.getInventory().clear();
                        sender.sendMessage(textManager.get("general.info.mapLoaded", args[1]));
                    } else {
                        sender.sendMessage(textManager.get("general.errors.notAMap"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("lobbySpawn")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(textManager.get("general.errors.notPlayer"));
                    return true;
                }

                Player player = (Player) sender;
                plugin.getConfig().set("lobbySpawn", player.getLocation());
                sender.sendMessage(textManager.get("general.info.lobbySpawnSet"));
            } else if (args[0].equals("list")) {
                Set<String> keys = mapsYML.getConfig().getKeys(false);
                String maps = textManager.get("general.info.mapList.header");
                for (String key : keys) {
                    int max = MapManager.getMapMaxPlayers(key);
                    maps += "\n " + textManager.get("general.info.mapList.line", key, max);
                    if (KitDuels.enabledMaps.containsKey(key)) {
                        maps += textManager.get("general.info.mapList.enabled");
                    } else {
                        maps += textManager.get("general.info.mapList.disabled");
                    }
                }
                sender.sendMessage(maps);
            } else if (args[0].equals("enable")) {
                if (args.length == 2) {
                    if (!KitDuels.enabledMaps.containsKey(args[1])) {
                        if (KitDuels.allMaps.contains(args[1])) {
                            KitDuels.enabledMaps.put(args[1], new ArrayList<>());
                            List<String> keys = new ArrayList<>(KitDuels.enabledMaps.keySet());
                            plugin.getConfig().set("enabledMaps", keys);
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
                    if (KitDuels.enabledMaps.containsKey(args[1])) {
                        KitDuels.enabledMaps.remove(args[1]);
                        List<String> keys = new ArrayList<>(KitDuels.enabledMaps.keySet());
                        plugin.getConfig().set("enabledMaps", keys);
                        sender.sendMessage(textManager.get("general.info.mapDisabled"));
                    } else {
                        sender.sendMessage(textManager.get("general.errors.mapNotEnabled"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("pos1") || args[0].equals("pos2")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(textManager.get("general.errors.notPlayer"));
                    return true;
                }

                Player player = (Player) sender;
                if (!KitDuels.editModePlayers.containsKey(player)) {
                    sender.sendMessage(textManager.get("general.errors.notEditingMap"));
                    return true;
                }
                String playerMap = KitDuels.editModePlayers.get(player);

                mapsYML.getConfig().set(playerMap + "." + args[0], player.getLocation());
                sender.sendMessage(textManager.get("general.info.resetPosSet", args[0]));
            } else if (args[0].equalsIgnoreCase("setHolo")) {
                if (!KitDuels.getHologramsEnabled()) {
                    sender.sendMessage(textManager.get("general.errors.hologramsNotEnabled"));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(textManager.get("general.errors.notPlayer"));
                    return true;
                }
                Player player = (Player) sender;
                HologramManager.updateHolo(player.getLocation());
                plugin.getConfig().set("hologramPos", player.getLocation());
                sender.sendMessage(textManager.get("general.info.hologramPosSet"));
            } else if (args[0].equalsIgnoreCase("delHolo")) {
                HologramManager.deleteAllHolos();
                plugin.getConfig().set("hologramPos", null);
                sender.sendMessage(textManager.get("general.info.hologramRemoved"));
            } else {
                sendHelp(sender);
            }
        }

        plugin.saveConfig();
        mapsYML.saveConfig();
        return true;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage("The correct usage is:\n - create <name>\n - delete <name>\n - maxPlayers <number>\n - spawn <number>\n - save\n - edit <name>\n - lobbySpawn\n - list\n - enable <name>\n - disable <name>\n - pos1\n - pos2\n - setHolo\n - delHolo");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> cmds = new ArrayList<>();
        if (args.length == 1) {
            cmds.add("create");
            cmds.add("delete");
            cmds.add("maxPlayers");
            cmds.add("spawn");
            cmds.add("save");
            cmds.add("edit");
            cmds.add("lobbySpawn");
            cmds.add("list");
            cmds.add("enable");
            cmds.add("disable");
            cmds.add("pos1");
            cmds.add("pos2");
            cmds.add("setHolo");
            cmds.add("delHolo");

        } else if (args.length == 2) {
            if (args[0].equals("edit") || args[0].equals("delete") || args[0].equals("enable") || args[0].equals("disable")) {
                cmds.addAll(mapsYML.getConfig().getKeys(false));
            }
        }
        return cmds;
    }
}
