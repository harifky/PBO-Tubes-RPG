package com.elemental.factory;

import com.elemental.model.Character;
import com.elemental.model.CharacterClass;
import com.elemental.model.Element;

public class CharacterFactory {

    /**
     * FR-CHAR-001: Character Creation with validation
     * - Name max 20 characters
     * - Name must be alphanumeric
     */
    public static Character createCharacter(CharacterClass characterClass, String name, Element element)
            throws IllegalArgumentException {

        // Validate name
        validateName(name);

        // Create character with validated data
        return new Character(name, characterClass, element);
    }

    private static void validateName(String name) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }

        if (name.length() > 20) {
            throw new IllegalArgumentException("Name must be maximum 20 characters!");
        }

        if (!name.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Name must be alphanumeric (letters and numbers only)!");
        }
    }

    /**
     * Validate name without throwing exception
     */
    public static boolean isValidName(String name) {
        try {
            validateName(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Get validation error message for name
     */
    public static String getNameValidationError(String name) {
        try {
            validateName(name);
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}
