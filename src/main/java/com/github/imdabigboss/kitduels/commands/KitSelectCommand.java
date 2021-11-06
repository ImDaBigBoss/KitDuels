package com.github.imdabigboss.kitduels.commands;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.managers.GameManager;
import com.github.imdabigboss.kitduels.managers.TextManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class KitSelectCommand implements CommandExecutor, TabExecutor {
    private KitDuels plugin;
    private TextManager textManager;

    public KitSelectCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
        this.textManager = KitDuels.getTextManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(textManager.get("general.errors.notPlayer"));
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            GameManager.openKitSelectGUIPlayer(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("random")) {
            KitDuels.playerKits.remove(player);
        } else {
            if (!KitDuels.allKits.contains(args[0])) {
                sender.sendMessage(textManager.get("general.errors.kitDoesntExist"));
                return true;
            }
            KitDuels.playerKits.put(player, args[0]);
        }

        sender.sendMessage(textManager.get("general.info.kitSelected", args[0]));
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
