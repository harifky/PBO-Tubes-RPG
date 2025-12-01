package com.elemental.model;

/**
 * Enum for specific item effects
 */
public enum ItemEffect {
    RESTORE_HP, // Health Potion
    RESTORE_MP, // Mana Potion
    RESTORE_BOTH, // Elixir
    BOOST_ATTACK, // Attack Boost
    BOOST_DEFENSE, // Defense Boost
    CURE_STATUS, // Antidote (removes Poison/Burn)
    REVIVE // Revive
}
