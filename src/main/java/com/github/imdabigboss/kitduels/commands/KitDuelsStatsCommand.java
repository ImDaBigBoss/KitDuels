package com.github.imdabigboss.kitduels.commands;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.managers.StatsManager;
import com.github.imdabigboss.kitduels.managers.TextManager;
import com.github.imdabigboss.kitduels.util.PlayerStats;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitDuelsStatsCommand implements CommandExecutor {
    public TextManager textManager;

    public KitDuelsStatsCommand(KitDuels plugin) {
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
        PlayerStats playerStats = StatsManager.getPlayerStats(player);

        String message = this.textManager.get("stats.header");
        message += "\n " + this.textManager.get("stats.wins", playerStats.getWins());
        message += "\n " + this.textManager.get("stats.losses", playerStats.getLosses());
        message += "\n " + this.textManager.get("stats.kills", playerStats.getKills());
        message += "\n " + this.textManager.get("stats.deaths", playerStats.getDeaths());

        sender.sendMessage(message);
        return true;
    }
}
