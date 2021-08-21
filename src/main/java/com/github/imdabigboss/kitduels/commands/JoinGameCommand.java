package com.github.imdabigboss.kitduels.commands;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.MapManager;
import com.github.imdabigboss.kitduels.YMLUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JoinGameCommand implements CommandExecutor, TabExecutor {
    private KitDuels plugin;
    private YMLUtils mapsYML;

    public JoinGameCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
        this.mapsYML = KitDuels.getMapsYML();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }

        if (KitDuels.editModePlayers.containsKey((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "You are currently editing a map!");
        }
        if (KitDuels.playerMaps.containsKey((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "You are already in a game!");
            return true;
        }

        int playerNum = 0;
        String mapName = "";

        boolean selectedMap = false;
        boolean selectedPlayerNum = false;

        if (args.length == 1) {
            try {
                selectedPlayerNum = true;
                playerNum = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                mapName = args[0];
                selectedMap = true;
            }
        }

        if (selectedMap) {
            if (KitDuels.enabledMaps.containsKey(mapName)) {
                if (KitDuels.enabledMaps.get(mapName).size() >= MapManager.getMapMaxPlayers(mapName)) {
                    sender.sendMessage(ChatColor.RED + "That map has no slots available!");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "That map does not exist!");
                return true;
            }
        } else {
            if (selectedPlayerNum) {
                if (playerNum < 2) {
                    sender.sendMessage(ChatColor.RED + "You can't get a map with that number of players!");
                    return true;
                }
                mapName = MapManager.getNextMap(playerNum);
            } else {
                mapName = MapManager.getNextMap();
            }
        }

        if (mapName == "") {
            if (selectedPlayerNum) {
                sender.sendMessage(ChatColor.RED + "Either no slots, no maps, or both are available for the selected amount of players!");
            } else {
                sender.sendMessage(ChatColor.RED + "Either no slots or no maps are available!");
            }
            return true;
        }

        MapManager.queuePlayerToMap((Player) sender, mapName);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> cmds = new ArrayList<>();
        if (args.length == 1) {
            cmds.addAll(KitDuels.enabledMaps.keySet());
            cmds.add("2");
            cmds.add("3");
            cmds.add("4");
        }
        return cmds;
    }
}
