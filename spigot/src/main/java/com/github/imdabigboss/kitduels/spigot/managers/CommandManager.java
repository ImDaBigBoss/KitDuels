package com.github.imdabigboss.kitduels.spigot.managers;

import com.github.imdabigboss.kitduels.common.commands.*;
import com.github.imdabigboss.kitduels.spigot.KitDuels;

import com.github.imdabigboss.kitduels.spigot.interfaces.SpigotCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabExecutor {
    private KitDuels plugin;

    public CommandManager(KitDuels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kd")) {
            return new KitDuelsCommand(plugin).runCommand(new SpigotCommandSender(sender), label, args);
        } else if (command.getName().equalsIgnoreCase("kdkits")) {
            return new KitDuelsKitsCommand(plugin).runCommand(new SpigotCommandSender(sender), label, args);
        } else if (command.getName().equalsIgnoreCase("joingame")) {
            return new JoinGameCommand(plugin).runCommand(new SpigotCommandSender(sender), label, args);
        } else if (command.getName().equalsIgnoreCase("leavegame")) {
            return new LeaveGameCommand(plugin).runCommand(new SpigotCommandSender(sender), label, args);
        } else if (command.getName().equalsIgnoreCase("kitselect")) {
            return new KitSelectCommand(plugin).runCommand(new SpigotCommandSender(sender), label, args);
        } else if (command.getName().equalsIgnoreCase("kdstats")) {
            return new KitDuelsStatsCommand(plugin).runCommand(new SpigotCommandSender(sender), label, args);
        } else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> cmds = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("kdkits")) {
            if (args.length == 1) {
                cmds.add("add");
                cmds.add("delete");
                cmds.add("load");
                cmds.add("saveAs");
                cmds.add("list");

            } else if (args.length == 2) {
                if (args[0].equals("load") || args[0].equals("delete")) {
                    cmds.addAll(plugin.getKitManager().getKits());
                }
            }
        } else if (command.getName().equalsIgnoreCase("joingame")) {
            if (args.length == 1) {
                cmds.add("random");
                cmds.add("kit");
            }
        } else if (command.getName().equalsIgnoreCase("kd")) {
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
                cmds.add("setKit");

            } else if (args.length == 2) {
                if (args[0].equals("edit") || args[0].equals("delete") || args[0].equals("enable") || args[0].equals("disable")) {
                    cmds.addAll(plugin.getMapsYML().getKeys(false));
                } else if (args[0].equals("setKit")) {
                    cmds.addAll(plugin.getKitManager().getKits());
                    cmds.add("random");
                }
            }
        }
        return cmds;
    }
}
