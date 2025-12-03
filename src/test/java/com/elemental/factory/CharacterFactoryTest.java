package com.elemental.factory;

import com.elemental.model.Character;
import com.elemental.model.CharacterClass;
import com.elemental.model.Element;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CharacterFactory Tests")
class CharacterFactoryTest {

    @Test
    @DisplayName("Should create character with valid name")
    void testCreateCharacterValid() {
        Character character = CharacterFactory.createCharacter(
            CharacterClass.MAGE, "ValidName", Element.FIRE
        );

        assertNotNull(character);
        assertEquals("ValidName", character.getName());
        assertEquals(CharacterClass.MAGE, character.getCharacterClass());
        assertEquals(Element.FIRE, character.getElement());
    }

    @Test
    @DisplayName("Should throw exception for empty name")
    void testEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            CharacterFactory.createCharacter(CharacterClass.WARRIOR, "", Element.EARTH);
        });
    }

    @Test
    @DisplayName("Should throw exception for null name")
    void testNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            CharacterFactory.createCharacter(CharacterClass.RANGER, null, Element.WATER);
        });
    }

    @Test
    @DisplayName("Should throw exception for name longer than 20 characters")
    void testNameTooLong() {
        String longName = "ThisNameIsWayTooLongForTheGame";
        assertThrows(IllegalArgumentException.class, () -> {
            CharacterFactory.createCharacter(CharacterClass.MAGE, longName, Element.FIRE);
        });
    }

    @Test
    @DisplayName("Should throw exception for non-alphanumeric name")
    void testNonAlphanumericName() {
        assertThrows(IllegalArgumentException.class, () -> {
            CharacterFactory.createCharacter(CharacterClass.WARRIOR, "Name@123", Element.EARTH);
        });
    }

    @Test
    @DisplayName("Should accept name with numbers")
    void testNameWithNumbers() {
        Character character = CharacterFactory.createCharacter(
            CharacterClass.RANGER, "Player123", Element.WATER
        );

        assertNotNull(character);
        assertEquals("Player123", character.getName());
    }

    @Test
    @DisplayName("Should accept 20 character name")
    void testMaxLengthName() {
        String maxName = "12345678901234567890"; // Exactly 20 characters
        Character character = CharacterFactory.createCharacter(
            CharacterClass.MAGE, maxName, Element.FIRE
        );

        assertNotNull(character);
        assertEquals(maxName, character.getName());
    }

    @Test
    @DisplayName("isValidName should return true for valid name")
    void testIsValidNameTrue() {
        assertTrue(CharacterFactory.isValidName("ValidName"));
        assertTrue(CharacterFactory.isValidName("Player1"));
        assertTrue(CharacterFactory.isValidName("ABC"));
    }

    @Test
    @DisplayName("isValidName should return false for invalid name")
    void testIsValidNameFalse() {
        assertFalse(CharacterFactory.isValidName(""));
        assertFalse(CharacterFactory.isValidName(null));
        assertFalse(CharacterFactory.isValidName("Name@123"));
        assertFalse(CharacterFactory.isValidName("Name With Spaces"));
        assertFalse(CharacterFactory.isValidName("ThisNameIsWayTooLongForTheGame"));
    }

    @Test
    @DisplayName("getNameValidationError should return error message for invalid name")
    void testGetNameValidationError() {
        assertNotNull(CharacterFactory.getNameValidationError(""));
        assertNotNull(CharacterFactory.getNameValidationError("ThisNameIsWayTooLongForTheGame"));
        assertNotNull(CharacterFactory.getNameValidationError("Name@123"));
    }

    @Test
    @DisplayName("getNameValidationError should return null for valid name")
    void testGetNameValidationErrorNull() {
        assertNull(CharacterFactory.getNameValidationError("ValidName"));
        assertNull(CharacterFactory.getNameValidationError("Player123"));
    }

    @Test
    @DisplayName("Should create all character classes")
    void testCreateAllClasses() {
        Character mage = CharacterFactory.createCharacter(CharacterClass.MAGE, "Mage", Element.FIRE);
        Character warrior = CharacterFactory.createCharacter(CharacterClass.WARRIOR, "Warrior", Element.EARTH);
        Character ranger = CharacterFactory.createCharacter(CharacterClass.RANGER, "Ranger", Element.WATER);

        assertNotNull(mage);
        assertNotNull(warrior);
        assertNotNull(ranger);

        assertEquals(CharacterClass.MAGE, mage.getCharacterClass());
        assertEquals(CharacterClass.WARRIOR, warrior.getCharacterClass());
        assertEquals(CharacterClass.RANGER, ranger.getCharacterClass());
    }

    @Test
    @DisplayName("Should create characters with all elements")
    void testCreateAllElements() {
        Character fire = CharacterFactory.createCharacter(CharacterClass.MAGE, "Fire", Element.FIRE);
        Character water = CharacterFactory.createCharacter(CharacterClass.MAGE, "Water", Element.WATER);
        Character earth = CharacterFactory.createCharacter(CharacterClass.MAGE, "Earth", Element.EARTH);

        assertEquals(Element.FIRE, fire.getElement());
        assertEquals(Element.WATER, water.getElement());
        assertEquals(Element.EARTH, earth.getElement());
    }
}

