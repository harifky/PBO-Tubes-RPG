package com.elemental.factory;

import com.elemental.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemFactory Tests")
class ItemFactoryTest {

    private com.elemental.model.Character testCharacter;
    private com.elemental.model.Character deadCharacter;

    @BeforeEach
    void setUp() {
        testCharacter = new com.elemental.model.Character("TestChar", CharacterClass.WARRIOR, Element.FIRE);
        deadCharacter = new com.elemental.model.Character("DeadChar", CharacterClass.MAGE, Element.WATER);
        deadCharacter.takeDamage(deadCharacter.getMaxHP()); // Kill character
    }

    @Test
    @DisplayName("Should get Health Potion")
    void testGetHealthPotion() {
        Item item = ItemFactory.getItem("Health Potion");

        assertNotNull(item);
        assertEquals("Health Potion", item.getName());
        assertEquals(ItemType.HEALING, item.getItemType());
        assertEquals(ItemEffect.RESTORE_HP, item.getEffect());
    }

    @Test
    @DisplayName("Should get Mana Potion")
    void testGetManaPotion() {
        Item item = ItemFactory.getItem("Mana Potion");

        assertNotNull(item);
        assertEquals("Mana Potion", item.getName());
        assertEquals(ItemEffect.RESTORE_MP, item.getEffect());
    }

    @Test
    @DisplayName("Should get Elixir")
    void testGetElixir() {
        Item item = ItemFactory.getItem("Elixir");

        assertNotNull(item);
        assertEquals("Elixir", item.getName());
        assertEquals(ItemEffect.RESTORE_BOTH, item.getEffect());
    }

    @Test
    @DisplayName("Should get Attack Boost")
    void testGetAttackBoost() {
        Item item = ItemFactory.getItem("Attack Boost");

        assertNotNull(item);
        assertEquals("Attack Boost", item.getName());
        assertEquals(ItemType.BUFF, item.getItemType());
        assertEquals(ItemEffect.BOOST_ATTACK, item.getEffect());
    }

    @Test
    @DisplayName("Should get Defense Boost")
    void testGetDefenseBoost() {
        Item item = ItemFactory.getItem("Defense Boost");

        assertNotNull(item);
        assertEquals("Defense Boost", item.getName());
        assertEquals(ItemEffect.BOOST_DEFENSE, item.getEffect());
    }

    @Test
    @DisplayName("Should get Antidote")
    void testGetAntidote() {
        Item item = ItemFactory.getItem("Antidote");

        assertNotNull(item);
        assertEquals("Antidote", item.getName());
        assertEquals(ItemType.STATUS_CURE, item.getItemType());
        assertEquals(ItemEffect.CURE_STATUS, item.getEffect());
    }

    @Test
    @DisplayName("Should get Revive")
    void testGetRevive() {
        Item item = ItemFactory.getItem("Revive");

        assertNotNull(item);
        assertEquals("Revive", item.getName());
        assertEquals(ItemType.REVIVAL, item.getItemType());
        assertEquals(ItemEffect.REVIVE, item.getEffect());
    }

    @Test
    @DisplayName("Should throw exception for unknown item")
    void testUnknownItem() {
        assertThrows(IllegalArgumentException.class, () -> {
            ItemFactory.getItem("Unknown Item");
        });
    }

    @Test
    @DisplayName("Should get all available items")
    void testGetAllItems() {
        List<String> items = ItemFactory.getAllItemNames();

        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertTrue(items.contains("Health Potion"));
        assertTrue(items.contains("Mana Potion"));
        assertTrue(items.contains("Elixir"));
        assertTrue(items.contains("Attack Boost"));
        assertTrue(items.contains("Defense Boost"));
        assertTrue(items.contains("Antidote"));
        assertTrue(items.contains("Revive"));
    }

    @Test
    @DisplayName("Health Potion should restore HP")
    void testHealthPotionEffect() {
        Item item = ItemFactory.getItem("Health Potion");
        testCharacter.takeDamage(60);
        int hpBefore = testCharacter.getCurrentHP();

        item.applyEffect(testCharacter);

        assertTrue(testCharacter.getCurrentHP() > hpBefore);
    }

    @Test
    @DisplayName("Mana Potion should restore MP")
    void testManaPotionEffect() {
        Item item = ItemFactory.getItem("Mana Potion");
        testCharacter.useMP(40);
        int mpBefore = testCharacter.getCurrentMP();

        item.applyEffect(testCharacter);

        assertTrue(testCharacter.getCurrentMP() > mpBefore);
    }

    @Test
    @DisplayName("Revive should work on dead character")
    void testReviveEffect() {
        Item item = ItemFactory.getItem("Revive");

        assertFalse(deadCharacter.isAlive());

        item.applyEffect(deadCharacter);

        assertTrue(deadCharacter.isAlive());
        assertTrue(deadCharacter.getCurrentHP() > 0);
    }

    @Test
    @DisplayName("Revive can only target dead allies")
    void testReviveCanUse() {
        Item item = ItemFactory.getItem("Revive");

        assertTrue(item.canUse(deadCharacter));
        assertFalse(item.canUse(testCharacter));
    }

    @Test
    @DisplayName("Health Potion can only target alive allies")
    void testHealthPotionCanUse() {
        Item item = ItemFactory.getItem("Health Potion");

        assertTrue(item.canUse(testCharacter));
        assertFalse(item.canUse(deadCharacter));
    }

    @Test
    @DisplayName("Antidote should remove status effects")
    void testAntidoteEffect() {
        Item item = ItemFactory.getItem("Antidote");

        testCharacter.applyStatusEffect(StatusEffectType.POISON, 3);
        assertTrue(testCharacter.hasStatusEffect(StatusEffectType.POISON));

        item.applyEffect(testCharacter);

        assertFalse(testCharacter.hasStatusEffect(StatusEffectType.POISON));
    }

    @Test
    @DisplayName("All items should have valid descriptions")
    void testItemDescriptions() {
        List<String> itemNames = ItemFactory.getAllItemNames();

        for (String name : itemNames) {
            Item item = ItemFactory.getItem(name);
            assertNotNull(item.getDescription());
            assertFalse(item.getDescription().isEmpty());
        }
    }
}

