package com.elemental.model;

import java.util.ArrayList;
import java.util.List;

public class Skill {
    private String name;
    private int mpCost;
    private double damageMultiplier;
    private SkillType skillType;

    public Skill(String name, int mpCost, double damageMultiplier, SkillType skillType) {
        this.name = name;
        this.mpCost = mpCost;
        this.damageMultiplier = damageMultiplier;
        this.skillType = skillType;
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

    // Factory methods for each class skills
    public static List<Skill> getMageSkills() {
        List<Skill> skills = new ArrayList<>();
        skills.add(new Skill("Fireball", 15, 1.5, SkillType.DAMAGE));
        skills.add(new Skill("Flame Burst", 25, 2.0, SkillType.DAMAGE));
        skills.add(new Skill("Meditation", 0, 0.3, SkillType.HEAL)); // Heals MP
        return skills;
    }

    public static List<Skill> getWarriorSkills() {
        List<Skill> skills = new ArrayList<>();
        skills.add(new Skill("Slash", 10, 1.3, SkillType.DAMAGE));
        skills.add(new Skill("Shield Bash", 15, 1.2, SkillType.DEBUFF)); // Damage + stun chance
        skills.add(new Skill("Iron Defense", 20, 1.5, SkillType.BUFF)); // Defense buff
        return skills;
    }

    public static List<Skill> getRangerSkills() {
        List<Skill> skills = new ArrayList<>();
        skills.add(new Skill("Arrow Shot", 12, 1.4, SkillType.DAMAGE));
        skills.add(new Skill("Poison Arrow", 18, 1.1, SkillType.DEBUFF)); // Damage + poison
        skills.add(new Skill("Quick Step", 15, 1.3, SkillType.BUFF)); // Speed buff
        return skills;
    }

    @Override
    public String toString() {
        return String.format("%s (MP: %d, Type: %s)", name, mpCost, skillType);
    }
}
