package com.elemental.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * FR-BATTLE-001: Battle Initialization
 * FR-BATTLE-002: Turn System
 * FR-BATTLE-005: Victory/Defeat
 */
public class Battle {
    private List<Character> playerTeam;
    private List<Character> enemyTeam;
    private BattleStatus battleStatus;
    private BattleLog battleLog;
    private PriorityQueue<Character> turnOrder;
    private int turnNumber;
    private Character currentTurn;

    public Battle() {
        this.battleLog = new BattleLog();
        this.battleStatus = BattleStatus.ONGOING;
        this.turnNumber = 0;
    }

    /**
     * FR-BATTLE-001: Initialize battle with teams
     */
    public void initializeBattle(List<Character> playerTeam, List<Character> enemyTeam) {
        this.playerTeam = new ArrayList<>(playerTeam);
        this.enemyTeam = new ArrayList<>(enemyTeam);
        this.battleStatus = BattleStatus.ONGOING;
        this.turnNumber = 0;

        // Calculate turn order based on Speed (highest first)
        calculateTurnOrder();

        battleLog.log("=== BATTLE START ===");
        battleLog.log("Player Team:");
        for (Character c : playerTeam) {
            battleLog.log("  - " + c.toString());
        }
        battleLog.log("Enemy Team:");
        for (Character e : enemyTeam) {
            battleLog.log("  - " + e.toString());
        }
        battleLog.log("==================");
    }

    /**
     * Calculate turn order based on character speed (modified by buffs)
     */
    private void calculateTurnOrder() {
        turnOrder = new PriorityQueue<>(
            Comparator.comparingInt(Character::getModifiedSpeed).reversed()
        );

        // Add all alive characters
        for (Character c : playerTeam) {
            if (c.isAlive()) {
                turnOrder.add(c);
            }
        }
        for (Character e : enemyTeam) {
            if (e.isAlive()) {
                turnOrder.add(e);
            }
        }
    }

    /**
     * Get next character in turn order
     */
    public Character getNextTurn() {
        // Rebuild turn order if empty
        if (turnOrder.isEmpty()) {
            calculateTurnOrder();
            turnNumber++;
        }

        // Skip dead or stunned characters
        while (!turnOrder.isEmpty()) {
            Character next = turnOrder.poll();

            if (!next.isAlive()) {
                continue;
            }

            // Check if stunned
            if (next.hasStatusEffect(StatusEffectType.STUN)) {
                battleLog.log(next.getName() + " is stunned and cannot act!");
                continue;
            }

            currentTurn = next;
            battleLog.logTurnStart(next, turnNumber);

            // Reset defending state at start of turn
            next.setDefending(false);

            return next;
        }

        return null;
    }

    /**
     * FR-BATTLE-002: Execute battle action
     */
    public void executeAction(BattleAction action) {
        Character actor = action.getActor();
        Character target = action.getTarget();
        ActionType actionType = action.getActionType();

        battleLog.logAction(actor, actionType);

        switch (actionType) {
            case ATTACK:
                executeAttack(actor, target);
                break;
            case SKILL:
                executeSkill(actor, target, action.getSkill());
                break;
            case DEFEND:
                executeDefend(actor);
                break;
            case ITEM:
                executeItem(actor, target, action.getItem());
                break;
            case SWITCH:
                // Team mode only - to be implemented later
                battleLog.log("Switch is not available in this mode.");
                break;
        }

        // Process status effects at end of turn
        processEndOfTurn(actor);

        // Check battle end
        checkBattleEnd();
    }

    /**
     * Execute basic attack
     */
    private void executeAttack(Character attacker, Character defender) {
        int damage = DamageCalculator.calculateBasicAttack(attacker, defender);
        boolean isCritical = DamageCalculator.isCritical();

        defender.takeDamage(damage);
        battleLog.logDamage(attacker, defender, damage, isCritical);

        String elementAdvantage = DamageCalculator.getElementAdvantage(
            attacker.getElement(), defender.getElement()
        );
        if (!elementAdvantage.isEmpty()) {
            battleLog.logElementAdvantage(elementAdvantage);
        }

        if (!defender.isAlive()) {
            battleLog.logDeath(defender);
        }
    }

    /**
     * Execute skill attack
     */
    private void executeSkill(Character user, Character target, Skill skill) {
        if (skill == null) {
            battleLog.log("No skill selected!");
            return;
        }

        if (!user.canUseSkill(skill)) {
            battleLog.log(user.getName() + " cannot use " + skill.getName() + "!");
            return;
        }

        // Use MP
        user.useMP(skill.getMpCost());
        battleLog.logSkillUse(user, skill);

        // Execute skill based on type
        switch (skill.getSkillType()) {
            case DAMAGE:
                int damage = DamageCalculator.calculateDamage(user, target, skill);
                boolean isCritical = DamageCalculator.isCritical();
                target.takeDamage(damage);
                battleLog.logDamage(user, target, damage, isCritical);

                String elementAdvantage = DamageCalculator.getElementAdvantage(
                    user.getElement(), target.getElement()
                );
                if (!elementAdvantage.isEmpty()) {
                    battleLog.logElementAdvantage(elementAdvantage);
                }

                // Apply secondary effects based on skill name
                applySkillSecondaryEffects(user, target, skill);

                if (!target.isAlive()) {
                    battleLog.logDeath(target);
                }
                break;

            case HEAL:
                int healAmount = (int) (user.getMaxMP() * skill.getDamageMultiplier());
                user.restoreMP(healAmount);
                battleLog.log(user.getName() + " restored " + healAmount + " MP!");
                break;

            case BUFF:
                applyBuffEffect(user, skill);
                break;

            case DEBUFF:
                applyDebuffEffect(user, target, skill);
                break;
        }
    }

    /**
     * Apply secondary effects from skills
     */
    private void applySkillSecondaryEffects(Character user, Character target, Skill skill) {
        String skillName = skill.getName();

        if (skillName.equals("Shield Bash")) {
            // 30% chance to stun
            if (Math.random() < 0.3) {
                target.applyStatusEffect(StatusEffectType.STUN, 1);
                battleLog.logStatusEffect(target, StatusEffectType.STUN, 1);
            }
        } else if (skillName.equals("Poison Arrow")) {
            target.applyStatusEffect(StatusEffectType.POISON, 3);
            battleLog.logStatusEffect(target, StatusEffectType.POISON, 3);
        } else if (skillName.contains("Fire") || skillName.contains("Flame")) {
            // 20% chance to burn
            if (Math.random() < 0.2) {
                target.applyStatusEffect(StatusEffectType.BURN, 2);
                battleLog.logStatusEffect(target, StatusEffectType.BURN, 2);
            }
        }
    }

    /**
     * Apply buff effects
     */
    private void applyBuffEffect(Character user, Skill skill) {
        String skillName = skill.getName();

        if (skillName.equals("Iron Defense")) {
            user.applyStatusEffect(StatusEffectType.SHIELDED, 2);
            battleLog.logStatusEffect(user, StatusEffectType.SHIELDED, 2);
        } else if (skillName.equals("Quick Step")) {
            user.applyStatusEffect(StatusEffectType.SPEED_BUFF, 3);
            battleLog.logStatusEffect(user, StatusEffectType.SPEED_BUFF, 3);
        }
    }

    /**
     * Apply debuff effects
     */
    private void applyDebuffEffect(Character user, Character target, Skill skill) {
        // Additional debuff logic can be added here
    }

    /**
     * Execute defend action
     */
    private void executeDefend(Character actor) {
        actor.setDefending(true);
        battleLog.logDefend(actor);
    }

    /**
     * Execute item usage
     */
    private void executeItem(Character user, Character target, Item item) {
        if (item == null) {
            battleLog.log("No item selected!");
            return;
        }
        battleLog.log("Item usage not yet implemented.");
    }

    /**
     * Process status effects at end of turn
     */
    private void processEndOfTurn(Character character) {
        if (!character.isAlive()) return;

        // Process active status effects
        for (StatusEffectType effectType : character.getActiveStatusEffects().keySet()) {
            int duration = character.getStatusEffectDuration(effectType);

            switch (effectType) {
                case POISON:
                    int poisonDamage = (int) (character.getMaxHP() * 0.05);
                    character.takeDamage(poisonDamage);
                    battleLog.logStatusDamage(character, StatusEffectType.POISON, poisonDamage);
                    break;
                case BURN:
                    int burnDamage = (int) (character.getMaxHP() * 0.07);
                    character.takeDamage(burnDamage);
                    battleLog.logStatusDamage(character, StatusEffectType.BURN, burnDamage);
                    break;
            }

            if (!character.isAlive()) {
                battleLog.logDeath(character);
            }
        }

        // Decrement status effect durations
        character.processStatusEffects();
    }

    /**
     * FR-BATTLE-005: Check battle end condition
     */
    public BattleStatus checkBattleEnd() {
        boolean allPlayersDead = playerTeam.stream().noneMatch(Character::isAlive);
        boolean allEnemiesDead = enemyTeam.stream().noneMatch(Character::isAlive);

        if (allEnemiesDead) {
            battleStatus = BattleStatus.VICTORY;
            battleLog.logVictory();
            giveRewards();
        } else if (allPlayersDead) {
            battleStatus = BattleStatus.DEFEAT;
            battleLog.logDefeat();
        } else {
            battleStatus = BattleStatus.ONGOING;
        }

        return battleStatus;
    }

    /**
     * FR-BATTLE-005: Calculate and give rewards
     * Experience: 50 × Enemy Level per enemy
     */
    private void giveRewards() {
        int totalExp = 0;
        for (Character enemy : enemyTeam) {
            totalExp += 50 * enemy.getLevel();
        }

        battleLog.log("=== REWARDS ===");
        for (Character player : playerTeam) {
            if (player.isAlive()) {
                int oldLevel = player.getLevel();
                player.gainExperience(totalExp);
                battleLog.logExperienceGain(player, totalExp);

                if (player.getLevel() > oldLevel) {
                    battleLog.logLevelUp(player, player.getLevel());
                }
            }
        }
    }

    // Getters
    public List<Character> getPlayerTeam() {
        return new ArrayList<>(playerTeam);
    }

    public List<Character> getEnemyTeam() {
        return new ArrayList<>(enemyTeam);
    }

    public BattleStatus getBattleStatus() {
        return battleStatus;
    }

    public BattleLog getBattleLog() {
        return battleLog;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public Character getCurrentTurn() {
        return currentTurn;
    }

    public boolean isPlayerTurn(Character character) {
        return playerTeam.contains(character);
    }
}
