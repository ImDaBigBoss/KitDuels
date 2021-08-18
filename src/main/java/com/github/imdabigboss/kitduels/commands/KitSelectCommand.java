package com.github.imdabigboss.kitduels.commands;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.YMLUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class KitSelectCommand implements CommandExecutor, TabExecutor {
    private KitDuels plugin;
    private YMLUtils mapsYML;

    public KitSelectCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
        this.mapsYML = KitDuels.getMapsYML();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used as a player!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            sendHelp(sender);
            return true;
        }

        if (!KitDuels.allKits.contains(args[0])) {
            sender.sendMessage(ChatColor.RED + "That kit does not exist!");
            return true;
        }

        KitDuels.playerKits.put(player, args[0]);
        sender.sendMessage(ChatColor.AQUA + "You have selected the " + args[0] + " kit!");
        return true;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage("The correct usage is: /kitselect <name>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> cmds = KitDuels.allKits;
        return cmds;
    }
}
