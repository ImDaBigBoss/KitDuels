package com.github.imdabigboss.kitduels.managers;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.util.KitDuelsPlaceholderReplacer;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import org.bukkit.Location;

public class HologramManager {
    public static int leaderboardLines = 5;

    public static void updateHolo(Location location) {
        deleteAllHolos();

        Hologram hologram = HologramsAPI.createHologram(KitDuels.getInstance(), location);
        hologram.appendTextLine(KitDuels.getTextManager().get("hologram.header"));
        for (int i = 1; i <= leaderboardLines; i++) {
            hologram.appendTextLine("{kitduels_stats_" + i + "}");
        }

        hologram.setAllowPlaceholders(true);
    }

    public static void deleteAllHolos() {
        for (Hologram hologram : HologramsAPI.getHolograms(KitDuels.getInstance())) {
            hologram.delete();
        }
    }

    public static void registerPlaceholders() {
        for (int i = 1; i <= leaderboardLines; i++) {
            HologramsAPI.registerPlaceholder(KitDuels.getInstance(), "{kitduels_stats_" + i + "}", 10, new KitDuelsPlaceholderReplacer(i));
        }
    }
}
