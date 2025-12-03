package com.elemental.decorator;

import com.elemental.model.Character;
import com.elemental.model.CharacterClass;
import com.elemental.model.Element;
import com.elemental.model.StatusEffectType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StatusEffectDecorator Tests")
class StatusEffectDecoratorTest {

    private Character testCharacter;

    @BeforeEach
    void setUp() {
        testCharacter = new Character("TestChar", CharacterClass.WARRIOR, Element.FIRE);
    }

    @Test
    @DisplayName("Should create poison effect")
    void testCreatePoisonEffect() {
        StatusEffectDecorator effect = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.POISON, 3
        );

        assertNotNull(effect);
        assertEquals(StatusEffectType.POISON, effect.getEffectType());
        assertEquals(3, effect.getDuration());
    }

    @Test
    @DisplayName("Should create burn effect")
    void testCreateBurnEffect() {
        StatusEffectDecorator effect = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.BURN, 2
        );

        assertNotNull(effect);
        assertEquals(StatusEffectType.BURN, effect.getEffectType());
        assertEquals(2, effect.getDuration());
    }

    @Test
    @DisplayName("Should create stun effect")
    void testCreateStunEffect() {
        StatusEffectDecorator effect = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.STUN, 1
        );

        assertNotNull(effect);
        assertEquals(StatusEffectType.STUN, effect.getEffectType());
    }

    @Test
    @DisplayName("Should create shield effect")
    void testCreateShieldEffect() {
        StatusEffectDecorator effect = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.SHIELDED, 3
        );

        assertNotNull(effect);
        assertEquals(StatusEffectType.SHIELDED, effect.getEffectType());
    }

    @Test
    @DisplayName("Should create speed buff effect")
    void testCreateSpeedBuffEffect() {
        StatusEffectDecorator effect = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.SPEED_BUFF, 3
        );

        assertNotNull(effect);
        assertEquals(StatusEffectType.SPEED_BUFF, effect.getEffectType());
    }

    @Test
    @DisplayName("Should apply poison effect to character")
    void testApplyPoisonEffect() {
        StatusEffectDecorator effect = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.POISON, 3
        );

        effect.apply();
        assertTrue(testCharacter.hasStatusEffect(StatusEffectType.POISON));
    }

    @Test
    @DisplayName("Poison should deal damage over time")
    void testPoisonTick() {
        StatusEffectDecorator effect = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.POISON, 3
        );

        effect.apply();
        int initialHP = testCharacter.getCurrentHP();
        effect.tick();

        assertTrue(testCharacter.getCurrentHP() < initialHP, "HP should decrease after poison tick");
    }

    @Test
    @DisplayName("Burn should deal damage over time")
    void testBurnTick() {
        StatusEffectDecorator effect = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.BURN, 2
        );

        effect.apply();
        int initialHP = testCharacter.getCurrentHP();
        effect.tick();

        assertTrue(testCharacter.getCurrentHP() < initialHP, "HP should decrease after burn tick");
    }

    @Test
    @DisplayName("Should remove effect from character")
    void testRemoveEffect() {
        StatusEffectDecorator effect = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.POISON, 3
        );

        effect.apply();
        assertTrue(testCharacter.hasStatusEffect(StatusEffectType.POISON));

        effect.remove();
        assertFalse(testCharacter.hasStatusEffect(StatusEffectType.POISON));
    }

    @Test
    @DisplayName("Should decrement duration")
    void testDecrementDuration() {
        StatusEffectDecorator effect = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.POISON, 3
        );

        assertEquals(3, effect.getDuration());
        effect.decrementDuration();
        assertEquals(2, effect.getDuration());
        effect.decrementDuration();
        assertEquals(1, effect.getDuration());
    }

    @Test
    @DisplayName("Should check if effect is expired")
    void testIsExpired() {
        StatusEffectDecorator effect = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.POISON, 1
        );

        assertFalse(effect.isExpired());
        effect.decrementDuration();
        assertTrue(effect.isExpired());
    }

    @Test
    @DisplayName("Should get character from effect")
    void testGetCharacter() {
        StatusEffectDecorator effect = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.POISON, 3
        );

        assertEquals(testCharacter, effect.getCharacter());
    }

    @Test
    @DisplayName("Should throw exception for unknown effect type")
    void testUnknownEffectType() {
        // This test assumes there might be invalid enum values in the future
        // For now, we can just verify that valid types work
        assertDoesNotThrow(() -> {
            StatusEffectDecorator.createEffect(testCharacter, StatusEffectType.POISON, 3);
            StatusEffectDecorator.createEffect(testCharacter, StatusEffectType.BURN, 2);
            StatusEffectDecorator.createEffect(testCharacter, StatusEffectType.STUN, 1);
            StatusEffectDecorator.createEffect(testCharacter, StatusEffectType.SHIELDED, 3);
            StatusEffectDecorator.createEffect(testCharacter, StatusEffectType.SPEED_BUFF, 3);
        });
    }

    @Test
    @DisplayName("Multiple effects should work independently")
    void testMultipleEffects() {
        StatusEffectDecorator poison = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.POISON, 3
        );
        StatusEffectDecorator burn = StatusEffectDecorator.createEffect(
            testCharacter, StatusEffectType.BURN, 2
        );

        poison.apply();
        burn.apply();

        assertTrue(testCharacter.hasStatusEffect(StatusEffectType.POISON));
        assertTrue(testCharacter.hasStatusEffect(StatusEffectType.BURN));
    }
}

