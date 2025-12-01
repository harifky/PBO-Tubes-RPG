package com.elemental.model;

/**
 * FR-SAVE-001: Battle History Tracker
 * Tracks wins, losses, total battles
 */
public class BattleHistory {
    private int totalBattles;
    private int wins;
    private int losses;
    private int totalEnemiesDefeated;

    public BattleHistory() {
        this.totalBattles = 0;
        this.wins = 0;
        this.losses = 0;
        this.totalEnemiesDefeated = 0;
    }

    /**
     * Record battle result
     */
    public void recordBattle(BattleStatus status, int enemiesDefeated) {
        totalBattles++;
        if (status == BattleStatus.VICTORY) {
            wins++;
            totalEnemiesDefeated += enemiesDefeated;
        } else if (status == BattleStatus.DEFEAT) {
            losses++;
        }
    }

    /**
     * Convert to DTO for saving
     */
    public BattleHistoryData toData() {
        return new BattleHistoryData(totalBattles, wins, losses, totalEnemiesDefeated);
    }

    /**
     * Load from DTO
     */
    public void loadFromData(BattleHistoryData data) {
        this.totalBattles = data.getTotalBattles();
        this.wins = data.getWins();
        this.losses = data.getLosses();
        this.totalEnemiesDefeated = data.getTotalEnemiesDefeated();
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

    @Override
    public String toString() {
        return String.format("Battles: %d | Wins: %d | Losses: %d | Enemies Defeated: %d",
                totalBattles, wins, losses, totalEnemiesDefeated);
    }
}
