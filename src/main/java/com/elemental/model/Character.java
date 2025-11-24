package com.elemental.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Character {
    // Basic attributes
    private String name;
    private CharacterClass characterClass;
    private Element element;

    // Level and experience
    private int level;
    private int experience;

    // Stats
    private int currentHP;
    private int maxHP;
    private int currentMP;
    private int maxMP;
    private int attack;
    private int defense;
    private int speed;

    // Status and skills
    private Status status;
    private List<Skill> skills;

    // Battle-related attributes
    private boolean isDefending;
    private Map<StatusEffectType, Integer> activeStatusEffects; // Effect type -> remaining turns
    private int baseSpeed; // Store original speed for buff calculations

    // Constructor
    public Character(String name, CharacterClass characterClass, Element element) {
        this.name = name;
        this.characterClass = characterClass;
        this.element = element;
        this.level = 1;
        this.experience = 0;
        this.status = Status.NORMAL;
        this.isDefending = false;
        this.activeStatusEffects = new HashMap<>();

        // Initialize base stats based on class
        initializeBaseStats();

        // Initialize skills based on class
        initializeSkills();
    }

    private void initializeBaseStats() {
        switch (characterClass) {
            case MAGE:
                this.maxHP = 80;
                this.maxMP = 100;
                this.attack = 35;
                this.defense = 10;
                this.speed = 25;
                break;
            case WARRIOR:
                this.maxHP = 120;
                this.maxMP = 50;
                this.attack = 25;
                this.defense = 20;
                this.speed = 15;
                break;
            case RANGER:
                this.maxHP = 100;
                this.maxMP = 70;
                this.attack = 30;
                this.defense = 15;
                this.speed = 30;
                break;
        }
        this.currentHP = this.maxHP;
        this.currentMP = this.maxMP;
    }

    private void initializeSkills() {
        switch (characterClass) {
            case MAGE:
                this.skills = Skill.getMageSkills();
                break;
            case WARRIOR:
                this.skills = Skill.getWarriorSkills();
                break;
            case RANGER:
                this.skills = Skill.getRangerSkills();
                break;
        }
    }

    // FR-CHAR-004: Level Up System
    public void gainExperience(int exp) {
        this.experience += exp;

        // Check if level up is needed
        while (this.experience >= getExperienceForNextLevel() && this.level < 99) {
            levelUp();
        }

        // Cap experience at 999
        if (this.experience > 999) {
            this.experience = 999;
        }
    }

    private int getExperienceForNextLevel() {
        return level * 100;
    }

    public void levelUp() {
        this.level++;

        // Stat increases
        this.maxHP += 10;
        this.maxMP += 5;
        this.attack += 3;
        this.defense += 2;
        this.speed += 1;

        // Update base speed
        this.baseSpeed = this.speed;

        // Restore HP and MP on level up
        this.currentHP = this.maxHP;
        this.currentMP = this.maxMP;

        // Reset experience for next level
        this.experience -= getExperienceForNextLevel() - 100;
    }

    // Combat methods
    public void takeDamage(int damage) {
        this.currentHP = Math.max(0, this.currentHP - damage);

        if (this.currentHP == 0) {
            this.status = Status.DEAD;
        }
    }

    public void heal(int amount) {
        if (this.status != Status.DEAD) {
            this.currentHP = Math.min(this.maxHP, this.currentHP + amount);
            if (this.currentHP > 0 && this.status == Status.DEAD) {
                this.status = Status.NORMAL;
            }
        }
    }

    public void restoreMP(int amount) {
        this.currentMP = Math.min(this.maxMP, this.currentMP + amount);
    }

    public void useMP(int amount) {
        this.currentMP = Math.max(0, this.currentMP - amount);
    }

    public boolean canUseSkill(Skill skill) {
        return this.currentMP >= skill.getMpCost() && this.status != Status.DEAD && this.status != Status.STUNNED;
    }

    // Battle state methods
    public void setDefending(boolean defending) {
        this.isDefending = defending;
    }

    public boolean isDefending() {
        return isDefending;
    }

    // Status effect methods
    public void applyStatusEffect(StatusEffectType effectType, int duration) {
        activeStatusEffects.put(effectType, duration);
        updateStatusFromEffects();
    }

    public void removeStatusEffect(StatusEffectType effectType) {
        activeStatusEffects.remove(effectType);
        updateStatusFromEffects();
    }

    public boolean hasStatusEffect(StatusEffectType effectType) {
        return activeStatusEffects.containsKey(effectType);
    }

    public int getStatusEffectDuration(StatusEffectType effectType) {
        return activeStatusEffects.getOrDefault(effectType, 0);
    }

    public Map<StatusEffectType, Integer> getActiveStatusEffects() {
        return new HashMap<>(activeStatusEffects);
    }

    private void updateStatusFromEffects() {
        if (activeStatusEffects.containsKey(StatusEffectType.STUN)) {
            this.status = Status.STUNNED;
        } else if (activeStatusEffects.containsKey(StatusEffectType.POISON)) {
            this.status = Status.POISONED;
        } else if (activeStatusEffects.containsKey(StatusEffectType.SHIELDED)) {
            this.status = Status.SHIELDED;
        } else if (this.currentHP > 0) {
            this.status = Status.NORMAL;
        }
    }

    public void processStatusEffects() {
        List<StatusEffectType> toRemove = new ArrayList<>();

        for (Map.Entry<StatusEffectType, Integer> entry : activeStatusEffects.entrySet()) {
            StatusEffectType effectType = entry.getKey();
            int duration = entry.getValue();

            // Process effect
            switch (effectType) {
                case POISON:
                    int poisonDamage = (int) (maxHP * 0.05);
                    takeDamage(poisonDamage);
                    break;
                case BURN:
                    int burnDamage = (int) (maxHP * 0.07);
                    takeDamage(burnDamage);
                    break;
                case STUN:
                case SHIELDED:
                case SPEED_BUFF:
                    // These are handled elsewhere
                    break;
            }

            // Decrease duration
            int newDuration = duration - 1;
            if (newDuration <= 0) {
                toRemove.add(effectType);
            } else {
                activeStatusEffects.put(effectType, newDuration);
            }
        }

        // Remove expired effects
        for (StatusEffectType effectType : toRemove) {
            removeStatusEffect(effectType);
        }
    }

    public int getModifiedDefense() {
        int modifiedDefense = this.defense;
        if (hasStatusEffect(StatusEffectType.SHIELDED)) {
            modifiedDefense = (int) (defense * 1.5);
        }
        return modifiedDefense;
    }

    public int getModifiedSpeed() {
        int modifiedSpeed = this.speed;
        if (hasStatusEffect(StatusEffectType.SPEED_BUFF)) {
            modifiedSpeed = (int) (speed * 1.3);
        }
        return modifiedSpeed;
    }

    // Getters
    public String getName() {
        return name;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public Element getElement() {
        return element;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getCurrentMP() {
        return currentMP;
    }

    public int getMaxMP() {
        return maxMP;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public Status getStatus() {
        return status;
    }

    public List<Skill> getSkills() {
        return new ArrayList<>(skills);
    }

    // Setters
    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCurrentHP(int currentHP) {
        this.currentHP = Math.max(0, Math.min(currentHP, this.maxHP));
        if (this.currentHP == 0) {
            this.status = Status.DEAD;
        }
    }

    public void setCurrentMP(int currentMP) {
        this.currentMP = Math.max(0, Math.min(currentMP, this.maxMP));
    }

    // Display methods
    public String getStatsPreview() {
        return String.format(
            "=== %s ===\n" +
            "Class: %s | Element: %s | Level: %d\n" +
            "HP: %d/%d | MP: %d/%d\n" +
            "ATK: %d | DEF: %d | SPD: %d\n" +
            "EXP: %d/%d | Status: %s",
            name, characterClass, element, level,
            currentHP, maxHP, currentMP, maxMP,
            attack, defense, speed,
            experience, getExperienceForNextLevel(), status
        );
    }

    @Override
    public String toString() {
        return String.format("%s (Lv.%d %s)", name, level, characterClass);
    }

    public boolean isAlive() {
        return currentHP > 0 && status != Status.DEAD;
    }
}
