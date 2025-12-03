package com.elemental.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DamageCalculator Tests")
class DamageCalculatorTest {

    private Character fireAttacker;
    private Character waterDefender;
    private Skill testSkill;

    @BeforeEach
    void setUp() {
        fireAttacker = new Character("FireMage", CharacterClass.MAGE, Element.FIRE);
        waterDefender = new Character("WaterWarrior", CharacterClass.WARRIOR, Element.WATER);

        // Create a basic test skill
        testSkill = new Skill("Test Attack", 20, 1.0, SkillType.DAMAGE, Element.FIRE);
    }

    @Test
    @DisplayName("Should calculate damage correctly")
    void testCalculateDamage() {
        int damage = DamageCalculator.calculateDamage(fireAttacker, waterDefender, testSkill);
        assertTrue(damage > 0, "Damage should be positive");
    }

    @Test
    @DisplayName("Should apply element advantage modifier")
    void testElementAdvantage() {
        // Fire has advantage over Earth
        double modifier = DamageCalculator.getElementModifier(Element.FIRE, Element.EARTH);
        assertEquals(1.5, modifier, 0.001);
    }

    @Test
    @DisplayName("Should apply element disadvantage modifier")
    void testElementDisadvantage() {
        // Fire has disadvantage against Water
        double modifier = DamageCalculator.getElementModifier(Element.FIRE, Element.WATER);
        assertEquals(0.7, modifier, 0.001);
    }

    @Test
    @DisplayName("Should apply neutral modifier for same element")
    void testSameElement() {
        double modifier = DamageCalculator.getElementModifier(Element.FIRE, Element.FIRE);
        assertEquals(1.0, modifier, 0.001);
    }

    @Test
    @DisplayName("Water should have advantage over Fire")
    void testWaterOverFire() {
        double modifier = DamageCalculator.getElementModifier(Element.WATER, Element.FIRE);
        assertEquals(1.5, modifier, 0.001);
    }

    @Test
    @DisplayName("Earth should have advantage over Water")
    void testEarthOverWater() {
        double modifier = DamageCalculator.getElementModifier(Element.EARTH, Element.WATER);
        assertEquals(1.5, modifier, 0.001);
    }

    @Test
    @DisplayName("Should calculate basic attack damage")
    void testBasicAttack() {
        int damage = DamageCalculator.calculateBasicAttack(fireAttacker, waterDefender);
        assertTrue(damage > 0, "Basic attack damage should be positive");
    }

    @Test
    @DisplayName("Should reduce damage when defending")
    void testDefendingReducesDamage() {
        int normalDamage = DamageCalculator.calculateBasicAttack(fireAttacker, waterDefender);

        waterDefender.setDefending(true);
        int defendedDamage = DamageCalculator.calculateBasicAttack(fireAttacker, waterDefender);

        assertTrue(defendedDamage < normalDamage, "Defended damage should be less than normal damage");
    }

    @Test
    @DisplayName("Damage should never be less than 1")
    void testMinimumDamage() {
        // Create a weak attacker with very low attack
        Character weakAttacker = new Character("Weak", CharacterClass.MAGE, Element.FIRE);
        weakAttacker.takeDamage(weakAttacker.getCurrentHP() - 1); // Make it very weak

        // Create a strong defender
        Character strongDefender = new Character("Tank", CharacterClass.WARRIOR, Element.WATER);

        int damage = DamageCalculator.calculateBasicAttack(weakAttacker, strongDefender);
        assertTrue(damage >= 1, "Minimum damage should be at least 1");
    }

    @Test
    @DisplayName("Should handle all element combinations")
    void testAllElementCombinations() {
        Element[] elements = {Element.FIRE, Element.WATER, Element.EARTH};

        for (Element attacker : elements) {
            for (Element defender : elements) {
                double modifier = DamageCalculator.getElementModifier(attacker, defender);
                assertTrue(modifier > 0, "Modifier should be positive");
                assertTrue(modifier <= 1.5, "Modifier should not exceed 1.5");
            }
        }
    }
}

