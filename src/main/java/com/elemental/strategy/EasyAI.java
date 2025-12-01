package com.elemental.strategy;

import com.elemental.model.ActionType;
import com.elemental.model.BattleAction;
import com.elemental.model.Skill;
import com.elemental.model.SkillType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * FR-AI-002: Easy AI Behavior
 * Random action selection with no strategic thinking
 * - 60% basic attack (random target)
 * - 30% random skill (if MP sufficient)
 * - 10% defend
 * - No element advantage consideration
 * - No item usage
 */
public class EasyAI implements AIStrategy {

    private final Random random;

    public EasyAI() {
        this.random = new Random();
    }

    /**
     * Decide action with random weighted selection
     */
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

        // If no alive enemies, return basic attack
        if (aliveEnemies.isEmpty()) {
            BattleAction action = new BattleAction(actor, ActionType.ATTACK);
            return action;
        }

        // Random weighted decision
        int choice = random.nextInt(100);

        if (choice < 60) {
            // 60% - Basic attack
            BattleAction action = new BattleAction(actor, ActionType.ATTACK);
            action.setTarget(selectTarget(actor, aliveEnemies));
            return action;

        } else if (choice < 90) {
            // 30% - Try to use skill
            List<Skill> usableSkills = getUsableSkills(actor);

            if (!usableSkills.isEmpty()) {
                // Pick random skill
                Skill selectedSkill = usableSkills.get(random.nextInt(usableSkills.size()));
                BattleAction action = new BattleAction(actor, ActionType.SKILL);
                action.setSkill(selectedSkill);

                // Select target based on skill type
                if (selectedSkill.getSkillType() == SkillType.HEAL ||
                    selectedSkill.getSkillType() == SkillType.BUFF) {
                    // Target self or random ally
                    action.setTarget(actor);
                } else {
                    // Target random enemy
                    action.setTarget(selectTarget(actor, aliveEnemies));
                }

                return action;
            } else {
                // Not enough MP, fall back to basic attack
                BattleAction action = new BattleAction(actor, ActionType.ATTACK);
                action.setTarget(selectTarget(actor, aliveEnemies));
                return action;
            }

        } else {
            // 10% - Defend
            BattleAction action = new BattleAction(actor, ActionType.DEFEND);
            return action;
        }
    }

    /**
     * Select random target from available targets
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

        // Return random target
        return aliveTargets.get(random.nextInt(aliveTargets.size()));
    }

    /**
     * Get list of skills that actor can use (has enough MP)
     */
    private List<Skill> getUsableSkills(com.elemental.model.Character actor) {
        List<Skill> usableSkills = new ArrayList<>();
        for (Skill skill : actor.getSkills()) {
            if (actor.canUseSkill(skill)) {
                usableSkills.add(skill);
            }
        }
        return usableSkills;
    }
}
