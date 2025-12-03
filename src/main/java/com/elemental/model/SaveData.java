package com.elemental.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map; // Tambahkan import ini
import java.util.UUID;

public class SaveData {
    // ... variable lama (saveId, savedAt, dll) tetap ada ...
    private String saveId;
    private LocalDateTime savedAt;
    private int saveVersion;
    private List<CharacterData> characters;
    private BattleHistoryData battleHistory;
    private GameSettingsData settings;
    private SaveMetadata metadata;

    // BARU: Global Inventory Data
    private Map<String, Integer> globalInventory;

    public SaveData() {
        this.saveId = UUID.randomUUID().toString();
        this.savedAt = LocalDateTime.now();
        this.saveVersion = 1;
    }

    // Getter & Setter Baru
    public Map<String, Integer> getGlobalInventory() {
        return globalInventory;
    }

    public void setGlobalInventory(Map<String, Integer> globalInventory) {
        this.globalInventory = globalInventory;
    }

    // ... (Getter Setter lama tetap dibiarkan) ...
    public String getSaveId() { return saveId; }
    public void setSaveId(String saveId) { this.saveId = saveId; }
    public LocalDateTime getSavedAt() { return savedAt; }
    public void setSavedAt(LocalDateTime savedAt) { this.savedAt = savedAt; }
    public int getSaveVersion() { return saveVersion; }
    public void setSaveVersion(int saveVersion) { this.saveVersion = saveVersion; }
    public List<CharacterData> getCharacters() { return characters; }
    public void setCharacters(List<CharacterData> characters) { this.characters = characters; }
    public BattleHistoryData getBattleHistory() { return battleHistory; }
    public void setBattleHistory(BattleHistoryData battleHistory) { this.battleHistory = battleHistory; }
    public GameSettingsData getSettings() { return settings; }
    public void setSettings(GameSettingsData settings) { this.settings = settings; }
    public SaveMetadata getMetadata() { return metadata; }
    public void setMetadata(SaveMetadata metadata) { this.metadata = metadata; }
}