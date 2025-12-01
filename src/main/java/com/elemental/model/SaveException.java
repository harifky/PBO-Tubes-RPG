package com.elemental.model;

/**
 * Custom exception for save/load operations
 */
public class SaveException extends Exception {
    public SaveException(String message) {
        super(message);
    }

    public SaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
