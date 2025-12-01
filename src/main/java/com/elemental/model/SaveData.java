package com.elemental.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * FR-SAVE-001: Main save data container
 * Contains all data to be saved in JSON format
 */
public class SaveData {
    private String saveId;
    private LocalDateTime savedAt;
    private int saveVersion;

    // Game data
    private List<CharacterData> characters;
    private BattleHistoryData battleHistory;
    private GameSettingsData settings;

    // Metadata for display
    private SaveMetadata metadata;

    public SaveData() {
        this.saveId = UUID.randomUUID().toString();
        this.savedAt = LocalDateTime.now();
        this.saveVersion = 1;
    }

    // Getters and Setters
    public String getSaveId() {
        return saveId;
    }

    public void setSaveId(String saveId) {
        this.saveId = saveId;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public int getSaveVersion() {
        return saveVersion;
    }

    public void setSaveVersion(int saveVersion) {
        this.saveVersion = saveVersion;
    }

    public List<CharacterData> getCharacters() {
        return characters;
    }

    public void setCharacters(List<CharacterData> characters) {
        this.characters = characters;
    }

    public BattleHistoryData getBattleHistory() {
        return battleHistory;
    }

    public void setBattleHistory(BattleHistoryData battleHistory) {
        this.battleHistory = battleHistory;
    }

    public GameSettingsData getSettings() {
        return settings;
    }

    public void setSettings(GameSettingsData settings) {
        this.settings = settings;
    }

    public SaveMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SaveMetadata metadata) {
        this.metadata = metadata;
    }
}
