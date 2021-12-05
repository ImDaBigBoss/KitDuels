package com.github.imdabigboss.kitduels.common.managers;

import com.github.imdabigboss.kitduels.common.KitDuels;
import com.github.imdabigboss.kitduels.common.interfaces.CommonOfflinePlayer;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.util.PlayerStats;
import com.github.imdabigboss.kitduels.common.util.YMLUtils;

import java.util.*;

public class StatsManager {
    private KitDuels plugin;
    private YMLUtils statsYML;

    private Map<Integer, String> playerRankings = new HashMap<>();
    private Map<String, PlayerStats> playerData = new HashMap<>();

    public StatsManager(KitDuels plugin) {
        this.plugin = plugin;
        this.statsYML = plugin.getStatsYML();
    }

    public void loadStats() {
        List<String> players = new ArrayList<>(this.plugin.getStatsYML().getKeys(false));
        for (String playerUUID : players) {
            CommonOfflinePlayer offlinePlayer = this.plugin.getPlayerUtils().getOfflinePlayer(UUID.fromString(playerUUID));
            if (offlinePlayer == null) {
                this.plugin.getLog().warning(this.plugin.getTextManager().get("general.warning.uuidWasSkipped", playerUUID));
                continue;
            }

            String name = offlinePlayer.getName();
            PlayerStats playerStats = new PlayerStats();
            if (this.statsYML.contains(playerUUID + ".wins")) {
                playerStats.setWins(this.statsYML.getInt(playerUUID + ".wins"));
            }
            if (this.statsYML.contains(playerUUID + ".losses")) {
                playerStats.setLosses(this.statsYML.getInt(playerUUID + ".losses"));
            }
            if (this.statsYML.contains(playerUUID + ".kills")) {
                playerStats.setKills(this.statsYML.getInt(playerUUID + ".kills"));
            }
            if (this.statsYML.contains(playerUUID + ".deaths")) {
                playerStats.setDeaths(this.statsYML.getInt(playerUUID + ".deaths"));
            }

            playerData.put(name, playerStats);
        }

        createRanking();
    }

    public void createRanking() {
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

    public void addPlayerWin(CommonPlayer player) {
        int num;
        if (playerData.containsKey(player.getName())) {
            num = playerData.get(player.getName()).addWin();
        } else {
            num = 1;
            playerData.put(player.getName(), new PlayerStats(num, 0, 0, 0));
        }

        createRanking();

        this.statsYML.set(player.getUUID() + ".wins", num);
        this.statsYML.saveConfig();
    }

    public void addPlayerLoss(CommonPlayer player) {
        int num;
        if (playerData.containsKey(player.getName())) {
            num = playerData.get(player.getName()).addLoss();
        } else {
            num = 1;
            playerData.put(player.getName(), new PlayerStats(0, 1, 0, 0));
        }

        this.statsYML.set(player.getUUID() + ".losses", num);
        this.statsYML.saveConfig();
    }

    public void addPlayerKill(CommonPlayer player) {
        int num;
        if (playerData.containsKey(player.getName())) {
            num = playerData.get(player.getName()).addKill();
        } else {
            num = 1;
            playerData.put(player.getName(), new PlayerStats(0, 0, 1, 0));
        }

        this.statsYML.set(player.getUUID() + ".kills", num);
        this.statsYML.saveConfig();
    }

    public void addPlayerDeath(CommonPlayer player) {
        int num;
        if (playerData.containsKey(player.getName())) {
            num = playerData.get(player.getName()).addDeath();
        } else {
            num = 1;
            playerData.put(player.getName(), new PlayerStats(0, 0, 0, 1));
        }

        this.statsYML.set(player.getUUID() + ".deaths", num);
        this.statsYML.saveConfig();
    }

    public PlayerStats getPlayerStats(CommonPlayer player) {
        return playerData.get(player.getName());
    }

    public String getStatLine(int line) {
        if (plugin.getStatsManager().playerRankings.containsKey(line)) {
            String player = plugin.getStatsManager().playerRankings.get(line);
            return this.plugin.getTextManager().get("hologram.line", line, player, plugin.getStatsManager().playerData.get(player).getWins());
        } else {
            return this.plugin.getTextManager().get("hologram.lineNoData");
        }
    }
}
