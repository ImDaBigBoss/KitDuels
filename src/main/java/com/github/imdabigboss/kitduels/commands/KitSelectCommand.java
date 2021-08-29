package com.github.imdabigboss.kitduels.commands;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.YMLUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.mozilla.javascript.Kit;

import java.util.List;

public class KitSelectCommand implements CommandExecutor, TabExecutor {
    private KitDuels plugin;

    public KitSelectCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used as a player!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            KitDuels.openKitSelectGUIPlayer(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("random")) {
            KitDuels.playerKits.remove(player);
        } else {
            if (!KitDuels.allKits.contains(args[0])) {
                sender.sendMessage(ChatColor.RED + "That kit does not exist!");
                return true;
            }
            KitDuels.playerKits.put(player, args[0]);
        }

        sender.sendMessage(ChatColor.AQUA + "You have selected the " + args[0] + " kit!");
        return true;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage("The correct usage is: /kitselect <name>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return KitDuels.allKits;
    }
}
