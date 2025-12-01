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
 * FR-AI-003: Medium AI Behavior
 * Basic strategic AI with HP/MP management
 * Logic:
 * - IF HP < 30%: Prioritize Defend or Heal skill
 * - ELSE IF MP > 50%: Use highest damage skill
 * - ELSE IF MP < 20%: Basic attack only
 * Target Selection:
 * - Lowest HP enemy
 * - Consider element advantage
 */
public class MediumAI implements AIStrategy {

    @Override
    public BattleAction decideAction(com.elemental.model.Character actor,
                                    List<com.elemental.model.Character> allies,
                                    List<com.elemental.model.Character> enemies) {
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

        // Strategy 1: Low HP - Prioritize survival
        if (hpPercentage < 30) {
            // Try to find heal skill
            Skill healSkill = findSkillByType(actor, SkillType.HEAL);
            if (healSkill != null && actor.canUseSkill(healSkill)) {
                BattleAction action = new BattleAction(actor, ActionType.SKILL);
                action.setSkill(healSkill);
                action.setTarget(actor);
                return action;
            }

            // No heal skill or not enough MP, defend
            BattleAction action = new BattleAction(actor, ActionType.DEFEND);
            return action;
        }

        // Strategy 2: High MP - Use powerful skills
        if (mpPercentage > 50) {
            Skill bestSkill = findHighestDamageSkill(actor);
            if (bestSkill != null && actor.canUseSkill(bestSkill)) {
                BattleAction action = new BattleAction(actor, ActionType.SKILL);
                action.setSkill(bestSkill);

                // Target selection based on skill type
                if (bestSkill.getSkillType() == SkillType.HEAL ||
                    bestSkill.getSkillType() == SkillType.BUFF) {
                    action.setTarget(actor);
                } else {
                    action.setTarget(selectTarget(actor, aliveEnemies));
                }

                return action;
            }
        }

        // Strategy 3: Low MP - Basic attack only
        if (mpPercentage < 20) {
            BattleAction action = new BattleAction(actor, ActionType.ATTACK);
            action.setTarget(selectTarget(actor, aliveEnemies));
            return action;
        }

        // Default: Use a medium cost skill or attack
        Skill cheapSkill = findCheapestDamageSkill(actor);
        if (cheapSkill != null && actor.canUseSkill(cheapSkill)) {
            BattleAction action = new BattleAction(actor, ActionType.SKILL);
            action.setSkill(cheapSkill);
            action.setTarget(selectTarget(actor, aliveEnemies));
            return action;
        }

        // Fallback: Basic attack
        BattleAction action = new BattleAction(actor, ActionType.ATTACK);
        action.setTarget(selectTarget(actor, aliveEnemies));
        return action;
    }

    /**
     * Select target: prioritize lowest HP enemy with element advantage consideration
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

        // Find targets with element advantage
        List<com.elemental.model.Character> advantagedTargets = new ArrayList<>();
        for (com.elemental.model.Character target : aliveTargets) {
            if (hasElementAdvantage(actor.getElement(), target.getElement())) {
                advantagedTargets.add(target);
            }
        }

        // If we have element advantage, target lowest HP among them
        if (!advantagedTargets.isEmpty()) {
            return findLowestHP(advantagedTargets);
        }

        // Otherwise, target lowest HP overall
        return findLowestHP(aliveTargets);
    }

    /**
     * Find character with lowest HP
     */
    private com.elemental.model.Character findLowestHP(List<com.elemental.model.Character> characters) {
        return characters.stream()
            .min(Comparator.comparingInt(com.elemental.model.Character::getCurrentHP))
            .orElse(characters.get(0));
    }

    /**
     * Check if attacker element has advantage over defender element
     * Fire > Earth > Water > Fire
     */
    private boolean hasElementAdvantage(Element attacker, Element defender) {
        return (attacker == Element.FIRE && defender == Element.EARTH) ||
               (attacker == Element.EARTH && defender == Element.WATER) ||
               (attacker == Element.WATER && defender == Element.FIRE);
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
     * Find highest damage skill that can be used
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
     * Find cheapest damage skill that can be used
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
}
