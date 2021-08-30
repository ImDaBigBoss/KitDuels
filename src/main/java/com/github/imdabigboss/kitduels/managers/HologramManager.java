package com.github.imdabigboss.kitduels.managers;

import com.github.imdabigboss.kitduels.KitDuels;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class HologramManager {
    public static void updateHolo(Location location) {
        deleteAllHolos();

        Hologram hologram = HologramsAPI.createHologram(KitDuels.getInstance(), location);
        hologram.appendTextLine(ChatColor.YELLOW + "Most all time wins:");
        hologram.appendTextLine("{kitduels_stats_1}");
        hologram.appendTextLine("{kitduels_stats_2}");
        hologram.appendTextLine("{kitduels_stats_3}");
        hologram.appendTextLine("{kitduels_stats_4}");
        hologram.appendTextLine("{kitduels_stats_5}");

        hologram.setAllowPlaceholders(true);
    }

    public static void deleteAllHolos() {
        for (Hologram hologram : HologramsAPI.getHolograms(KitDuels.getInstance())) {
            hologram.delete();
        }
    }

    public static void registerPlaceholders() {
        HologramsAPI.registerPlaceholder(KitDuels.getInstance(), "{kitduels_stats_1}", 10, new PlaceholderReplacer() {
            @Override
            public String update() {
                return StatsManager.getStatLine(1);
            }
        });
        HologramsAPI.registerPlaceholder(KitDuels.getInstance(), "{kitduels_stats_2}", 10, new PlaceholderReplacer() {
            @Override
            public String update() {
                return StatsManager.getStatLine(2);
            }
        });
        HologramsAPI.registerPlaceholder(KitDuels.getInstance(), "{kitduels_stats_3}", 10, new PlaceholderReplacer() {
            @Override
            public String update() {
                return StatsManager.getStatLine(3);
            }
        });
        HologramsAPI.registerPlaceholder(KitDuels.getInstance(), "{kitduels_stats_4}", 10, new PlaceholderReplacer() {
            @Override
            public String update() {
                return StatsManager.getStatLine(4);
            }
        });
        HologramsAPI.registerPlaceholder(KitDuels.getInstance(), "{kitduels_stats_5}", 10, new PlaceholderReplacer() {
            @Override
            public String update() {
                return StatsManager.getStatLine(5);
            }
        });
    }
}
