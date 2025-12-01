package com.elemental.model;

/**
 * Enum for item target types
 */
public enum ItemTarget {
    ALIVE_ALLY, // Can only target alive allies
    DEAD_ALLY, // Can only target dead allies (Revive)
    ANY_ALLY // Can target any ally
}
