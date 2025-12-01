package com.elemental.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * FR-BATTLE-006: Battle Log with Observer Pattern
 * Logs battle events and maintains a scrollable history
 */
public class BattleLog {
    private static final int MAX_VISIBLE_ENTRIES = 10;
    private LinkedList<String> logEntries;
    private List<BattleLogObserver> observers;

    public BattleLog() {
        this.logEntries = new LinkedList<>();
        this.observers = new ArrayList<>();
    }

    public void addObserver(BattleLogObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BattleLogObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(String message) {
        for (BattleLogObserver observer : observers) {
            observer.onLogUpdate(message);
        }
    }

    public void log(String message) {
        logEntries.add(message);
        notifyObservers(message);
    }

    public void logTurnStart(Character character, int turnNumber) {
        log(String.format("=== Turn %d: %s's turn ===", turnNumber, character.getName()));
    }

    public void logAction(Character actor, ActionType actionType) {
        log(String.format("%s used %s!", actor.getName(), actionType));
    }

    public void logDamage(Character attacker, Character target, int damage, boolean isCritical) {
        String critText = isCritical ? " CRITICAL HIT!" : "";
        log(String.format("%s dealt %d damage to %s!%s", attacker.getName(), damage, target.getName(), critText));
    }

    public void logSkillUse(Character user, Skill skill) {
        log(String.format("%s used %s! (-%d MP)", user.getName(), skill.getName(), skill.getMpCost()));
    }

    public void logItemUse(Character user, Item item, Character target) {
        if (user == target) {
            log(String.format("%s used %s!", user.getName(), item.getName()));
        } else {
            log(String.format("%s used %s on %s!", user.getName(), item.getName(), target.getName()));
        }
    }

    public void logHeal(Character healer, Character target, int amount) {
        log(String.format("%s healed %s for %d HP!", healer.getName(), target.getName(), amount));
    }

    public void logDefend(Character character) {
        log(String.format("%s is defending! (50%% damage reduction)", character.getName()));
    }

    public void logStatusEffect(Character target, StatusEffectType effectType, int duration) {
        log(String.format("%s is now %s for %d turn(s)!", target.getName(), effectType, duration));
    }

    public void logStatusEffectExpired(Character target, StatusEffectType effectType) {
        log(String.format("%s's %s effect has worn off.", target.getName(), effectType));
    }

    public void logStatusDamage(Character character, StatusEffectType effectType, int damage) {
        log(String.format("%s took %d damage from %s!", character.getName(), damage, effectType));
    }

    public void logHPChange(Character character, int oldHP, int newHP) {
        log(String.format("%s: %d/%d HP -> %d/%d HP",
                character.getName(), oldHP, character.getMaxHP(), newHP, character.getMaxHP()));
    }

    public void logMPChange(Character character, int oldMP, int newMP) {
        log(String.format("%s: %d/%d MP -> %d/%d MP",
                character.getName(), oldMP, character.getMaxMP(), newMP, character.getMaxMP()));
    }

    public void logElementAdvantage(String message) {
        log(">>> " + message + " <<<");
    }

    public void logLevelUp(Character character, int newLevel) {
        log(String.format("🎉 %s leveled up to Level %d!", character.getName(), newLevel));
    }

    public void logExperienceGain(Character character, int exp) {
        log(String.format("%s gained %d EXP!", character.getName(), exp));
    }

    public void logDeath(Character character) {
        log(String.format("💀 %s has been defeated!", character.getName()));
    }

    public void logVictory() {
        log("======================");
        log("    VICTORY!");
        log("======================");
    }

    public void logDefeat() {
        log("======================");
        log("    DEFEAT...");
        log("======================");
    }

    public List<String> getRecentEntries() {
        int size = logEntries.size();
        int startIndex = Math.max(0, size - MAX_VISIBLE_ENTRIES);
        return new ArrayList<>(logEntries.subList(startIndex, size));
    }

    public List<String> getAllEntries() {
        return new ArrayList<>(logEntries);
    }

    public void clear() {
        logEntries.clear();
    }

    public String getFormattedLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== BATTLE LOG ===\n");
        List<String> recent = getRecentEntries();
        for (String entry : recent) {
            sb.append(entry).append("\n");
        }
        return sb.toString();
    }

    // Observer interface
    public interface BattleLogObserver {
        void onLogUpdate(String message);
    }
}
