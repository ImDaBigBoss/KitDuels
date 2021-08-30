package com.github.imdabigboss.kitduels.managers;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.util.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class StatsManager {
    public static Map<Integer, String> playerRankings = new HashMap<>();
    public static Map<String, Integer> playerWins = new HashMap<>();

    public static void loadStats() {
        List<String> players = new ArrayList<>(KitDuels.getStatsYML().getConfig().getKeys(false));
        for (String playerUUID : players) {
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayer(UUID.fromString(playerUUID));
            if (offlinePlayer == null) {
                KitDuels.getLog().warning("UUID in stats was skipped!");
                continue;
            }

            String name = offlinePlayer.getName();
            if (KitDuels.getStatsYML().getConfig().contains(playerUUID + ".wins")) {
                playerWins.put(name, KitDuels.getStatsYML().getConfig().getInt(playerUUID + ".wins"));
            } else {
                playerWins.put(name, 0);
            }
        }

        createRanking();
    }

    public static void createRanking() {
        playerRankings.clear();
        Map<String, Integer> playerWinsTEMP = new HashMap<>(playerWins);
        int size = playerWinsTEMP.size();
        for (int i = 1; i <= size; i++) {
            int higest = 0;
            String player = "";
            for (String playerName : playerWinsTEMP.keySet()) {
                if (playerWinsTEMP.get(playerName) >= higest) {
                    higest = playerWinsTEMP.get(playerName);
                    player = playerName;
                }
            }

            playerRankings.put(i, player);
            playerWinsTEMP.remove(player);
        }
    }

    public static void addPlayerWin(Player player) {
        int num;
        if (playerWins.containsKey(player.getName())) {
            num = playerWins.get(player.getName()) + 1;
            playerWins.replace(player.getName(), num);
        } else {
            num = 1;
            playerWins.put(player.getName(), 1);
        }

        createRanking();

        KitDuels.getStatsYML().getConfig().set(player.getUniqueId().toString() + ".wins", num);
        KitDuels.getStatsYML().saveConfig();
    }

    public static String getStatLine(int line) {
        if (StatsManager.playerRankings.containsKey(line)) {
            String player = StatsManager.playerRankings.get(line);
            return player + " " + ChatColor.AQUA + StatsManager.playerWins.get(player);
        } else {
            return "No data available";
        }
    }
}
