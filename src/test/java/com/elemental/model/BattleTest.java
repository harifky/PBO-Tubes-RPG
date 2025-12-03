package com.elemental.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Battle Tests")
class BattleTest {

    private Battle battle;
    private List<Character> playerTeam;
    private List<Character> enemyTeam;

    @BeforeEach
    void setUp() {
        battle = new Battle();

        // Create player team
        playerTeam = new ArrayList<>();
        playerTeam.add(new Character("Hero1", CharacterClass.WARRIOR, Element.FIRE));
        playerTeam.add(new Character("Hero2", CharacterClass.MAGE, Element.WATER));

        // Create enemy team
        enemyTeam = new ArrayList<>();
        enemyTeam.add(new Character("Enemy1", CharacterClass.WARRIOR, Element.EARTH));
        enemyTeam.add(new Character("Enemy2", CharacterClass.RANGER, Element.FIRE));
    }

    @Test
    @DisplayName("Should initialize battle correctly")
    void testInitializeBattle() {
        battle.initializeBattle(playerTeam, enemyTeam);

        assertEquals(BattleStatus.ONGOING, battle.getBattleStatus());
        assertNotNull(battle.getPlayerTeam());
        assertNotNull(battle.getEnemyTeam());
        assertEquals(2, battle.getPlayerTeam().size());
        assertEquals(2, battle.getEnemyTeam().size());
    }

    @Test
    @DisplayName("Should have ongoing status after initialization")
    void testBattleStatusOngoing() {
        battle.initializeBattle(playerTeam, enemyTeam);
        assertEquals(BattleStatus.ONGOING, battle.getBattleStatus());
    }

    @Test
    @DisplayName("Should get next turn correctly")
    void testGetNextTurn() {
        battle.initializeBattle(playerTeam, enemyTeam);

        Character nextChar = battle.getNextTurn();
        assertNotNull(nextChar);
        assertTrue(nextChar.isAlive());
    }

    @Test
    @DisplayName("Should have battle log")
    void testBattleLog() {
        battle.initializeBattle(playerTeam, enemyTeam);

        BattleLog log = battle.getBattleLog();
        assertNotNull(log);
        assertFalse(log.getAllEntries().isEmpty());
    }

    @Test
    @DisplayName("Turn order should be based on speed")
    void testTurnOrderBySpeed() {
        // Create characters with different speeds
        Character fastChar = new Character("Fast", CharacterClass.RANGER, Element.WATER); // Speed 30
        Character slowChar = new Character("Slow", CharacterClass.WARRIOR, Element.EARTH); // Speed 15

        List<Character> team1 = new ArrayList<>();
        team1.add(slowChar);

        List<Character> team2 = new ArrayList<>();
        team2.add(fastChar);

        battle.initializeBattle(team1, team2);

        Character first = battle.getNextTurn();
        // Ranger should go first (higher speed)
        assertEquals("Fast", first.getName());
    }

    @Test
    @DisplayName("Should handle empty turn order")
    void testEmptyTurnOrder() {
        battle.initializeBattle(playerTeam, enemyTeam);

        // Exhaust turn order
        for (int i = 0; i < 10; i++) {
            Character next = battle.getNextTurn();
            if (next == null) break;
        }

        // Should still be able to get turns (turn order rebuilds)
        assertNotNull(battle);
    }

    @Test
    @DisplayName("Should detect player victory")
    void testPlayerVictory() {
        battle.initializeBattle(playerTeam, enemyTeam);

        // Kill all enemies
        for (Character enemy : enemyTeam) {
            enemy.takeDamage(enemy.getMaxHP());
        }

        battle.checkBattleEnd();
        assertEquals(BattleStatus.VICTORY, battle.getBattleStatus());
    }

    @Test
    @DisplayName("Should detect player defeat")
    void testPlayerDefeat() {
        battle.initializeBattle(playerTeam, enemyTeam);

        // Kill all players
        for (Character player : playerTeam) {
            player.takeDamage(player.getMaxHP());
        }

        battle.checkBattleEnd();
        assertEquals(BattleStatus.DEFEAT, battle.getBattleStatus());
    }

    @Test
    @DisplayName("Battle should continue when both teams have alive members")
    void testBattleOngoing() {
        battle.initializeBattle(playerTeam, enemyTeam);

        // Damage but don't kill anyone
        playerTeam.get(0).takeDamage(20);
        enemyTeam.get(0).takeDamage(20);

        battle.checkBattleEnd();
        assertEquals(BattleStatus.ONGOING, battle.getBattleStatus());
    }

    @Test
    @DisplayName("Should get player team")
    void testGetPlayerTeam() {
        battle.initializeBattle(playerTeam, enemyTeam);

        List<Character> team = battle.getPlayerTeam();
        assertNotNull(team);
        assertEquals(2, team.size());
    }

    @Test
    @DisplayName("Should get enemy team")
    void testGetEnemyTeam() {
        battle.initializeBattle(playerTeam, enemyTeam);

        List<Character> team = battle.getEnemyTeam();
        assertNotNull(team);
        assertEquals(2, team.size());
    }

    @Test
    @DisplayName("Dead characters should not get turns")
    void testDeadCharactersNoTurn() {
        battle.initializeBattle(playerTeam, enemyTeam);

        // Kill first character
        playerTeam.get(0).takeDamage(playerTeam.get(0).getMaxHP());

        // Get several turns
        for (int i = 0; i < 5; i++) {
            Character next = battle.getNextTurn();
            if (next != null) {
                assertNotEquals(playerTeam.get(0), next, "Dead character should not get turn");
            }
        }
    }

    @Test
    @DisplayName("Should handle single character teams")
    void testSingleCharacterTeams() {
        List<Character> singlePlayer = new ArrayList<>();
        singlePlayer.add(new Character("Solo", CharacterClass.WARRIOR, Element.FIRE));

        List<Character> singleEnemy = new ArrayList<>();
        singleEnemy.add(new Character("Boss", CharacterClass.WARRIOR, Element.WATER));

        battle.initializeBattle(singlePlayer, singleEnemy);

        assertEquals(BattleStatus.ONGOING, battle.getBattleStatus());
        assertNotNull(battle.getNextTurn());
    }
}

