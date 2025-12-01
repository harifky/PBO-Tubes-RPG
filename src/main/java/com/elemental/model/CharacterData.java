package com.elemental.model;

import com.elemental.factory.CharacterFactory;

/**
 * FR-SAVE-001: Character Data Transfer Object
 * Serializable representation of Character for saving
 */
public class CharacterData {
    // Basic info
    private String name;
    private CharacterClass characterClass;
    private Element element;

    // Stats
    private int level;
    private int experience;
    private int currentHP;
    private int maxHP;
    private int currentMP;
    private int maxMP;
    private int attack;
    private int defense;
    private int speed;
    private Status status;

    // Factory method: Character → CharacterData
    public static CharacterData fromCharacter(Character character) {
        CharacterData data = new CharacterData();
        data.name = character.getName();
        data.characterClass = character.getCharacterClass();
        data.element = character.getElement();
        data.level = character.getLevel();
        data.experience = character.getExperience();
        data.currentHP = character.getCurrentHP();
        data.maxHP = character.getMaxHP();
        data.currentMP = character.getCurrentMP();
        data.maxMP = character.getMaxMP();
        data.attack = character.getAttack();
        data.defense = character.getDefense();
        data.speed = character.getSpeed();
        data.status = character.getStatus();
        return data;
    }

    // Convert back: CharacterData → Character
    public Character toCharacter(CharacterFactory factory) {
        Character character = factory.createCharacter(characterClass, name, element);
        // Restore stats by leveling up to saved level
        restoreCharacterState(character);
        return character;
    }

    private void restoreCharacterState(Character character) {
        // Level up to saved level
        int levelsToGain = level - 1;
        for (int i = 0; i < levelsToGain; i++) {
            character.levelUp();
        }

        // Set exact stats (in case of variations)
        character.setCurrentHP(currentHP);
        character.setCurrentMP(currentMP);
        character.setStatus(status);

        // Set experience within current level
        int currentLevelExp = experience % 100;
        character.gainExperience(currentLevelExp);
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public Element getElement() {
        return element;
    }
}
