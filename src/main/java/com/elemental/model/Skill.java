package com.elemental.model;

import java.util.ArrayList;
import java.util.List;

public class Skill {
    private String name;
    private int mpCost;
    private double damageMultiplier;
    private SkillType skillType;
    private Element element; // Elemental attribute for skills

    public Skill(String name, int mpCost, double damageMultiplier, SkillType skillType, Element element) {
        this.name = name;
        this.mpCost = mpCost;
        this.damageMultiplier = damageMultiplier;
        this.skillType = skillType;
        this.element = element;
    }

    // Constructor for non-elemental skills (buffs, heals)
    public Skill(String name, int mpCost, double damageMultiplier, SkillType skillType) {
        this(name, mpCost, damageMultiplier, skillType, null);
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getMpCost() {
        return mpCost;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public Element getElement() {
        return element;
    }

    // Factory methods for each class skills
    public static List<Skill> getMageSkills(Element element) {
        List<Skill> skills = new ArrayList<>();

        // Skills are element-specific for Mage
        switch (element) {
            case FIRE:
                // Fire Mage - Offensive fire spells
                skills.add(new Skill("Fireball", 15, 1.5, SkillType.DAMAGE, Element.FIRE));
                skills.add(new Skill("Flame Burst", 25, 2.0, SkillType.DAMAGE, Element.FIRE));
                skills.add(new Skill("Inferno", 35, 2.5, SkillType.DAMAGE, Element.FIRE));
                break;

            case WATER:
                // Water Mage - Healing and water attacks
                skills.add(new Skill("Water Bolt", 15, 1.5, SkillType.DAMAGE, Element.WATER));
                skills.add(new Skill("Tidal Wave", 25, 2.0, SkillType.DAMAGE, Element.WATER));
                skills.add(new Skill("Healing Stream", 20, 0.5, SkillType.HEAL)); // Heals HP
                break;

            case EARTH:
                // Earth Mage - Defensive and earth attacks
                skills.add(new Skill("Stone Strike", 15, 1.5, SkillType.DAMAGE, Element.EARTH));
                skills.add(new Skill("Earthquake", 25, 2.0, SkillType.DAMAGE, Element.EARTH));
                skills.add(new Skill("Earth Shield", 20, 1.5, SkillType.BUFF)); // Defense buff
                break;
        }

        // All Mages get Meditation
        skills.add(new Skill("Meditation", 0, 0.3, SkillType.HEAL)); // Heals MP
        return skills;
    }

    public static List<Skill> getWarriorSkills(Element element) {
        List<Skill> skills = new ArrayList<>();

        // Skills are element-specific for Warrior
        switch (element) {
            case FIRE:
                // Fire Warrior - Aggressive offensive skills
                skills.add(new Skill("Flame Slash", 15, 1.4, SkillType.DAMAGE, Element.FIRE));
                skills.add(new Skill("Blazing Strike", 20, 1.8, SkillType.DAMAGE, Element.FIRE));
                skills.add(new Skill("Inferno Charge", 25, 2.0, SkillType.DAMAGE, Element.FIRE));
                break;

            case WATER:
                // Water Warrior - Tactical and balanced
                skills.add(new Skill("Aqua Slash", 15, 1.4, SkillType.DAMAGE, Element.WATER));
                skills.add(new Skill("Tidal Cleave", 20, 1.8, SkillType.DAMAGE, Element.WATER));
                skills.add(new Skill("Water Barrier", 20, 1.5, SkillType.BUFF)); // Defense buff
                break;

            case EARTH:
                // Earth Warrior - Defensive tank
                skills.add(new Skill("Earth Slam", 15, 1.4, SkillType.DAMAGE, Element.EARTH));
                skills.add(new Skill("Boulder Crush", 20, 1.8, SkillType.DAMAGE, Element.EARTH));
                skills.add(new Skill("Stone Shield", 20, 1.5, SkillType.BUFF)); // Defense buff
                break;
        }

        // All Warriors get Shield Bash (non-elemental utility)
        skills.add(new Skill("Shield Bash", 15, 1.2, SkillType.DEBUFF)); // Stun chance
        return skills;
    }

    public static List<Skill> getRangerSkills(Element element) {
        List<Skill> skills = new ArrayList<>();

        // Skills are element-specific for Ranger
        switch (element) {
            case FIRE:
                // Fire Ranger - Burning damage over time
                skills.add(new Skill("Flame Arrow", 15, 1.5, SkillType.DAMAGE, Element.FIRE));
                skills.add(new Skill("Blazing Shot", 20, 1.9, SkillType.DAMAGE, Element.FIRE));
                skills.add(new Skill("Explosive Arrow", 25, 2.2, SkillType.DAMAGE, Element.FIRE));
                break;

            case WATER:
                // Water Ranger - Frost and mobility
                skills.add(new Skill("Frost Arrow", 15, 1.5, SkillType.DAMAGE, Element.WATER));
                skills.add(new Skill("Ice Shot", 20, 1.9, SkillType.DAMAGE, Element.WATER));
                skills.add(new Skill("Glacial Pierce", 25, 2.2, SkillType.DAMAGE, Element.WATER));
                break;

            case EARTH:
                // Earth Ranger - Piercing and critical hits
                skills.add(new Skill("Rock Arrow", 15, 1.5, SkillType.DAMAGE, Element.EARTH));
                skills.add(new Skill("Piercing Shot", 20, 1.9, SkillType.DAMAGE, Element.EARTH));
                skills.add(new Skill("Seismic Arrow", 25, 2.2, SkillType.DAMAGE, Element.EARTH));
                break;
        }

        // All Rangers get Rapid Shot and Quick Step (non-elemental utility)
        skills.add(new Skill("Rapid Shot", 12, 1.3, SkillType.DAMAGE)); // Fast attack
        skills.add(new Skill("Quick Step", 15, 1.3, SkillType.BUFF)); // Speed buff
        return skills;
    }

    @Override
    public String toString() {
        if (element != null) {
            return String.format("%s [%s] (MP: %d, Type: %s)", name, element, mpCost, skillType);
        }
        return String.format("%s (MP: %d, Type: %s)", name, mpCost, skillType);
    }
}
