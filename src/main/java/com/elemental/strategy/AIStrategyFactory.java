package com.elemental.strategy;

import com.elemental.model.AIDifficulty;

/**
 * Factory class for creating AI strategies based on difficulty
 * Implements Strategy Pattern
 */
public class AIStrategyFactory {

    /**
     * Create appropriate AI strategy based on difficulty level
     * @param difficulty The AI difficulty level
     * @return AIStrategy implementation
     */
    public static AIStrategy create(AIDifficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return new EasyAI();
            case MEDIUM:
                return new MediumAI();
            case HARD:
                return new HardAI();
            default:
                return new EasyAI(); // Default fallback
        }
    }
}

