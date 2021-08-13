package com.github.imdabigboss.kitduels.commands;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.MapManager;
import com.github.imdabigboss.kitduels.YMLUtils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
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

    public KitDuelsCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
        this.mapsYML = KitDuels.getMapsYML();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
        } else {
            if (args[0].equals("add")) {
                if (args.length == 2) {
                    if (!mapsYML.getConfig().contains(args[1] + ".world")) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(ChatColor.RED + "You must be a player to add maps!");
                            return true;
                        }
                        Player player = (Player) sender;

                        KitDuels.editModePlayers.put(player, args[1]);
                        mapsYML.getConfig().set(args[1] + ".world", player.getLocation());
                        sender.sendMessage("The map was added! Please set the spawns using: " + ChatColor.GOLD + "/kd maxPlayers <number>" + ChatColor.RESET + " and " + ChatColor.GOLD + "/kd spawn <number>" + ChatColor.RESET + ". Then enable the map: " + ChatColor.GOLD + "/kd enable <name>");
                    } else {
                        sender.sendMessage(ChatColor.RED + "That map already exists!");
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("create")) {
                if (args.length == 2) {
                    if (!mapsYML.getConfig().contains(args[1] + ".world")) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(ChatColor.RED + "You must be a player to add maps!");
                            return true;
                        }
                        Player player = (Player) sender;

                        sender.sendMessage(ChatColor.AQUA + "Creating world...");
                        MapManager.createMap(args[1]);
                        sender.sendMessage(ChatColor.AQUA + "Done!");
                        player.setGameMode(GameMode.CREATIVE);
                        player.teleport(KitDuels.getInstance().getServer().getWorld(args[1]).getSpawnLocation());

                        KitDuels.editModePlayers.put(player, args[1]);
                        mapsYML.getConfig().set(args[1] + ".world", player.getLocation());
                        sender.sendMessage("The map was added! Please set the spawns using: " + ChatColor.GOLD + "/kd maxPlayers <number>" + ChatColor.RESET + " and " + ChatColor.GOLD + "/kd spawn <number>" + ChatColor.RESET + ". Then enable the map: " + ChatColor.GOLD + "/kd enable <name>");
                    } else {
                        sender.sendMessage(ChatColor.RED + "That map already exists!");
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
                            KitDuels.sendToSpawn(player);
                        }
                        KitDuels.enabledMaps.remove(args[1]);
                        List<String> keys = new ArrayList<>(KitDuels.enabledMaps.keySet());
                        plugin.getConfig().set("enabledMaps", keys);

                        if (KitDuels.allMaps.contains(args[1])) {
                            KitDuels.allMaps.remove(args[1]);
                            plugin.getConfig().set("allMaps", KitDuels.allMaps);
                            sender.sendMessage(ChatColor.AQUA + "The world will not be removed from your server's root directory and is not unloaded!");
                        }

                        mapsYML.getConfig().set(args[1], null);
                        sender.sendMessage(ChatColor.AQUA + "The map was deleted.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "That map does not exist!");
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("maxPlayers")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
                    return true;
                }

                Player player = (Player) sender;
                if (!KitDuels.editModePlayers.containsKey(player)) {
                    sender.sendMessage(ChatColor.RED + "You are not editing any map!");
                    return true;
                }

                if (args.length == 2) {
                    try {
                        int num = Integer.parseInt(args[1]);
                        if (num < 2) {
                            sender.sendMessage(ChatColor.RED + "A map can't have less than 2 players!");
                            return true;
                        }

                        mapsYML.getConfig().set(KitDuels.editModePlayers.get(player) + ".maxPlayers", num);
                        sender.sendMessage(ChatColor.AQUA + "You set the max number of players to " + num + " for this map!");
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "That is not a number!");
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("spawn")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
                    return true;
                }

                Player player = (Player) sender;
                if (!KitDuels.editModePlayers.containsKey(player)) {
                    sender.sendMessage(ChatColor.RED + "You are not editing any map!");
                    return true;
                }

                if (args.length == 2) {
                    if (mapsYML.getConfig().contains(KitDuels.editModePlayers.get(player) + ".maxPlayers")) {
                        int maxnum = mapsYML.getConfig().getInt(KitDuels.editModePlayers.get(player) + ".maxPlayers");

                        try {
                            int num = Integer.parseInt(args[1]);
                            if (num <= 0) {
                                sender.sendMessage(ChatColor.RED + "You can't set a negative or null number as a spawn point!");
                                return true;
                            }
                            if (num > maxnum) {
                                sender.sendMessage(ChatColor.RED + "You set your max number of players to " + maxnum);
                                return true;
                            }

                            mapsYML.getConfig().set(KitDuels.editModePlayers.get(player) + ".spawn" + num, player.getLocation());
                            sender.sendMessage(ChatColor.AQUA + "You set the spawn point number " + num + " out of " + maxnum);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "That is not a number!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You need to have set a max number of players to run this.");
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("save")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
                    return true;
                }

                Player player = (Player) sender;
                if (!KitDuels.editModePlayers.containsKey(player)) {
                    sender.sendMessage(ChatColor.RED + "You are not editing any map!");
                    return true;
                }

                KitDuels.editModePlayers.remove(player);
                KitDuels.sendToSpawn(player);
                sender.sendMessage(ChatColor.AQUA + "You exited edit mode!");
            } else if (args[0].equals("edit")) {
                if (args.length == 2) {
                    if (mapsYML.getConfig().contains(args[1] + ".world")) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(ChatColor.RED + "You must be a player to add maps!");
                            return true;
                        }
                        Player player = (Player) sender;

                        KitDuels.editModePlayers.put(player, args[1]);
                        Location location = (Location) mapsYML.getConfig().get(args[1] + ".world");
                        player.teleport(location);
                        player.setGameMode(GameMode.CREATIVE);
                        player.getInventory().clear();
                        sender.sendMessage(ChatColor.AQUA + "You are now editing " + args[1] + "!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "That map does not exist!");
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("lobbySpawn")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
                    return true;
                }

                Player player = (Player) sender;
                plugin.getConfig().set("lobbySpawn", player.getLocation());
                sender.sendMessage(ChatColor.AQUA + "You have set the lobby spawn position here!");
            } else if (args[0].equals("list")) {
                Set<String> keys = mapsYML.getConfig().getKeys(false);
                String maps = "The existing maps are:";
                for (String key : keys) {
                    maps += "\n - " + key;
                    if (KitDuels.enabledMaps.containsKey(key)) {
                        maps += ChatColor.GREEN + " ENABLED" + ChatColor.RESET;
                    } else {
                        maps += ChatColor.RED + " DISABLED" + ChatColor.RESET;
                    }
                }
                sender.sendMessage(maps);
            } else if (args[0].equals("enable")) {
                if (args.length == 2) {
                    if (!KitDuels.enabledMaps.containsKey(args[1])) {
                        KitDuels.enabledMaps.put(args[1], new ArrayList<>());
                        List<String> keys = new ArrayList<>(KitDuels.enabledMaps.keySet());
                        plugin.getConfig().set("enabledMaps", keys);
                        sender.sendMessage(ChatColor.AQUA + "The map has been enabled!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "That map is already enabled!");
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
                        sender.sendMessage(ChatColor.AQUA + "The map has been disabled!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "That map is not enabled!");
                    }
                } else {
                    sendHelp(sender);
                }
            } else {
                sendHelp(sender);
            }
        }

        plugin.saveConfig();
        mapsYML.saveConfig();
        return true;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage("The correct usage is:\n - add <name>\n - create <name>\n - delete <name>\n - maxPlayers <number>\n - spawn <number>\n - save\n - edit <name>\n - lobbySpawn\n - list\n - enable <name>\n - disable <name>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> cmds = new ArrayList<>();
        if (args.length == 1) {
            cmds.add("add");
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

        } else if (args.length == 2) {
            if (args[0].equals("edit") || args[0].equals("delete") || args[0].equals("enable") || args[0].equals("disable")) {
                cmds.addAll(mapsYML.getConfig().getKeys(false));
            }
        }
        return cmds;
    }
}
