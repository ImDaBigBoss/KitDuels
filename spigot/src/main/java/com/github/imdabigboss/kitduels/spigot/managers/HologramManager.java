package com.github.imdabigboss.kitduels.spigot.managers;

import com.github.imdabigboss.kitduels.common.interfaces.Location;
import com.github.imdabigboss.kitduels.spigot.KitDuels;
import com.github.imdabigboss.kitduels.spigot.util.KitDuelsPlaceholderReplacer;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class HologramManager implements com.github.imdabigboss.kitduels.common.managers.HologramManager {
    private KitDuels plugin;
    private int leaderboardLines = 5;

    public HologramManager(KitDuels plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setLeaderboardLines(int lines) {
        this.leaderboardLines = lines;
    }

    @Override
    public void updateHolo(Location location) {
        deleteAllHolos();

        Hologram hologram = HologramsAPI.createHologram(plugin, new com.github.imdabigboss.kitduels.spigot.interfaces.Location(location, plugin.getServer()).toBukkit());
        hologram.appendTextLine(plugin.getTextManager().get("hologram.header"));
        for (int i = 1; i <= leaderboardLines; i++) {
            hologram.appendTextLine("{kitduels_stats_" + i + "}");
        }

        hologram.setAllowPlaceholders(true);
    }

    @Override
    public void deleteAllHolos() {
        for (Hologram hologram : HologramsAPI.getHolograms(plugin)) {
            hologram.delete();
        }
    }

    @Override
    public void registerPlaceholders() {
        for (int i = 1; i <= leaderboardLines; i++) {
            HologramsAPI.registerPlaceholder(plugin, "{kitduels_stats_" + i + "}", 10, new KitDuelsPlaceholderReplacer(plugin, i));
        }
    }
}
