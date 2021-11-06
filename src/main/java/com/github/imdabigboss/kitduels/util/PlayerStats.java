package com.github.imdabigboss.kitduels.util;

public class PlayerStats {
    private int wins;
    private int losses;
    private int kills;
    private int deaths;

    public PlayerStats() {
        this.wins = 0;
        this.losses = 0;
        this.kills = 0;
        this.deaths = 0;
    }
    public PlayerStats(int wins, int losses, int kills, int deaths) {
        this.wins = wins;
        this.losses = losses;
        this.kills = kills;
        this.deaths = deaths;
    }

    public int getWins() {
        return wins;
    }
    public int addWin() {
        return this.wins++;
    }
    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }
    public int addLoss() {
        return this.losses++;
    }
    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getKills() {
        return kills;
    }
    public int addKill() {
        return this.kills++;
    }
    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }
    public int addDeath() {
        return this.deaths++;
    }
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
}
