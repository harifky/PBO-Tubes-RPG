package com.elemental.service;

import com.elemental.factory.CharacterFactory;
import com.elemental.model.*;
import com.elemental.util.LocalDateTimeAdapter;
import com.google.gson.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * FR-SAVE-001, FR-SAVE-002, FR-SAVE-003: Save/Load Service
 * Handles game save/load operations with JSON format
 */
public class SaveLoadService {
    private static final String SAVE_DIR = "saves/";
    private static final int MAX_SLOTS = 3;
    private static final int CURRENT_VERSION = 1;

    private final Gson gson;
    private final CharacterService characterService;
    private BattleHistory battleHistory;
    private int currentSlot; // Track which slot is currently loaded/active

    public SaveLoadService(CharacterService characterService) {
        this.characterService = characterService;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        this.battleHistory = new BattleHistory();
        this.currentSlot = 1; // Default to slot 1
        ensureSaveDirectoryExists();
    }

    /**
     * FR-SAVE-001: Save game to specified slot
     */
    public boolean saveGame(int slotNumber) throws SaveException {
        validateSlotNumber(slotNumber);

        try {
            // 1. Collect current game state
            SaveData saveData = createSaveData();

            // 2. Generate metadata
            saveData.setMetadata(SaveMetadata.create(slotNumber, saveData));

            // 3. Convert to JSON
            String json = gson.toJson(saveData);

            // 4. Write to file
            Path savePath = getSaveFilePath(slotNumber);
            Files.writeString(savePath, json);

            // 5. Update current slot (so auto-save knows which slot to use)
            this.currentSlot = slotNumber;

            System.out.println("✓ Game saved to Slot " + slotNumber);
            return true;

        } catch (IOException e) {
            throw new SaveException("Failed to save game: " + e.getMessage());
        }
    }

    /**
     * FR-SAVE-002: Load game from specified slot
     */
    public SaveData loadGame(int slotNumber) throws SaveException {
        validateSlotNumber(slotNumber);

        if (!saveSlotExists(slotNumber)) {
            throw new SaveException("Save slot " + slotNumber + " is empty");
        }

        try {
            // 1. Read file
            Path savePath = getSaveFilePath(slotNumber);
            String json = Files.readString(savePath);

            // 2. Parse JSON
            SaveData saveData = gson.fromJson(json, SaveData.class);

            // 3. Validate data integrity
            if (!validateSaveData(saveData)) {
                throw new SaveException("Save data is corrupted or invalid");
            }

            // 4. Check version compatibility
            if (saveData.getSaveVersion() > CURRENT_VERSION) {
                throw new SaveException("Save file is from newer version");
            }

            // 5. Update current slot (so auto-save knows which slot to use)
            this.currentSlot = slotNumber;

            System.out.println("✓ Game loaded from Slot " + slotNumber);
            return saveData;

        } catch (JsonSyntaxException e) {
            throw new SaveException("Save file is corrupted");
        } catch (IOException e) {
            throw new SaveException("Failed to load game: " + e.getMessage());
        }
    }

    /**
     * Apply loaded save data to current game
     */
    public void applySaveData(SaveData saveData) {
        // 1. Clear current characters
        characterService.clearAllCharacters();

        // 2. Restore characters
        CharacterFactory factory = new CharacterFactory();
        for (CharacterData charData : saveData.getCharacters()) {
            com.elemental.model.Character character = charData.toCharacter(factory);
            characterService.addCharacter(character);
        }

        // 3. Restore battle history
        battleHistory.loadFromData(saveData.getBattleHistory());

        // 4. Restore settings
        GameSettings.getInstance().loadFromData(saveData.getSettings());

        System.out.println("✓ Game state restored");
    }

    /**
     * FR-SAVE-003: Delete save slot
     */
    public boolean deleteSave(int slotNumber) throws SaveException {
        validateSlotNumber(slotNumber);

        if (!saveSlotExists(slotNumber)) {
            System.out.println("Slot " + slotNumber + " is already empty");
            return false;
        }

        try {
            Path savePath = getSaveFilePath(slotNumber);
            Files.delete(savePath);
            System.out.println("✓ Save Slot " + slotNumber + " deleted");
            return true;
        } catch (IOException e) {
            throw new SaveException("Failed to delete save: " + e.getMessage());
        }
    }

    /**
     * FR-SAVE-002: Get metadata for all save slots
     */
    public List<SaveMetadata> getAllSaveMetadata() {
        List<SaveMetadata> metadataList = new ArrayList<>();

        for (int i = 1; i <= MAX_SLOTS; i++) {
            if (saveSlotExists(i)) {
                try {
                    SaveMetadata meta = getSaveMetadata(i);
                    metadataList.add(meta);
                } catch (Exception e) {
                    // Corrupt save - create error metadata
                    SaveMetadata empty = new SaveMetadata();
                    empty.setSlotName("Slot " + i + " (Corrupted)");
                    metadataList.add(empty);
                }
            } else {
                // Empty slot
                SaveMetadata empty = new SaveMetadata();
                empty.setSlotName("Slot " + i + " (Empty)");
                metadataList.add(empty);
            }
        }

        return metadataList;
    }

    /**
     * Get metadata for specific slot
     */
    public SaveMetadata getSaveMetadata(int slotNumber) throws SaveException {
        validateSlotNumber(slotNumber);

        if (!saveSlotExists(slotNumber)) {
            SaveMetadata empty = new SaveMetadata();
            empty.setSlotName("Slot " + slotNumber + " (Empty)");
            return empty;
        }

        try {
            Path savePath = getSaveFilePath(slotNumber);
            String json = Files.readString(savePath);
            SaveData saveData = gson.fromJson(json, SaveData.class);
            return saveData.getMetadata();
        } catch (Exception e) {
            throw new SaveException("Could not read save metadata");
        }
    }

    /**
     * Check if save slot exists and has data
     */
    public boolean saveSlotExists(int slotNumber) {
        Path savePath = getSaveFilePath(slotNumber);
        return Files.exists(savePath);
    }

    /**
     * FR-SAVE-001: Auto-save to current active slot
     * Saves to whichever slot was last loaded/saved
     */
    public void autoSave() {
        try {
            saveGame(currentSlot);
        } catch (SaveException e) {
            System.err.println("Auto-save failed: " + e.getMessage());
        }
    }

    /**
     * Validate save data integrity
     */
    private boolean validateSaveData(SaveData data) {
        if (data == null)
            return false;
        if (data.getSaveId() == null)
            return false;
        if (data.getSavedAt() == null)
            return false;
        if (data.getCharacters() == null)
            return false;
        if (data.getBattleHistory() == null)
            return false;
        if (data.getMetadata() == null)
            return false;

        return true;
    }

    /**
     * Create SaveData from current game state
     */
    private SaveData createSaveData() {
        SaveData saveData = new SaveData();

        // 1. Save characters
        List<CharacterData> charDataList = new ArrayList<>();
        for (com.elemental.model.Character character : characterService.getAllCharacters()) {
            charDataList.add(CharacterData.fromCharacter(character));
        }
        saveData.setCharacters(charDataList);

        // 2. Save battle history
        saveData.setBattleHistory(battleHistory.toData());

        // 3. Save settings
        saveData.setSettings(GameSettings.getInstance().toData());

        return saveData;
    }

    /**
     * Get file path for save slot
     */
    private Path getSaveFilePath(int slotNumber) {
        return Paths.get(SAVE_DIR + "slot" + slotNumber + ".json");
    }

    /**
     * Ensure save directory exists
     */
    private void ensureSaveDirectoryExists() {
        try {
            Path saveDir = Paths.get(SAVE_DIR);
            if (!Files.exists(saveDir)) {
                Files.createDirectories(saveDir);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not create save directory");
        }
    }

    /**
     * Validate slot number (1-3)
     */
    private void validateSlotNumber(int slotNumber) throws SaveException {
        if (slotNumber < 1 || slotNumber > MAX_SLOTS) {
            throw new SaveException("Invalid slot number: " + slotNumber);
        }
    }

    // Getters
    public BattleHistory getBattleHistory() {
        return battleHistory;
    }

    public int getCurrentSlot() {
        return currentSlot;
    }

    // Setter
    public void setCurrentSlot(int slotNumber) throws SaveException {
        validateSlotNumber(slotNumber);
        this.currentSlot = slotNumber;
    }
}
