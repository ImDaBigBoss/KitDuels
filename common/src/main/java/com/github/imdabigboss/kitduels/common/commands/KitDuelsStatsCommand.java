package com.github.imdabigboss.kitduels.common.commands;

import com.github.imdabigboss.kitduels.common.KitDuels;
import com.github.imdabigboss.kitduels.common.interfaces.CommonCommandSender;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.managers.TextManager;
import com.github.imdabigboss.kitduels.common.util.PlayerStats;

public class KitDuelsStatsCommand {
    private TextManager textManager;
    private KitDuels plugin;

    public KitDuelsStatsCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
        this.textManager = plugin.getTextManager();
    }

    public boolean runCommand(CommonCommandSender sender, String label, String[] args) {
        if (sender.isConsole()) {
            sender.sendMessage(textManager.get("general.errors.notPlayer"));
            return true;
        }

        CommonPlayer player = sender.getPlayer();
        PlayerStats playerStats = plugin.getStatsManager().getPlayerStats(player);

        String message = this.textManager.get("stats.header");
        message += "\n " + this.textManager.get("stats.wins", playerStats.getWins());
        message += "\n " + this.textManager.get("stats.losses", playerStats.getLosses());
        message += "\n " + this.textManager.get("stats.kills", playerStats.getKills());
        message += "\n " + this.textManager.get("stats.deaths", playerStats.getDeaths());

        sender.sendMessage(message);
        return true;
    }
}
