package com.elemental.model;

/**
 * FR-ITEM-002: Item Types
 * Represents an item that can be used in battle
 */
public class Item {
    private String name;
    private String description;
    private ItemType itemType;
    private ItemEffect effect;
    private int value; // HP/MP restoration or buff percentage
    private int duration; // For buffs (0 for instant effects)
    private ItemTarget targetType; // Who can this item target

    public Item(String name, String description, ItemType itemType,
            ItemEffect effect, int value, int duration, ItemTarget targetType) {
        this.name = name;
        this.description = description;
        this.itemType = itemType;
        this.effect = effect;
        this.value = value;
        this.duration = duration;
        this.targetType = targetType;
    }

    /**
     * Check if this item can be used on the target character
     */
    public boolean canUse(Character target) {
        if (target == null) {
            return false;
        }

        switch (targetType) {
            case ALIVE_ALLY:
                return target.isAlive();
            case DEAD_ALLY:
                return !target.isAlive();
            case ANY_ALLY:
                return true;
            default:
                return false;
        }
    }

    /**
     * Apply the item's effect to the target character
     */
    public void applyEffect(Character target) {
        switch (effect) {
            case RESTORE_HP:
                target.heal(value);
                break;
            case RESTORE_MP:
                target.restoreMP(value);
                break;
            case RESTORE_BOTH:
                // For Elixir: value contains HP amount, duration contains MP amount
                // But we need to fix this - let's hardcode Elixir values for now
                if (name.equals("Elixir")) {
                    target.heal(100); // 100 HP
                    target.restoreMP(50); // 50 MP
                } else {
                    target.heal(value);
                    target.restoreMP(value / 2);
                }
                break;
            case BOOST_ATTACK:
                target.applyItemBuff("ATTACK", value, duration);
                break;
            case BOOST_DEFENSE:
                target.applyItemBuff("DEFENSE", value, duration);
                break;
            case CURE_STATUS:
                // Remove Poison and Burn status effects
                target.removeStatusEffect(StatusEffectType.POISON);
                target.removeStatusEffect(StatusEffectType.BURN);
                break;
            case REVIVE:
                if (!target.isAlive()) {
                    int reviveHP = (int) (target.getMaxHP() * (value / 100.0));
                    target.setCurrentHP(reviveHP);
                    target.setStatus(Status.NORMAL);
                }
                break;
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public ItemEffect getEffect() {
        return effect;
    }

    public int getValue() {
        return value;
    }

    public int getDuration() {
        return duration;
    }

    public ItemTarget getTargetType() {
        return targetType;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", name, description);
    }
}
