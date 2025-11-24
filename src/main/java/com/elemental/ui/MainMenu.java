package com.elemental.ui;

import com.elemental.service.CharacterService;
import com.elemental.service.BattleService;
import com.elemental.service.SaveLoadService;

import java.util.Scanner;

public class MainMenu {
    private final Scanner scanner;
    private final CharacterService characterService;
    private final BattleService battleService;
    private final SaveLoadService saveLoadService;

    private final CharacterManagement characterManagement;
    private final BattleScreen battleScreen;

    public MainMenu() {
        this.scanner = new Scanner(System.in);
        this.characterService = new CharacterService();
        this.battleService = new BattleService();
        this.saveLoadService = new SaveLoadService();

        this.characterManagement = new CharacterManagement(characterService, scanner);
        this.battleScreen = new BattleScreen(battleService, characterService, scanner);
    }

    public void show() {
        displayWelcome();

        boolean running = true;
        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    characterManagement.show();
                    break;
                case "2":
                    startBattle();
                    break;
                case "3":
                    saveGame();
                    break;
                case "4":
                    loadGame();
                    break;
                case "5":
                    displayAbout();
                    break;
                case "0":
                    running = false;
                    System.out.println("\nThank you for playing Elemental Battle Arena!");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }

        scanner.close();
    }

    private void displayWelcome() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║                                              ║");
        System.out.println("║     ELEMENTAL BATTLE ARENA                   ║");
        System.out.println("║     Turn-Based RPG Battle System             ║");
        System.out.println("║                                              ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
    }

    private void displayMenu() {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║              MAIN MENU                       ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println("1. Character Management");
        System.out.println("2. Start Battle");
        System.out.println("3. Save Game");
        System.out.println("4. Load Game");
        System.out.println("5. About");
        System.out.println("0. Exit Game");
        System.out.println("───────────────────────────────────────────────");
        System.out.print("Enter your choice: ");
    }

    private void startBattle() {
        battleScreen.startBattle();
    }

    private void saveGame() {
        System.out.println("\n[Save System - Coming in Phase 3]");
        System.out.println("This feature will be available soon!");
    }

    private void loadGame() {
        System.out.println("\n[Load System - Coming in Phase 3]");
        System.out.println("This feature will be available soon!");
    }

    private void displayAbout() {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║              ABOUT THE GAME                  ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println("Elemental Battle Arena v1.0");
        System.out.println("A turn-based RPG battle system");
        System.out.println();
        System.out.println("Features:");
        System.out.println("✓ Character Creation (Mage, Warrior, Ranger)");
        System.out.println("✓ Elemental System (Fire, Water, Earth)");
        System.out.println("✓ Skill-based Combat");
        System.out.println("✓ Level Up System");
        System.out.println("✓ Status Effects");
        System.out.println("✓ Turn-based Battle System");
        System.out.println("✓ Damage Calculation with Element Modifiers");
        System.out.println();
        System.out.println("Design Patterns Used:");
        System.out.println("• Factory Pattern - Character creation");
        System.out.println("• Strategy Pattern - AI behavior");
        System.out.println("• Observer Pattern - Battle events");
        System.out.println("• Decorator Pattern - Status effects");
        System.out.println();
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    public CharacterService getCharacterService() {
        return characterService;
    }

    public BattleService getBattleService() {
        return battleService;
    }

    public SaveLoadService getSaveLoadService() {
        return saveLoadService;
    }
}
