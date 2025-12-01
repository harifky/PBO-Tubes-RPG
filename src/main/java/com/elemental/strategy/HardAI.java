package com.elemental.strategy;

import com.elemental.model.ActionType;
import com.elemental.model.BattleAction;
import com.elemental.model.Element;
import com.elemental.model.Skill;
import com.elemental.model.SkillType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * FR-AI-004: Hard AI Behavior
 * Advanced tactical AI with threat assessment and counter strategies
 * Features:
 * - Threat Assessment: Calculate threat = enemy.Attack × elementAdvantage
 * - Target Priority: High Attack > Low HP > Element advantage
 * - Resource Management: Save MP for finishing blow, use buffs early
 * - Defensive Mode (HP < 40%): Prioritize survival, use defensive skills
 * - Counter Strategy: Adapt to player patterns
 */
public class HardAI implements AIStrategy {

    private int turnCount = 0;
    private int playerDefendCount = 0;

    @Override
    public BattleAction decideAction(com.elemental.model.Character actor,
                                    List<com.elemental.model.Character> allies,
                                    List<com.elemental.model.Character> enemies) {
        turnCount++;

        // Filter alive enemies
        List<com.elemental.model.Character> aliveEnemies = new ArrayList<>();
        for (com.elemental.model.Character enemy : enemies) {
            if (enemy.isAlive()) {
                aliveEnemies.add(enemy);
            }
        }

        // Safety check
        if (aliveEnemies.isEmpty()) {
            BattleAction action = new BattleAction(actor, ActionType.ATTACK);
            return action;
        }

        // Calculate HP percentage
        double hpPercentage = (double) actor.getCurrentHP() / actor.getMaxHP() * 100;
        double mpPercentage = (double) actor.getCurrentMP() / actor.getMaxMP() * 100;

        // === DEFENSIVE MODE (HP < 40%) ===
        if (hpPercentage < 40) {
            return executeDefensiveStrategy(actor, aliveEnemies);
        }

        // === EARLY GAME BUFF STRATEGY (Turn 1-2, High HP) ===
        if (turnCount <= 2 && hpPercentage > 70) {
            Skill buffSkill = findSkillByType(actor, SkillType.BUFF);
            if (buffSkill != null && actor.canUseSkill(buffSkill)) {
                BattleAction action = new BattleAction(actor, ActionType.SKILL);
                action.setSkill(buffSkill);
                action.setTarget(actor);
                return action;
            }
        }

        // === COUNTER STRATEGY ===
        // If player defends often, use buffs/debuffs instead of attacking
        if (playerDefendCount > 2 && mpPercentage > 30) {
            Skill debuffSkill = findSkillByType(actor, SkillType.DEBUFF);
            if (debuffSkill != null && actor.canUseSkill(debuffSkill)) {
                BattleAction action = new BattleAction(actor, ActionType.SKILL);
                action.setSkill(debuffSkill);
                action.setTarget(selectHighestThreatTarget(actor, aliveEnemies));
                return action;
            }
        }

        // === FINISHING BLOW STRATEGY ===
        // If enemy has low HP and we have MP, use highest damage skill
        com.elemental.model.Character weakestEnemy = findLowestHP(aliveEnemies);
        if (weakestEnemy != null && mpPercentage > 20) {
            double enemyHpPercentage = (double) weakestEnemy.getCurrentHP() / weakestEnemy.getMaxHP() * 100;

            if (enemyHpPercentage < 30) {
                Skill finishingSkill = findHighestDamageSkill(actor);
                if (finishingSkill != null && actor.canUseSkill(finishingSkill)) {
                    BattleAction action = new BattleAction(actor, ActionType.SKILL);
                    action.setSkill(finishingSkill);
                    action.setTarget(weakestEnemy);
                    return action;
                }
            }
        }

        // === RESOURCE MANAGEMENT ===
        // High MP: Use skills strategically
        if (mpPercentage > 60) {
            Skill bestSkill = findBestStrategicSkill(actor, aliveEnemies);
            if (bestSkill != null && actor.canUseSkill(bestSkill)) {
                BattleAction action = new BattleAction(actor, ActionType.SKILL);
                action.setSkill(bestSkill);

                if (bestSkill.getSkillType() == SkillType.DAMAGE ||
                    bestSkill.getSkillType() == SkillType.DEBUFF) {
                    action.setTarget(selectTarget(actor, aliveEnemies));
                } else {
                    action.setTarget(actor);
                }

                return action;
            }
        }

        // Medium MP: Use cheap skills
        if (mpPercentage > 30) {
            Skill cheapSkill = findCheapestDamageSkill(actor);
            if (cheapSkill != null && actor.canUseSkill(cheapSkill)) {
                BattleAction action = new BattleAction(actor, ActionType.SKILL);
                action.setSkill(cheapSkill);
                action.setTarget(selectTarget(actor, aliveEnemies));
                return action;
            }
        }

        // === DEFAULT: Basic Attack ===
        BattleAction action = new BattleAction(actor, ActionType.ATTACK);
        action.setTarget(selectTarget(actor, aliveEnemies));
        return action;
    }

    /**
     * Execute defensive strategy when HP is low
     */
    private BattleAction executeDefensiveStrategy(com.elemental.model.Character actor,
                                                   List<com.elemental.model.Character> aliveEnemies) {
        // Try to heal
        Skill healSkill = findSkillByType(actor, SkillType.HEAL);
        if (healSkill != null && actor.canUseSkill(healSkill)) {
            BattleAction action = new BattleAction(actor, ActionType.SKILL);
            action.setSkill(healSkill);
            action.setTarget(actor);
            return action;
        }

        // Try to use defensive buff
        Skill buffSkill = findSkillByType(actor, SkillType.BUFF);
        if (buffSkill != null && actor.canUseSkill(buffSkill)) {
            BattleAction action = new BattleAction(actor, ActionType.SKILL);
            action.setSkill(buffSkill);
            action.setTarget(actor);
            return action;
        }

        // Try to debuff strongest enemy
        Skill debuffSkill = findSkillByType(actor, SkillType.DEBUFF);
        if (debuffSkill != null && actor.canUseSkill(debuffSkill)) {
            BattleAction action = new BattleAction(actor, ActionType.SKILL);
            action.setSkill(debuffSkill);
            action.setTarget(selectHighestThreatTarget(actor, aliveEnemies));
            return action;
        }

        // Last resort: Defend
        return new BattleAction(actor, ActionType.DEFEND);
    }

    /**
     * Advanced target selection with threat assessment
     * Priority: High Attack > Low HP > Element Advantage
     */
    @Override
    public com.elemental.model.Character selectTarget(com.elemental.model.Character actor,
                                                       List<com.elemental.model.Character> targets) {
        if (targets.isEmpty()) {
            return null;
        }

        // Filter alive targets
        List<com.elemental.model.Character> aliveTargets = new ArrayList<>();
        for (com.elemental.model.Character target : targets) {
            if (target.isAlive()) {
                aliveTargets.add(target);
            }
        }

        if (aliveTargets.isEmpty()) {
            return null;
        }

        // Calculate threat for each target
        return selectHighestThreatTarget(actor, aliveTargets);
    }

    /**
     * Select target with highest threat level
     */
    private com.elemental.model.Character selectHighestThreatTarget(com.elemental.model.Character actor,
                                                                     List<com.elemental.model.Character> targets) {
        com.elemental.model.Character bestTarget = null;
        double highestThreat = -1;

        for (com.elemental.model.Character target : targets) {
            if (!target.isAlive()) continue;

            // Calculate threat score
            double threat = calculateThreat(actor, target);

            if (threat > highestThreat) {
                highestThreat = threat;
                bestTarget = target;
            }
        }

        return bestTarget != null ? bestTarget : targets.get(0);
    }

    /**
     * Calculate threat value for a target
     * threat = (attack * elementMultiplier) + (lowHpBonus) + (highAttackBonus)
     */
    private double calculateThreat(com.elemental.model.Character actor,
                                   com.elemental.model.Character target) {
        double threat = 0;

        // Base threat from attack
        threat += target.getAttack();

        // Element advantage multiplier
        if (hasElementAdvantage(actor.getElement(), target.getElement())) {
            threat *= 1.5; // We have advantage - higher priority to finish them
        } else if (hasElementAdvantage(target.getElement(), actor.getElement())) {
            threat *= 1.3; // They have advantage - threat to us
        }

        // Low HP bonus (prioritize finishing weak enemies)
        double hpPercentage = (double) target.getCurrentHP() / target.getMaxHP();
        if (hpPercentage < 0.3) {
            threat += 50; // High priority to finish
        } else if (hpPercentage < 0.5) {
            threat += 20;
        }

        // High attack bonus
        if (target.getAttack() > 30) {
            threat += 30;
        }

        return threat;
    }

    /**
     * Check element advantage
     */
    private boolean hasElementAdvantage(Element attacker, Element defender) {
        return (attacker == Element.FIRE && defender == Element.EARTH) ||
               (attacker == Element.EARTH && defender == Element.WATER) ||
               (attacker == Element.WATER && defender == Element.FIRE);
    }

    /**
     * Find best strategic skill considering enemy composition
     */
    private Skill findBestStrategicSkill(com.elemental.model.Character actor,
                                         List<com.elemental.model.Character> enemies) {
        // Check if debuff would be valuable
        com.elemental.model.Character strongestEnemy = enemies.stream()
            .max(Comparator.comparingInt(com.elemental.model.Character::getAttack))
            .orElse(null);

        if (strongestEnemy != null && strongestEnemy.getAttack() > 30) {
            Skill debuffSkill = findSkillByType(actor, SkillType.DEBUFF);
            if (debuffSkill != null) {
                return debuffSkill;
            }
        }

        // Otherwise use highest damage skill
        return findHighestDamageSkill(actor);
    }

    /**
     * Find skill by type
     */
    private Skill findSkillByType(com.elemental.model.Character actor, SkillType type) {
        for (Skill skill : actor.getSkills()) {
            if (skill.getSkillType() == type) {
                return skill;
            }
        }
        return null;
    }

    /**
     * Find highest damage skill
     */
    private Skill findHighestDamageSkill(com.elemental.model.Character actor) {
        Skill bestSkill = null;
        double maxDamage = 0;

        for (Skill skill : actor.getSkills()) {
            if (skill.getSkillType() == SkillType.DAMAGE && actor.canUseSkill(skill)) {
                if (skill.getDamageMultiplier() > maxDamage) {
                    maxDamage = skill.getDamageMultiplier();
                    bestSkill = skill;
                }
            }
        }

        return bestSkill;
    }

    /**
     * Find cheapest damage skill
     */
    private Skill findCheapestDamageSkill(com.elemental.model.Character actor) {
        Skill cheapestSkill = null;
        int minCost = Integer.MAX_VALUE;

        for (Skill skill : actor.getSkills()) {
            if (skill.getSkillType() == SkillType.DAMAGE && actor.canUseSkill(skill)) {
                if (skill.getMpCost() < minCost) {
                    minCost = skill.getMpCost();
                    cheapestSkill = skill;
                }
            }
        }

        return cheapestSkill;
    }

    /**
     * Find character with lowest HP
     */
    private com.elemental.model.Character findLowestHP(List<com.elemental.model.Character> characters) {
        return characters.stream()
            .filter(com.elemental.model.Character::isAlive)
            .min(Comparator.comparingInt(com.elemental.model.Character::getCurrentHP))
            .orElse(null);
    }

    /**
     * Track player defend pattern (for counter strategy)
     */
    public void notifyPlayerDefend() {
        playerDefendCount++;
    }

    /**
     * Reset counters for new battle
     */
    public void resetCounters() {
        turnCount = 0;
        playerDefendCount = 0;
    }
}
