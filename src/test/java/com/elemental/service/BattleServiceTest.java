package com.elemental.service;

import com.elemental.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BattleService Tests")
class BattleServiceTest {

    private BattleService battleService;
    private List<com.elemental.model.Character> playerTeam;
    private List<com.elemental.model.Character> enemyTeam;

    @BeforeEach
    void setUp() {
        battleService = new BattleService();

        // Create player team
        playerTeam = new ArrayList<>();
        playerTeam.add(new com.elemental.model.Character("Hero1", CharacterClass.WARRIOR, Element.FIRE));
        playerTeam.add(new com.elemental.model.Character("Hero2", CharacterClass.MAGE, Element.WATER));

        // Create enemy team
        enemyTeam = new ArrayList<>();
        enemyTeam.add(new com.elemental.model.Character("Enemy1", CharacterClass.WARRIOR, Element.EARTH));
        enemyTeam.add(new com.elemental.model.Character("Enemy2", CharacterClass.RANGER, Element.FIRE));
    }

    @Test
    @DisplayName("Should start a new battle")
    void testStartBattle() {
        Battle battle = battleService.startBattle(playerTeam, enemyTeam);

        assertNotNull(battle);
        assertNotNull(battleService.getCurrentBattle());
        assertEquals(BattleStatus.ONGOING, battleService.getBattleStatus());
    }

    @Test
    @DisplayName("Battle should be active after starting")
    void testIsBattleActive() {
        assertFalse(battleService.isBattleActive());

        battleService.startBattle(playerTeam, enemyTeam);

        assertTrue(battleService.isBattleActive());
    }

    @Test
    @DisplayName("Should get current battle")
    void testGetCurrentBattle() {
        assertNull(battleService.getCurrentBattle());

        Battle battle = battleService.startBattle(playerTeam, enemyTeam);

        assertNotNull(battleService.getCurrentBattle());
        assertEquals(battle, battleService.getCurrentBattle());
    }

    @Test
    @DisplayName("Should end battle")
    void testEndBattle() {
        battleService.startBattle(playerTeam, enemyTeam);
        assertTrue(battleService.isBattleActive());

        battleService.endBattle();

        assertNull(battleService.getCurrentBattle());
        assertFalse(battleService.isBattleActive());
    }

    @Test
    @DisplayName("Should get battle status")
    void testGetBattleStatus() {
        assertNull(battleService.getBattleStatus());

        battleService.startBattle(playerTeam, enemyTeam);

        assertNotNull(battleService.getBattleStatus());
        assertEquals(BattleStatus.ONGOING, battleService.getBattleStatus());
    }

    @Test
    @DisplayName("Should handle multiple battles")
    void testMultipleBattles() {
        // First battle
        Battle battle1 = battleService.startBattle(playerTeam, enemyTeam);
        assertNotNull(battle1);

        // End first battle
        battleService.endBattle();
        assertNull(battleService.getCurrentBattle());

        // Start second battle
        Battle battle2 = battleService.startBattle(playerTeam, enemyTeam);
        assertNotNull(battle2);
        assertNotEquals(battle1, battle2);
    }

    @Test
    @DisplayName("Should return null status when no battle")
    void testNullStatusWhenNoBattle() {
        assertNull(battleService.getBattleStatus());
    }

    @Test
    @DisplayName("Battle should not be active when no battle started")
    void testNotActiveWhenNoBattle() {
        assertFalse(battleService.isBattleActive());
    }
}

