package com.elemental.model;

import com.elemental.observer.BattleObserver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

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
    private Random random;

    // Observer Pattern - List of observers
    private List<BattleObserver> observers;

    public Battle() {
        this.battleLog = new BattleLog();
        this.battleStatus = BattleStatus.ONGOING;
        this.turnNumber = 0;
        this.random = new Random();
        this.observers = new ArrayList<>();
    }

    // ==================== OBSERVER PATTERN METHODS ====================

    /**
     * Menambahkan observer untuk memantau event battle
     * @param observer Observer yang akan ditambahkan
     */
    public void addObserver(BattleObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Menghapus observer dari daftar
     * @param observer Observer yang akan dihapus
     */
    public void removeObserver(BattleObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notify semua observers ketika battle dimulai
     */
    private void notifyBattleStart() {
        for (BattleObserver observer : observers) {
            observer.onBattleStart();
        }
    }

    /**
     * Notify semua observers ketika giliran berubah
     */
    private void notifyTurnChange(Character character, int turnNumber) {
        for (BattleObserver observer : observers) {
            observer.onTurnChange(character, turnNumber);
        }
    }

    /**
     * Notify semua observers ketika ada serangan
     */
    private void notifyAttack(Character attacker, Character target, int damage, boolean isCritical) {
        for (BattleObserver observer : observers) {
            observer.onAttack(attacker, target, damage, isCritical);
        }
    }

    /**
     * Notify semua observers ketika skill digunakan
     */
    private void notifySkillUsed(Character user, Skill skill, Character target) {
        for (BattleObserver observer : observers) {
            observer.onSkillUsed(user, skill, target);
        }
    }

    /**
     * Notify semua observers ketika item digunakan
     */
    private void notifyItemUsed(Character user, Item item, Character target) {
        for (BattleObserver observer : observers) {
            observer.onItemUsed(user, item, target);
        }
    }

    /**
     * Notify semua observers ketika karakter defend
     */
    private void notifyDefend(Character character) {
        for (BattleObserver observer : observers) {
            observer.onDefend(character);
        }
    }

    /**
     * Notify semua observers ketika HP berubah
     */
    private void notifyHPChange(Character character, int oldHP, int newHP) {
        for (BattleObserver observer : observers) {
            observer.onHPChange(character, oldHP, newHP);
        }
    }

    /**
     * Notify semua observers ketika karakter dikalahkan
     */
    private void notifyCharacterDefeated(Character character) {
        for (BattleObserver observer : observers) {
            observer.onCharacterDefeated(character);
        }
    }

    /**
     * Notify semua observers ketika battle berakhir
     */
    private void notifyBattleEnd(BattleStatus status) {
        for (BattleObserver observer : observers) {
            observer.onBattleEnd(status);
        }
    }

    /**
     * Notify semua observers dengan pesan log
     */
    private void notifyLogMessage(String message) {
        for (BattleObserver observer : observers) {
            observer.onLogMessage(message);
        }
    }

    // ==================== END OBSERVER PATTERN METHODS ====================

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

        // Notify observers bahwa battle dimulai
        notifyBattleStart();
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

            // Notify observers bahwa giliran berubah
            notifyTurnChange(next, turnNumber);

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

        int oldHP = defender.getCurrentHP();
        defender.takeDamage(damage);
        int newHP = defender.getCurrentHP();

        battleLog.logDamage(attacker, defender, damage, isCritical);

        // Notify observers
        notifyAttack(attacker, defender, damage, isCritical);
        notifyHPChange(defender, oldHP, newHP);

        String elementAdvantage = DamageCalculator.getElementAdvantage(
                attacker.getElement(), defender.getElement()
        );
        if (!elementAdvantage.isEmpty()) {
            battleLog.logElementAdvantage(elementAdvantage);
        }

        if (!defender.isAlive()) {
            battleLog.logDeath(defender);
            notifyCharacterDefeated(defender);
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

        // Notify observers bahwa skill digunakan
        notifySkillUsed(user, skill, target);

        // Execute skill based on type
        switch (skill.getSkillType()) {
            case DAMAGE:
                int damage = DamageCalculator.calculateDamage(user, target, skill);
                boolean isCritical = DamageCalculator.isCritical();
                int oldHP = target.getCurrentHP();
                target.takeDamage(damage);
                int newHP = target.getCurrentHP();
                battleLog.logDamage(user, target, damage, isCritical);

                // Notify observers
                notifyAttack(user, target, damage, isCritical);
                notifyHPChange(target, oldHP, newHP);

                // Element Advantage for Skills
                Element attackElement = skill.getElement() != null ? skill.getElement() : user.getElement();
                String elementAdvantage = DamageCalculator.getElementAdvantage(
                        attackElement, target.getElement()
                );
                if (!elementAdvantage.isEmpty()) {
                    battleLog.logElementAdvantage(elementAdvantage);
                }

                // Apply secondary effects based on skill name
                applySkillSecondaryEffects(user, target, skill);

                if (!target.isAlive()) {
                    battleLog.logDeath(target);
                    notifyCharacterDefeated(target);
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

        // Notify observers bahwa karakter defend
        notifyDefend(actor);
    }

    /**
     * Execute item usage
     * UPDATE: Menggunakan Global Inventory
     */
    private void executeItem(Character user, Character target, Item item) {
        if (item == null) {
            battleLog.log("No item selected!");
            return;
        }

        // Gunakan Singleton Inventory
        Inventory globalInv = Inventory.getInstance();

        // Cek stok di inventory global
        if (!globalInv.hasItem(item.getName())) {
            battleLog.log(item.getName() + " not available!");
            return;
        }

        // Cek validitas target
        if (!item.canUse(target)) {
            String reason = target.isAlive() ? "invalid target" : "cannot use on dead ally";
            if (item.getTargetType() == ItemTarget.DEAD_ALLY && target.isAlive()) {
                reason = "can only be used on dead allies";
            }
            battleLog.log("Cannot use " + item.getName() + " - " + reason + "!");
            return;
        }

        // Gunakan item (kurangi stok global)
        if (globalInv.useItem(item.getName())) {
            battleLog.logItemUse(user, item, target);
            item.applyEffect(target);

            // Notify observers bahwa item digunakan
            notifyItemUsed(user, item, target);

            // Log spesifik efek item
            switch (item.getEffect()) {
                case RESTORE_HP:
                    battleLog.log(target.getName() + " recovered HP!");
                    break;
                case RESTORE_MP:
                    battleLog.log(target.getName() + " recovered MP!");
                    break;
                case RESTORE_BOTH:
                    battleLog.log(target.getName() + " recovered HP and MP!");
                    break;
                case BOOST_ATTACK:
                    battleLog.log(target.getName() + "'s Attack increased!");
                    break;
                case BOOST_DEFENSE:
                    battleLog.log(target.getName() + "'s Defense increased!");
                    break;
                case CURE_STATUS:
                    battleLog.log(target.getName() + " was cured of negative status!");
                    break;
                case REVIVE:
                    if (target.isAlive()) {
                        battleLog.log(target.getName() + " was revived!");
                    }
                    break;
            }
        } else {
            battleLog.log("Failed to use " + item.getName() + "!");
        }
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

        // Process Item Buffs
        character.processItemBuffs();
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
            // Notify observers bahwa battle berakhir dengan Victory
            notifyBattleEnd(battleStatus);
        } else if (allPlayersDead) {
            battleStatus = BattleStatus.DEFEAT;
            battleLog.logDefeat();
            // Notify observers bahwa battle berakhir dengan Defeat
            notifyBattleEnd(battleStatus);
        } else {
            battleStatus = BattleStatus.ONGOING;
        }

        return battleStatus;
    }

    /**
     * FR-BATTLE-005: Calculate and give rewards
     * Update: Menambahkan Item Drops ke Global Inventory
     */
    private void giveRewards() {
        int totalExp = 0;
        boolean defeatedBoss = false;

        for (Character enemy : enemyTeam) {
            int enemyExp = 50 * enemy.getLevel();

            // Boss memberikan 3x EXP
            if (enemy.isBoss()) {
                enemyExp *= 3;
                defeatedBoss = true;
            }

            totalExp += enemyExp;
        }

        battleLog.log("=== REWARDS ===");

        if (defeatedBoss) {
            battleLog.log("🎉 BOSS DEFEATED! BONUS REWARDS! 🎉");
        }

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

        // --- ITEM DROPS SYSTEM ---
        battleLog.log("\n=== ITEM DROPS ===");
        for (Character enemy : enemyTeam) {
            dropItems(enemy);
        }
    }

    /**
     * Drop items from enemies to Global Inventory
     */
    private void dropItems(Character enemy) {
        Inventory globalInv = Inventory.getInstance();
        double dropChance = random.nextDouble();

        if (dropChance < 0.6) {
            // 60% chance common items
            String item = random.nextBoolean() ? "Health Potion" : "Mana Potion";
            if (globalInv.addItem(item, 1)) {
                battleLog.log(enemy.getName() + " dropped " + item + "!");
            }
        } else if (dropChance < 0.9) {
            // 30% chance medium items
            String[] mediumItems = {"Elixir", "Attack Boost", "Defense Boost", "Antidote"};
            String item = mediumItems[random.nextInt(mediumItems.length)];
            if (globalInv.addItem(item, 1)) {
                battleLog.log(enemy.getName() + " dropped " + item + "!");
            }
        } else {
            // 10% chance rare items
            if (globalInv.addItem("Revive", 1)) {
                battleLog.log(enemy.getName() + " dropped Revive! (RARE)");
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