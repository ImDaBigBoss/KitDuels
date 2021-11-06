package com.github.imdabigboss.kitduels.managers;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.util.PlayerStats;
import com.github.imdabigboss.kitduels.util.PlayerUtils;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class StatsManager {
    private static Map<Integer, String> playerRankings = new HashMap<>();
    private static Map<String, PlayerStats> playerData = new HashMap<>();

    public static void loadStats() {
        List<String> players = new ArrayList<>(KitDuels.getStatsYML().getConfig().getKeys(false));
        for (String playerUUID : players) {
            OfflinePlayer offlinePlayer = PlayerUtils.getOfflinePlayer(UUID.fromString(playerUUID));
            if (offlinePlayer == null) {
                KitDuels.getLog().warning(KitDuels.getTextManager().get("general.warning.uuidWasSkipped", playerUUID));
                continue;
            }

            String name = offlinePlayer.getName();
            PlayerStats playerStats = new PlayerStats();
            if (KitDuels.getStatsYML().getConfig().contains(playerUUID + ".wins")) {
                playerStats.setWins(KitDuels.getStatsYML().getConfig().getInt(playerUUID + ".wins"));
            }
            if (KitDuels.getStatsYML().getConfig().contains(playerUUID + ".losses")) {
                playerStats.setLosses(KitDuels.getStatsYML().getConfig().getInt(playerUUID + ".losses"));
            }
            if (KitDuels.getStatsYML().getConfig().contains(playerUUID + ".kills")) {
                playerStats.setKills(KitDuels.getStatsYML().getConfig().getInt(playerUUID + ".kills"));
            }
            if (KitDuels.getStatsYML().getConfig().contains(playerUUID + ".deaths")) {
                playerStats.setDeaths(KitDuels.getStatsYML().getConfig().getInt(playerUUID + ".deaths"));
            }

            playerData.put(name, playerStats);
        }

        createRanking();
    }

    public static void createRanking() {
        playerRankings.clear();
        Map<String, PlayerStats> playerRankingsTEMP = new HashMap<>(playerData);
        int size = playerRankingsTEMP.size();
        for (int i = 1; i <= size; i++) {
            int higest = 0;
            String player = "";
            for (String playerName : playerRankingsTEMP.keySet()) {
                int wins = playerRankingsTEMP.get(playerName).getWins();
                if (wins >= higest) {
                    higest = wins;
                    player = playerName;
                }
            }

            playerRankings.put(i, player);
            playerRankingsTEMP.remove(player);
        }
    }

    public static void addPlayerWin(Player player) {
        int num;
        if (playerData.containsKey(player.getName())) {
            num = playerData.get(player.getName()).addWin();
        } else {
            num = 1;
            playerData.put(player.getName(), new PlayerStats(num, 0, 0, 0));
        }

        createRanking();

        KitDuels.getStatsYML().getConfig().set(player.getUniqueId() + ".wins", num);
        KitDuels.getStatsYML().saveConfig();
    }

    public static void addPlayerLoss(Player player) {
        int num;
        if (playerData.containsKey(player.getName())) {
            num = playerData.get(player.getName()).addLoss();
        } else {
            num = 1;
            playerData.put(player.getName(), new PlayerStats(0, 1, 0, 0));
        }

        KitDuels.getStatsYML().getConfig().set(player.getUniqueId() + ".losses", num);
        KitDuels.getStatsYML().saveConfig();
    }

    public static void addPlayerKill(Player player) {
        int num;
        if (playerData.containsKey(player.getName())) {
            num = playerData.get(player.getName()).addKill();
        } else {
            num = 1;
            playerData.put(player.getName(), new PlayerStats(0, 0, 1, 0));
        }

        KitDuels.getStatsYML().getConfig().set(player.getUniqueId() + ".kills", num);
        KitDuels.getStatsYML().saveConfig();
    }

    public static void addPlayerDeath(Player player) {
        int num;
        if (playerData.containsKey(player.getName())) {
            num = playerData.get(player.getName()).addDeath();
        } else {
            num = 1;
            playerData.put(player.getName(), new PlayerStats(0, 0, 0, 1));
        }

        KitDuels.getStatsYML().getConfig().set(player.getUniqueId() + ".deaths", num);
        KitDuels.getStatsYML().saveConfig();
    }

    public static PlayerStats getPlayerStats(Player player) {
        return playerData.get(player.getName());
    }

    public static String getStatLine(int line) {
        if (StatsManager.playerRankings.containsKey(line)) {
            String player = StatsManager.playerRankings.get(line);
            return KitDuels.getTextManager().get("hologram.line", line, player, StatsManager.playerData.get(player).getWins());
        } else {
            return KitDuels.getTextManager().get("hologram.lineNoData");
        }
    }
}
