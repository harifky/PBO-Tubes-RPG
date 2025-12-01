package com.elemental.strategy;

import com.elemental.model.BattleAction;

import java.util.List;

/**
 * FR-AI-001: AI Strategy Pattern Interface
 * Strategy pattern for AI decision making in battle
 */
public interface AIStrategy {

    /**
     * Decide which action the AI should take based on battle state
     * @param actor The character taking action
     * @param allies List of AI's allies
     * @param enemies List of enemy characters
     * @return BattleAction containing the decision
     */
    BattleAction decideAction(com.elemental.model.Character actor,
                             List<com.elemental.model.Character> allies,
                             List<com.elemental.model.Character> enemies);

    /**
     * Select target from list of characters
     * @param actor The character selecting target
     * @param targets List of potential targets
     * @return Selected target character
     */
    com.elemental.model.Character selectTarget(com.elemental.model.Character actor,
                                                List<com.elemental.model.Character> targets);
}
