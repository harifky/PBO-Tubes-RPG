package com.elemental.model;

/**
 * Game Settings - Global configuration
 * Used to store player preferences including AI difficulty
 */
public class GameSettings {

    // Singleton instance
    private static GameSettings instance;

    // Settings
    private AIDifficulty aiDifficulty;
    private boolean showDetailedLog;
    private boolean autoProgress; // Auto press enter after action

    // Private constructor for singleton
    private GameSettings() {
        // Default settings
        this.aiDifficulty = AIDifficulty.MEDIUM;
        this.showDetailedLog = true;
        this.autoProgress = false;
    }

    /**
     * Get singleton instance
     */
    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    // Getters
    public AIDifficulty getAIDifficulty() {
        return aiDifficulty;
    }

    public boolean isShowDetailedLog() {
        return showDetailedLog;
    }

    public boolean isAutoProgress() {
        return autoProgress;
    }

    // Setters
    public void setAIDifficulty(AIDifficulty aiDifficulty) {
        this.aiDifficulty = aiDifficulty;
    }

    public void setShowDetailedLog(boolean showDetailedLog) {
        this.showDetailedLog = showDetailedLog;
    }

    public void setAutoProgress(boolean autoProgress) {
        this.autoProgress = autoProgress;
    }

    /**
     * Reset to default settings
     */
    public void resetToDefaults() {
        this.aiDifficulty = AIDifficulty.MEDIUM;
        this.showDetailedLog = true;
        this.autoProgress = false;
    }

    /**
     * Display current settings
     */
    public String displaySettings() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════╗\n");
        sb.append("║            GAME SETTINGS                     ║\n");
        sb.append("╚══════════════════════════════════════════════╝\n");
        sb.append("\n");
        sb.append("AI Difficulty:    ").append(aiDifficulty).append("\n");
        sb.append("Detailed Log:     ").append(showDetailedLog ? "ON" : "OFF").append("\n");
        sb.append("Auto Progress:    ").append(autoProgress ? "ON" : "OFF").append("\n");
        return sb.toString();
    }

    /**
     * FR-SAVE-001: Convert to DTO for saving
     */
    public GameSettingsData toData() {
        return new GameSettingsData(aiDifficulty, showDetailedLog);
    }

    /**
     * FR-SAVE-002: Load from DTO
     */
    public void loadFromData(GameSettingsData data) {
        this.aiDifficulty = data.getAiDifficulty();
        this.showDetailedLog = data.isShowDetailedLog();
    }
}
