package com.github.imdabigboss.kitduels.commands;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.managers.MapManager;
import com.github.imdabigboss.kitduels.managers.TextManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LeaveGameCommand implements CommandExecutor, TabExecutor {
    private TextManager textManager;

    public LeaveGameCommand(KitDuels plugin) {
        super();
        this.textManager = KitDuels.getTextManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(textManager.get("general.errors.notPlayer"));
            return true;
        }
        Player player = (Player) sender;

        boolean out = MapManager.removePlayerFromMap(player);
        if (!out) {
            sender.sendMessage(textManager.get("general.errors.notInGame"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return new ArrayList<>();
    }
}
