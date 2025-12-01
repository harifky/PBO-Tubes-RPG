package com.elemental.model;

/**
 * FR-SAVE-001: Battle History Data Transfer Object
 */
public class BattleHistoryData {
    private int totalBattles;
    private int wins;
    private int losses;
    private int totalEnemiesDefeated;

    public BattleHistoryData(int totalBattles, int wins, int losses, int totalEnemiesDefeated) {
        this.totalBattles = totalBattles;
        this.wins = wins;
        this.losses = losses;
        this.totalEnemiesDefeated = totalEnemiesDefeated;
    }

    // Getters
    public int getTotalBattles() {
        return totalBattles;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getTotalEnemiesDefeated() {
        return totalEnemiesDefeated;
    }
}
