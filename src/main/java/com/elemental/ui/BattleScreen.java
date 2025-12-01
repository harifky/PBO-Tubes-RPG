package com.elemental.ui;

import com.elemental.factory.EnemyFactory;
import com.elemental.model.ActionType;
import com.elemental.model.AIDifficulty;
import com.elemental.model.Battle;
import com.elemental.model.BattleAction;
import com.elemental.model.BattleStatus;
import com.elemental.model.GameSettings;
import com.elemental.model.Skill;
import com.elemental.model.Status;
import com.elemental.service.BattleService;
import com.elemental.service.CharacterService;
import com.elemental.strategy.AIStrategy;
import com.elemental.strategy.AIStrategyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Battle Screen UI - handles battle display and user interaction
 */
public class BattleScreen {
    private BattleService battleService;
    private CharacterService characterService;
    private Scanner scanner;
    private Battle currentBattle;

    public BattleScreen(BattleService battleService, CharacterService characterService, Scanner scanner) {
        this.battleService = battleService;
        this.characterService = characterService;
        this.scanner = scanner;
    }

    /**
     * Start a battle with selected character
     */
    public void startBattle() {
        // Check if player has characters
        if (characterService.isRosterEmpty()) {
            System.out.println("\n❌ You need to create a character first!");
            return;
        }

        // Select character for battle
        com.elemental.model.Character playerCharacter = selectCharacterForBattle();
        if (playerCharacter == null) {
            return;
        }

        // Generate enemy team
        List<com.elemental.model.Character> enemyTeam = generateEnemyTeam(playerCharacter.getLevel());

        // Create player team
        List<com.elemental.model.Character> playerTeam = new ArrayList<>();
        playerTeam.add(playerCharacter);

        // Initialize battle
        currentBattle = battleService.startBattle(playerTeam, enemyTeam);

        // Display battle start
        displayBattleScreen();

        // Battle loop
        runBattleLoop();
    }

    /**
     * Select character for battle
     */
    private com.elemental.model.Character selectCharacterForBattle() {
        System.out.println("\n=== SELECT CHARACTER FOR BATTLE ===");
        System.out.println(characterService.displayAllCharacters());
        System.out.print("Enter character number (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return null;

            com.elemental.model.Character selected = characterService.getCharacter(choice - 1);
            if (selected != null && selected.isAlive()) {
                return selected;
            } else {
                System.out.println("❌ Invalid character or character is dead!");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
            return null;
        }
    }

    /**
     * Generate enemy team based on player level
     */
    private List<com.elemental.model.Character> generateEnemyTeam(int playerLevel) {
        List<com.elemental.model.Character> enemies = new ArrayList<>();

        // Generate 1-2 enemies
        int enemyCount = (int) (Math.random() * 2) + 1;

        for (int i = 0; i < enemyCount; i++) {
            int enemyLevel = Math.max(1, playerLevel + (int) (Math.random() * 3) - 1);
            enemies.add(EnemyFactory.createEnemy(enemyLevel));
        }

        return enemies;
    }

    /**
     * Main battle loop
     */
    private void runBattleLoop() {
        while (battleService.isBattleActive()) {
            // Get next turn
            com.elemental.model.Character currentTurn = currentBattle.getNextTurn();

            if (currentTurn == null) {
                break;
            }

            // Display current state
            displayBattleState();

            // Handle turn
            if (currentBattle.isPlayerTurn(currentTurn)) {
                handlePlayerTurn(currentTurn);
            } else {
                handleEnemyTurn(currentTurn);
            }

            // Check battle end
            BattleStatus status = currentBattle.checkBattleEnd();
            if (status != BattleStatus.ONGOING) {
                displayBattleResult(status);
                break;
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }

        battleService.endBattle();
    }

    /**
     * Display battle screen
     */
    private void displayBattleScreen() {
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║              BATTLE ARENA                      ║");
        System.out.println("╚════════════════════════════════════════════════╝");

        displayTeams();
    }

    /**
     * Display current battle state
     */
    private void displayBattleState() {
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║              BATTLE - Turn " + currentBattle.getTurnNumber() + "                    ║");
        System.out.println("╚═══════════════════════════════���════════════════╝");

        displayTeams();
        displayBattleLog();
    }

    /**
     * Display teams
     */
    private void displayTeams() {
        System.out.println("\n【 YOUR TEAM 】");
        for (com.elemental.model.Character c : currentBattle.getPlayerTeam()) {
            displayCharacterStatus(c);
        }

        System.out.println("\n【 ENEMY TEAM 】");
        for (com.elemental.model.Character e : currentBattle.getEnemyTeam()) {
            displayCharacterStatus(e);
        }
        System.out.println();
    }

    /**
     * Display character status
     */
    private void displayCharacterStatus(com.elemental.model.Character character) {
        String hpBar = createHPBar(character);
        String mpBar = createMPBar(character);
        String statusText = character.getStatus() != Status.NORMAL ?
            " [" + character.getStatus() + "]" : "";

        System.out.println(String.format("  %s (Lv.%d %s)%s",
            character.getName(), character.getLevel(), character.getElement(), statusText));
        System.out.println("    HP: " + hpBar + " " + character.getCurrentHP() + "/" + character.getMaxHP());
        System.out.println("    MP: " + mpBar + " " + character.getCurrentMP() + "/" + character.getMaxMP());
    }

    /**
     * Create HP bar
     */
    private String createHPBar(com.elemental.model.Character character) {
        int barLength = 20;
        int filledLength = (int) ((double) character.getCurrentHP() / character.getMaxHP() * barLength);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append("]");
        return bar.toString();
    }

    /**
     * Create MP bar
     */
    private String createMPBar(com.elemental.model.Character character) {
        int barLength = 20;
        int filledLength = (int) ((double) character.getCurrentMP() / character.getMaxMP() * barLength);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append("▓");
            } else {
                bar.append("░");
            }
        }
        bar.append("]");
        return bar.toString();
    }

    /**
     * Display battle log
     */
    private void displayBattleLog() {
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("【 BATTLE LOG 】");
        List<String> recentLogs = currentBattle.getBattleLog().getRecentEntries();
        for (String log : recentLogs) {
            System.out.println("  " + log);
        }
        System.out.println("─────────────────────────────────────────────────");
    }

    /**
     * Handle player turn
     */
    private void handlePlayerTurn(com.elemental.model.Character player) {
        System.out.println("\n>>> " + player.getName() + "'s turn! <<<");

        BattleAction action = selectPlayerAction(player);
        if (action != null) {
            currentBattle.executeAction(action);
        }
    }

    /**
     * Select player action
     */
    private BattleAction selectPlayerAction(com.elemental.model.Character player) {
        System.out.println("\nWhat will " + player.getName() + " do?");
        System.out.println("1. Attack");
        System.out.println("2. Use Skill");
        System.out.println("3. Defend");
        System.out.println("4. View Stats");
        System.out.print("Choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                return createAttackAction(player);
            case "2":
                return createSkillAction(player);
            case "3":
                return createDefendAction(player);
            case "4":
                displayDetailedStats(player);
                return selectPlayerAction(player);
            default:
                System.out.println("Invalid choice!");
                return selectPlayerAction(player);
        }
    }

    /**
     * Create attack action
     */
    private BattleAction createAttackAction(com.elemental.model.Character player) {
        com.elemental.model.Character target = selectTarget(currentBattle.getEnemyTeam());
        if (target == null) return null;

        BattleAction action = new BattleAction(player, ActionType.ATTACK);
        action.setTarget(target);
        return action;
    }

    /**
     * Create skill action
     */
    private BattleAction createSkillAction(com.elemental.model.Character player) {
        List<Skill> skills = player.getSkills();

        System.out.println("\n=== SELECT SKILL ===");
        for (int i = 0; i < skills.size(); i++) {
            Skill skill = skills.get(i);
            String canUse = player.canUseSkill(skill) ? "✓" : "✗";
            System.out.println(String.format("%d. %s %s", i + 1, canUse, skill.toString()));
        }
        System.out.print("Choice (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return selectPlayerAction(player);

            Skill selectedSkill = skills.get(choice - 1);
            if (!player.canUseSkill(selectedSkill)) {
                System.out.println("❌ Cannot use this skill!");
                return selectPlayerAction(player);
            }

            com.elemental.model.Character target = selectTarget(currentBattle.getEnemyTeam());
            if (target == null) return selectPlayerAction(player);

            BattleAction action = new BattleAction(player, ActionType.SKILL);
            action.setSkill(selectedSkill);
            action.setTarget(target);
            return action;
        } catch (Exception e) {
            System.out.println("❌ Invalid input!");
            return selectPlayerAction(player);
        }
    }

    /**
     * Create defend action
     */
    private BattleAction createDefendAction(com.elemental.model.Character player) {
        return new BattleAction(player, ActionType.DEFEND);
    }

    /**
     * Select target from enemy team
     */
    private com.elemental.model.Character selectTarget(List<com.elemental.model.Character> enemies) {
        List<com.elemental.model.Character> aliveEnemies = new ArrayList<>();
        for (com.elemental.model.Character e : enemies) {
            if (e.isAlive()) {
                aliveEnemies.add(e);
            }
        }

        if (aliveEnemies.isEmpty()) return null;
        if (aliveEnemies.size() == 1) return aliveEnemies.get(0);

        System.out.println("\n=== SELECT TARGET ===");
        for (int i = 0; i < aliveEnemies.size(); i++) {
            System.out.println((i + 1) + ". " + aliveEnemies.get(i).toString() +
                " (HP: " + aliveEnemies.get(i).getCurrentHP() + "/" + aliveEnemies.get(i).getMaxHP() + ")");
        }
        System.out.print("Choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return aliveEnemies.get(choice - 1);
        } catch (Exception e) {
            System.out.println("❌ Invalid target!");
            return selectTarget(enemies);
        }
    }

    /**
     * Handle enemy turn using Phase 3 AI System (Hybrid)
     */
    private void handleEnemyTurn(com.elemental.model.Character enemy) {
        System.out.println("\n>>> " + enemy.getName() + "'s turn! <<<");

        // PHASE 3: AI Hybrid System
        // Get AI difficulty using hybrid approach
        AIDifficulty difficulty = getAIDifficultyHybrid(enemy);

        // Create AI strategy based on difficulty
        AIStrategy ai = AIStrategyFactory.create(difficulty);

        // Display AI difficulty (if detailed log enabled)
        if (GameSettings.getInstance().isShowDetailedLog()) {
            System.out.println("  [AI Level: " + difficulty + "]");
        }

        // AI makes decision
        BattleAction action = ai.decideAction(
            enemy,                           // Current enemy character
            currentBattle.getEnemyTeam(),    // Enemy team (allies for AI)
            currentBattle.getPlayerTeam()    // Player team (enemies for AI)
        );

        // Display AI decision (if detailed log enabled)
        if (GameSettings.getInstance().isShowDetailedLog()) {
            displayAIDecision(enemy, action);
        }

        // Execute the AI's chosen action
        currentBattle.executeAction(action);
    }

    /**
     * HYBRID AI Difficulty Selection
     * Combines player preference with contextual adjustments
     */
    private AIDifficulty getAIDifficultyHybrid(com.elemental.model.Character enemy) {
        // 1. Get base difficulty from player settings
        AIDifficulty baseDifficulty = GameSettings.getInstance().getAIDifficulty();

        // 2. Check for special cases
        // TODO: Add boss flag check when implemented
        // if (enemy.isBoss()) {
        //     return AIDifficulty.HARD;  // Boss always uses Hard AI
        // }

        // 3. Get enemy level for context
        int enemyLevel = enemy.getLevel();

        // 4. Apply hybrid logic based on base difficulty
        switch (baseDifficulty) {
            case EASY:
                // Respect player choice - stay Easy
                return AIDifficulty.EASY;

            case MEDIUM:
                // Auto-scale within Medium range
                if (enemyLevel >= 10) {
                    // High level enemy → upgrade to Hard
                    return AIDifficulty.HARD;
                } else if (enemyLevel <= 2) {
                    // Low level enemy → downgrade to Easy
                    return AIDifficulty.EASY;
                }
                return AIDifficulty.MEDIUM;

            case HARD:
                // Hard mode requested - always Hard
                // But could downgrade for very low level enemies (optional)
                if (enemyLevel <= 1) {
                    return AIDifficulty.EASY;  // Tutorial enemies
                }
                return AIDifficulty.HARD;

            default:
                return AIDifficulty.MEDIUM;
        }
    }

    /**
     * Display AI decision for transparency (optional, for detailed log)
     */
    private void displayAIDecision(com.elemental.model.Character enemy, BattleAction action) {
        switch (action.getActionType()) {
            case ATTACK:
                System.out.println("  💥 " + enemy.getName() + " attacks " +
                                 action.getTarget().getName() + "!");
                break;
            case SKILL:
                System.out.println("  ✨ " + enemy.getName() + " uses " +
                                 action.getSkill().getName() + " on " +
                                 action.getTarget().getName() + "!");
                break;
            case DEFEND:
                System.out.println("  🛡️  " + enemy.getName() + " takes a defensive stance!");
                break;
        }
    }

    /**
     * Get random alive player
     */
    private com.elemental.model.Character getRandomAlivePlayer() {
        List<com.elemental.model.Character> alivePlayers = new ArrayList<>();
        for (com.elemental.model.Character p : currentBattle.getPlayerTeam()) {
            if (p.isAlive()) {
                alivePlayers.add(p);
            }
        }

        if (alivePlayers.isEmpty()) return null;
        return alivePlayers.get((int) (Math.random() * alivePlayers.size()));
    }

    /**
     * Display detailed stats
     */
    private void displayDetailedStats(com.elemental.model.Character character) {
        System.out.println("\n" + character.getStatsPreview());
        System.out.println("\nSkills:");
        for (Skill skill : character.getSkills()) {
            System.out.println("  - " + skill.toString());
        }
    }

    /**
     * Display battle result
     */
    private void displayBattleResult(BattleStatus status) {
        System.out.println("\n╔══════��═════════════════════════════════════════╗");
        if (status == BattleStatus.VICTORY) {
            System.out.println("║              ⚔️  VICTORY! ⚔️                    ║");
        } else {
            System.out.println("║              💀  DEFEAT  💀                    ║");
        }
        System.out.println("╚════════════════════════════════════════════════╝");

        // Display final log
        displayBattleLog();

        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }

    /**
     * Clear screen (simple implementation)
     */
    private void clearScreen() {
        // Print empty lines to simulate screen clear
        for (int i = 0; i < 2; i++) {
            System.out.println();
        }
    }
}
