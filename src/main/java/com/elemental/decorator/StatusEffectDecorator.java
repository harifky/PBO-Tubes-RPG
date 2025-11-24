package com.elemental.decorator;

import com.elemental.model.Character;
import com.elemental.model.StatusEffectType;

/**
 * FR-BATTLE-004: Status Effect Decorator Pattern
 * Base decorator for applying status effects to characters
 */
public abstract class StatusEffectDecorator {
    protected Character character;
    protected StatusEffectType effectType;
    protected int duration;

    public StatusEffectDecorator(Character character, StatusEffectType effectType, int duration) {
        this.character = character;
        this.effectType = effectType;
        this.duration = duration;
    }

    public abstract void apply();
    public abstract void tick();
    public abstract void remove();

    public Character getCharacter() {
        return character;
    }

    public StatusEffectType getEffectType() {
        return effectType;
    }

    public int getDuration() {
        return duration;
    }

    public void decrementDuration() {
        this.duration--;
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    // Factory method to create appropriate decorator
    public static StatusEffectDecorator createEffect(Character character, StatusEffectType effectType, int duration) {
        switch (effectType) {
            case POISON:
                return new PoisonEffect(character, duration);
            case BURN:
                return new BurnEffect(character, duration);
            case STUN:
                return new StunEffect(character, duration);
            case SHIELDED:
                return new ShieldedEffect(character, duration);
            case SPEED_BUFF:
                return new SpeedBuffEffect(character, duration);
            default:
                throw new IllegalArgumentException("Unknown status effect type: " + effectType);
        }
    }
}

/**
 * Poison Effect: 5% max HP/turn, 3 turns
 */
class PoisonEffect extends StatusEffectDecorator {
    public PoisonEffect(Character character, int duration) {
        super(character, StatusEffectType.POISON, duration);
    }

    @Override
    public void apply() {
        character.applyStatusEffect(StatusEffectType.POISON, duration);
    }

    @Override
    public void tick() {
        int damage = (int) (character.getMaxHP() * 0.05);
        character.takeDamage(damage);
    }

    @Override
    public void remove() {
        character.removeStatusEffect(StatusEffectType.POISON);
    }
}

/**
 * Burn Effect: 7% max HP/turn, 2 turns
 */
class BurnEffect extends StatusEffectDecorator {
    public BurnEffect(Character character, int duration) {
        super(character, StatusEffectType.BURN, duration);
    }

    @Override
    public void apply() {
        character.applyStatusEffect(StatusEffectType.BURN, duration);
    }

    @Override
    public void tick() {
        int damage = (int) (character.getMaxHP() * 0.07);
        character.takeDamage(damage);
    }

    @Override
    public void remove() {
        character.removeStatusEffect(StatusEffectType.BURN);
    }
}

/**
 * Stun Effect: Skip turn, 1 turn
 */
class StunEffect extends StatusEffectDecorator {
    public StunEffect(Character character, int duration) {
        super(character, StatusEffectType.STUN, duration);
    }

    @Override
    public void apply() {
        character.applyStatusEffect(StatusEffectType.STUN, duration);
    }

    @Override
    public void tick() {
        // Stun prevents action, handled in battle logic
    }

    @Override
    public void remove() {
        character.removeStatusEffect(StatusEffectType.STUN);
    }
}

/**
 * Shielded Effect: +50% Defense, 2 turns
 */
class ShieldedEffect extends StatusEffectDecorator {
    public ShieldedEffect(Character character, int duration) {
        super(character, StatusEffectType.SHIELDED, duration);
    }

    @Override
    public void apply() {
        character.applyStatusEffect(StatusEffectType.SHIELDED, duration);
    }

    @Override
    public void tick() {
        // Defense bonus is handled in getModifiedDefense()
    }

    @Override
    public void remove() {
        character.removeStatusEffect(StatusEffectType.SHIELDED);
    }
}

/**
 * Speed Buff Effect: +30% Speed, 3 turns
 */
class SpeedBuffEffect extends StatusEffectDecorator {
    public SpeedBuffEffect(Character character, int duration) {
        super(character, StatusEffectType.SPEED_BUFF, duration);
    }

    @Override
    public void apply() {
        character.applyStatusEffect(StatusEffectType.SPEED_BUFF, duration);
    }

    @Override
    public void tick() {
        // Speed bonus is handled in getModifiedSpeed()
    }

    @Override
    public void remove() {
        character.removeStatusEffect(StatusEffectType.SPEED_BUFF);
    }
}
