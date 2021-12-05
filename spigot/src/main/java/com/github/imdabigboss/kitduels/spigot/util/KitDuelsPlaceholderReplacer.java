package com.github.imdabigboss.kitduels.spigot.util;

import com.github.imdabigboss.kitduels.spigot.KitDuels;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;

public class KitDuelsPlaceholderReplacer implements PlaceholderReplacer {
    private KitDuels plugin;
    private int leaderboardLine;

    public KitDuelsPlaceholderReplacer(KitDuels plugin, int line) {
        super();
        this.plugin = plugin;
        this.leaderboardLine = line;
    }

    @Override
    public String update() {
        return plugin.getStatsManager().getStatLine(this.leaderboardLine);
    }
}
