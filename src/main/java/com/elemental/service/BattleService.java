package com.elemental.service;

import com.elemental.model.Battle;
import com.elemental.model.BattleStatus;
import com.elemental.model.Character;

import java.util.List;

/**
 * Service for managing battle instances
 */
public class BattleService {
    private Battle currentBattle;

    public BattleService() {
        this.currentBattle = null;
    }

    /**
     * Start a new battle
     */
    public Battle startBattle(List<Character> playerTeam, List<Character> enemyTeam) {
        currentBattle = new Battle();
        currentBattle.initializeBattle(playerTeam, enemyTeam);
        return currentBattle;
    }

    /**
     * Get current battle
     */
    public Battle getCurrentBattle() {
        return currentBattle;
    }

    /**
     * Check if battle is active
     */
    public boolean isBattleActive() {
        return currentBattle != null && currentBattle.getBattleStatus() == BattleStatus.ONGOING;
    }

    /**
     * End current battle
     */
    public void endBattle() {
        currentBattle = null;
    }

    /**
     * Get battle status
     */
    public BattleStatus getBattleStatus() {
        if (currentBattle == null) {
            return null;
        }
        return currentBattle.getBattleStatus();
    }
}
