package com.github.imdabigboss.kitduels.util;

import com.github.imdabigboss.kitduels.managers.StatsManager;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;

public class KitDuelsPlaceholderReplacer implements PlaceholderReplacer {
    private int leaderboardLine;

    public KitDuelsPlaceholderReplacer(int line) {
        super();
        this.leaderboardLine = line;
    }

    @Override
    public String update() {
        return StatsManager.getStatLine(this.leaderboardLine);
    }
}
