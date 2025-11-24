package com.elemental.model;

public enum StatusEffectType {
    POISON,    // 5% max HP/turn, 3 turns
    BURN,      // 7% max HP/turn, 2 turns
    STUN,      // Skip turn, 1 turn
    SHIELDED,  // +50% Defense, 2 turns
    SPEED_BUFF // +30% Speed, 3 turns
}

