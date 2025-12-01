package com.elemental.model;

/**
 * FR-SAVE-001: Game Settings Data Transfer Object
 */
public class GameSettingsData {
    private AIDifficulty aiDifficulty;
    private boolean showDetailedLog;

    public GameSettingsData(AIDifficulty aiDifficulty, boolean showDetailedLog) {
        this.aiDifficulty = aiDifficulty;
        this.showDetailedLog = showDetailedLog;
    }

    // Getters
    public AIDifficulty getAiDifficulty() {
        return aiDifficulty;
    }

    public boolean isShowDetailedLog() {
        return showDetailedLog;
    }
}
