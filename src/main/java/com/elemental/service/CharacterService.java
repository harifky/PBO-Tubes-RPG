package com.elemental.service;

import com.elemental.model.Character;
import com.elemental.model.CharacterClass;
import com.elemental.model.Element;
import com.elemental.factory.CharacterFactory;

import java.util.ArrayList;
import java.util.List;

public class CharacterService {
    private List<Character> characterRoster;
    private Character selectedCharacter;

    public CharacterService() {
        this.characterRoster = new ArrayList<>();
        this.selectedCharacter = null;
    }

    /**
     * FR-CHAR-001: Create and add character to roster
     */
    public Character createCharacter(String name, CharacterClass characterClass, Element element)
            throws IllegalArgumentException {
        Character newCharacter = CharacterFactory.createCharacter(characterClass, name, element);
        characterRoster.add(newCharacter);
        return newCharacter;
    }

    /**
     * FR-CHAR-005: Character Selection
     */
    public Character selectCharacter(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= characterRoster.size()) {
            throw new IndexOutOfBoundsException("Invalid character index: " + index);
        }
        selectedCharacter = characterRoster.get(index);
        return selectedCharacter;
    }

    /**
     * Get character at specific index
     */
    public Character getCharacter(int index) {
        if (index >= 0 && index < characterRoster.size()) {
            return characterRoster.get(index);
        }
        return null;
    }

    /**
     * Remove character from roster
     */
    public boolean removeCharacter(int index) {
        if (index >= 0 && index < characterRoster.size()) {
            Character removed = characterRoster.remove(index);
            if (removed == selectedCharacter) {
                selectedCharacter = null;
            }
            return true;
        }
        return false;
    }

    /**
     * Get all characters in roster
     */
    public List<Character> getAllCharacters() {
        return new ArrayList<>(characterRoster);
    }

    /**
     * Get currently selected character
     */
    public Character getSelectedCharacter() {
        return selectedCharacter;
    }

    /**
     * Get roster size
     */
    public int getRosterSize() {
        return characterRoster.size();
    }

    /**
     * Check if roster is empty
     */
    public boolean isRosterEmpty() {
        return characterRoster.isEmpty();
    }

    /**
     * Display all characters with stats preview
     */
    public String displayAllCharacters() {
        if (characterRoster.isEmpty()) {
            return "No characters in roster.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== CHARACTER ROSTER ===\n");
        for (int i = 0; i < characterRoster.size(); i++) {
            Character character = characterRoster.get(i);
            sb.append(String.format("[%d] %s\n", i + 1, character.toString()));
        }
        return sb.toString();
    }

    /**
     * Display detailed stats preview for a character
     */
    public String displayCharacterStats(int index) {
        if (index >= 0 && index < characterRoster.size()) {
            return characterRoster.get(index).getStatsPreview();
        }
        return "Invalid character index.";
    }

    /**
     * Clear all characters from roster
     */
    public void clearRoster() {
        characterRoster.clear();
        selectedCharacter = null;
    }

    /**
     * Find character by name
     */
    public Character findCharacterByName(String name) {
        for (Character character : characterRoster) {
            if (character.getName().equalsIgnoreCase(name)) {
                return character;
            }
        }
        return null;
    }

    /**
     * Get character index in roster
     */
    public int getCharacterIndex(Character character) {
        return characterRoster.indexOf(character);
    }
}
