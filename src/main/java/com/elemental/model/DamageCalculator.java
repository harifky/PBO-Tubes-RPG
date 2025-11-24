package com.elemental.model;

import java.util.Random;

/**
 * FR-BATTLE-003: Damage Calculation
 * Handles damage calculation with element modifiers, defense, and critical hits
 */
public class DamageCalculator {
    private static final Random random = new Random();
    private static final double CRITICAL_CHANCE = 0.10; // 10%
    private static final double CRITICAL_MULTIPLIER = 1.5;
    private static final double DEFEND_REDUCTION = 0.5; // 50% damage reduction
    private static final int MIN_DAMAGE = 1;

    // Private constructor to prevent instantiation
    private DamageCalculator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Calculate damage with all modifiers
     */
    public static int calculateDamage(Character attacker, Character defender, Skill skill) {
        // Base Damage = Attacker.Attack × Skill Multiplier
        double baseDamage = attacker.getAttack() * skill.getDamageMultiplier();

        // Element Modifier
        double elementModifier = getElementModifier(attacker.getElement(), defender.getElement());

        // Defense Reduction = Base Damage × (Defender.Defense / 200)
        double defenseReduction = baseDamage * (defender.getDefense() / 200.0);

        // Critical Hit (10% chance)
        double criticalModifier = isCritical() ? CRITICAL_MULTIPLIER : 1.0;

        // Final Damage = (Base × Element - Defense) × Critical
        double finalDamage = (baseDamage * elementModifier - defenseReduction) * criticalModifier;

        // Handle Defend state (50% damage reduction)
        if (defender.isDefending()) {
            finalDamage *= DEFEND_REDUCTION;
        }

        // Minimum Damage = 1
        return Math.max(MIN_DAMAGE, (int) Math.round(finalDamage));
    }

    /**
     * Calculate basic attack damage (no skill)
     */
    public static int calculateBasicAttack(Character attacker, Character defender) {
        double baseDamage = attacker.getAttack();
        double elementModifier = getElementModifier(attacker.getElement(), defender.getElement());
        double defenseReduction = baseDamage * (defender.getDefense() / 200.0);
        double criticalModifier = isCritical() ? CRITICAL_MULTIPLIER : 1.0;

        double finalDamage = (baseDamage * elementModifier - defenseReduction) * criticalModifier;

        if (defender.isDefending()) {
            finalDamage *= DEFEND_REDUCTION;
        }

        return Math.max(MIN_DAMAGE, (int) Math.round(finalDamage));
    }

    /**
     * Get element modifier based on attacker and defender elements
     * Fire > Earth: 1.5×
     * Earth > Water: 1.5×
     * Water > Fire: 1.5×
     * Same element: 1.0×
     * Disadvantage: 0.7×
     */
    public static double getElementModifier(Element attackerElement, Element defenderElement) {
        if (attackerElement == defenderElement) {
            return 1.0;
        }

        switch (attackerElement) {
            case FIRE:
                return defenderElement == Element.EARTH ? 1.5 : 0.7;
            case EARTH:
                return defenderElement == Element.WATER ? 1.5 : 0.7;
            case WATER:
                return defenderElement == Element.FIRE ? 1.5 : 0.7;
            default:
                return 1.0;
        }
    }

    /**
     * Check if attack is critical (10% chance)
     */
    public static boolean isCritical() {
        return random.nextDouble() < CRITICAL_CHANCE;
    }

    /**
     * Get element advantage description
     */
    public static String getElementAdvantage(Element attackerElement, Element defenderElement) {
        double modifier = getElementModifier(attackerElement, defenderElement);
        if (modifier > 1.0) {
            return "SUPER EFFECTIVE!";
        } else if (modifier < 1.0) {
            return "Not very effective...";
        }
        return "";
    }
}
