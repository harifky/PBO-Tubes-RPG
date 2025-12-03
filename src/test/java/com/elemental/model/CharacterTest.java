package com.elemental.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Character Model Tests")
class CharacterTest {

    private Character mage;
    private Character warrior;
    private Character ranger;

    @BeforeEach
    void setUp() {
        mage = new Character("TestMage", CharacterClass.MAGE, Element.FIRE);
        warrior = new Character("TestWarrior", CharacterClass.WARRIOR, Element.EARTH);
        ranger = new Character("TestRanger", CharacterClass.RANGER, Element.WATER);
    }

    @Test
    @DisplayName("Should create character with correct attributes")
    void testCharacterCreation() {
        assertEquals("TestMage", mage.getName());
        assertEquals(CharacterClass.MAGE, mage.getCharacterClass());
        assertEquals(Element.FIRE, mage.getElement());
        assertEquals(1, mage.getLevel());
    }

    @Test
    @DisplayName("Mage should have correct base stats")
    void testMageBaseStats() {
        assertEquals(80, mage.getMaxHP());
        assertEquals(100, mage.getMaxMP());
        assertEquals(35, mage.getAttack());
        assertEquals(10, mage.getDefense());
        assertEquals(25, mage.getSpeed());
    }

    @Test
    @DisplayName("Warrior should have correct base stats")
    void testWarriorBaseStats() {
        assertEquals(120, warrior.getMaxHP());
        assertEquals(50, warrior.getMaxMP());
        assertEquals(25, warrior.getAttack());
        assertEquals(20, warrior.getDefense());
        assertEquals(15, warrior.getSpeed());
    }

    @Test
    @DisplayName("Ranger should have correct base stats")
    void testRangerBaseStats() {
        assertEquals(100, ranger.getMaxHP());
        assertEquals(70, ranger.getMaxMP());
        assertEquals(30, ranger.getAttack());
        assertEquals(15, ranger.getDefense());
        assertEquals(30, ranger.getSpeed());
    }

    @Test
    @DisplayName("Should take damage correctly")
    void testTakeDamage() {
        int initialHP = mage.getCurrentHP();
        mage.takeDamage(20);
        assertEquals(initialHP - 20, mage.getCurrentHP());
    }

    @Test
    @DisplayName("HP should not go below 0")
    void testHPCannotGoNegative() {
        mage.takeDamage(1000);
        assertEquals(0, mage.getCurrentHP());
        assertFalse(mage.isAlive());
    }

    @Test
    @DisplayName("Should heal correctly")
    void testHealing() {
        mage.takeDamage(30);
        int currentHP = mage.getCurrentHP();
        mage.heal(20);
        assertEquals(currentHP + 20, mage.getCurrentHP());
    }

    @Test
    @DisplayName("HP should not exceed max HP when healing")
    void testHealingCannotExceedMax() {
        mage.heal(1000);
        assertEquals(mage.getMaxHP(), mage.getCurrentHP());
    }

    @Test
    @DisplayName("Should restore MP correctly")
    void testRestoreMP() {
        mage.useMP(30);
        int currentMP = mage.getCurrentMP();
        mage.restoreMP(20);
        assertEquals(currentMP + 20, mage.getCurrentMP());
    }

    @Test
    @DisplayName("MP should not exceed max MP when restoring")
    void testRestoreMPCannotExceedMax() {
        mage.restoreMP(1000);
        assertEquals(mage.getMaxMP(), mage.getCurrentMP());
    }

    @Test
    @DisplayName("Should check if character is alive")
    void testIsAlive() {
        assertTrue(mage.isAlive());
        mage.takeDamage(mage.getMaxHP());
        assertFalse(mage.isAlive());
    }

    @Test
    @DisplayName("Should set and get defending state")
    void testDefendingState() {
        assertFalse(mage.isDefending());
        mage.setDefending(true);
        assertTrue(mage.isDefending());
        mage.setDefending(false);
        assertFalse(mage.isDefending());
    }

    @Test
    @DisplayName("Should apply status effect correctly")
    void testApplyStatusEffect() {
        mage.applyStatusEffect(StatusEffectType.POISON, 3);
        assertTrue(mage.hasStatusEffect(StatusEffectType.POISON));
    }

    @Test
    @DisplayName("Should remove status effect correctly")
    void testRemoveStatusEffect() {
        mage.applyStatusEffect(StatusEffectType.BURN, 2);
        assertTrue(mage.hasStatusEffect(StatusEffectType.BURN));
        mage.removeStatusEffect(StatusEffectType.BURN);
        assertFalse(mage.hasStatusEffect(StatusEffectType.BURN));
    }

    @Test
    @DisplayName("Should have skills initialized")
    void testSkillsInitialization() {
        assertNotNull(mage.getSkills());
        assertFalse(mage.getSkills().isEmpty());
    }

    @Test
    @DisplayName("Should use MP correctly")
    void testUseMP() {
        int initialMP = mage.getCurrentMP();
        mage.useMP(20);
        assertEquals(initialMP - 20, mage.getCurrentMP());
    }

    @Test
    @DisplayName("MP should not go below 0")
    void testMPCannotGoNegative() {
        mage.useMP(mage.getCurrentMP() + 10);
        assertEquals(0, mage.getCurrentMP());
    }
}

