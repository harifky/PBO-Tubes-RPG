package com.elemental.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * FR-SAVE-002: Save Metadata for display in load menu
 */
public class SaveMetadata {
    private String slotName;
    private LocalDateTime savedAt;
    private int totalBattles;
    private int highestLevel;
    private String highestLevelCharName;

    public SaveMetadata() {
    }

    /**
     * Create metadata from SaveData
     */
    public static SaveMetadata create(int slotNumber, SaveData saveData) {
        SaveMetadata meta = new SaveMetadata();
        meta.slotName = "Save Slot " + slotNumber;
        meta.savedAt = saveData.getSavedAt();
        meta.totalBattles = saveData.getBattleHistory().getTotalBattles();

        // Find highest level character
        int maxLevel = 0;
        String charName = "None";
        for (CharacterData c : saveData.getCharacters()) {
            if (c.getLevel() > maxLevel) {
                maxLevel = c.getLevel();
                charName = c.getName();
            }
        }
        meta.highestLevel = maxLevel;
        meta.highestLevelCharName = charName;

        return meta;
    }

    /**
     * Format for display in menu
     */
    public String getDisplayString() {
        if (savedAt == null) {
            return slotName + " (Empty)";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String dateStr = savedAt.format(formatter);

        return String.format("%s", slotName) +
                String.format("\n  Saved: %s", dateStr) +
                String.format("\n  Battles: %d | Highest Level: %d (%s)",
                        totalBattles, highestLevel, highestLevelCharName);
    }

    // Getters and setters
    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public int getTotalBattles() {
        return totalBattles;
    }

    public void setTotalBattles(int totalBattles) {
        this.totalBattles = totalBattles;
    }

    public int getHighestLevel() {
        return highestLevel;
    }

    public void setHighestLevel(int highestLevel) {
        this.highestLevel = highestLevel;
    }

    public String getHighestLevelCharName() {
        return highestLevelCharName;
    }

    public void setHighestLevelCharName(String highestLevelCharName) {
        this.highestLevelCharName = highestLevelCharName;
    }
}
