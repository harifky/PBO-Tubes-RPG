package com.elemental.ui;

import com.elemental.model.AIDifficulty;
import com.elemental.model.GameSettings;
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
        this.saveLoadService = new SaveLoadService(characterService);

        this.characterManagement = new CharacterManagement(characterService, scanner);
        this.battleScreen = new BattleScreen(battleService, characterService, saveLoadService, scanner);
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
                    showSettings();
                    break;
                case "4":
                    saveGame();
                    break;
                case "5":
                    loadGame();
                    break;
                case "6":
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
        System.out.println("3. Game Settings");
        System.out.println("4. Save Game");
        System.out.println("5. Load Game");
        System.out.println("6. About");
        System.out.println("0. Exit Game");
        System.out.println("───────────────────────────────────────────────");
        System.out.print("Enter your choice: ");
    }

    private void startBattle() {
        battleScreen.startBattle();
    }

    /**
     * FR-SAVE-001: Save game to slot
     */
    private void saveGame() {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║              SAVE GAME                       ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        displaySaveSlots();

        System.out.print("\nSelect slot (1-3, 0=cancel): ");
        try {
            int slot = Integer.parseInt(scanner.nextLine().trim());
            if (slot == 0)
                return;

            if (slot < 1 || slot > 3) {
                System.out.println("❌ Invalid slot!");
                return;
            }

            if (saveLoadService.saveSlotExists(slot)) {
                System.out.print("⚠️  Overwrite slot " + slot + "? (y/n): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    System.out.println("Cancelled.");
                    return;
                }
            }

            saveLoadService.saveGame(slot);
            System.out.println("\nPress Enter...");
            scanner.nextLine();

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
        } catch (Exception e) {
            System.out.println("❌ Save failed: " + e.getMessage());
        }
    }

    /**
     * FR-SAVE-002: Load game from slot
     */
    private void loadGame() {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║              LOAD GAME                       ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        displaySaveSlots();

        System.out.print("\nSelect slot (1-3, 0=cancel, D=delete): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("d")) {
            handleDeleteSave();
            return;
        }

        try {
            int slot = Integer.parseInt(input);
            if (slot == 0)
                return;

            if (slot < 1 || slot > 3) {
                System.out.println("❌ Invalid slot!");
                return;
            }

            if (!saveLoadService.saveSlotExists(slot)) {
                System.out.println("❌ Slot " + slot + " is empty!");
                return;
            }

            var saveData = saveLoadService.loadGame(slot);
            saveLoadService.applySaveData(saveData);

            System.out.println("\n✅ Game loaded!");
            System.out.println("Press Enter...");
            scanner.nextLine();

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
        } catch (Exception e) {
            System.out.println("❌ Load failed: " + e.getMessage());
        }
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

    /**
     * Display and manage game settings
     */
    private void showSettings() {
        boolean inSettings = true;

        while (inSettings) {
            // Display current settings
            System.out.println("\n" + GameSettings.getInstance().displaySettings());

            // Display menu
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║            SETTINGS MENU                     ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.println("1. Change AI Difficulty");
            System.out.println("2. Toggle Detailed Battle Log");
            System.out.println("3. Toggle Auto Progress");
            System.out.println("4. Reset to Defaults");
            System.out.println("0. Back to Main Menu");
            System.out.println("───────────────────────────────────────────────");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    changeAIDifficulty();
                    break;
                case "2":
                    toggleDetailedLog();
                    break;
                case "3":
                    toggleAutoProgress();
                    break;
                case "4":
                    resetSettings();
                    break;
                case "0":
                    inSettings = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    /**
     * Change AI Difficulty setting
     */
    private void changeAIDifficulty() {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║          SELECT AI DIFFICULTY                ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. EASY   - Random AI, simple decisions");
        System.out.println("   • 60% basic attack (random target)");
        System.out.println("   • 30% random skill usage");
        System.out.println("   • 10% defend");
        System.out.println("   • No strategic thinking");
        System.out.println();
        System.out.println("2. MEDIUM - Strategic AI, basic tactics");
        System.out.println("   • HP/MP management");
        System.out.println("   • Targets lowest HP enemies");
        System.out.println("   • Considers element advantage");
        System.out.println("   • Defensive when HP < 30%");
        System.out.println();
        System.out.println("3. HARD   - Advanced AI, adaptive strategies");
        System.out.println("   • Threat-based targeting");
        System.out.println("   • Resource optimization");
        System.out.println("   • Counter-strategy (adapts to player)");
        System.out.println("   • Early game buffs & finishing blows");
        System.out.println();
        System.out.println("0. Cancel");
        System.out.println("───────────────────────────────────────────────");
        System.out.println("Current: " + GameSettings.getInstance().getAIDifficulty());
        System.out.print("\nEnter your choice: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                GameSettings.getInstance().setAIDifficulty(AIDifficulty.EASY);
                System.out.println("\n✅ AI Difficulty set to EASY");
                System.out.println("💡 Good for learning game mechanics!");
                break;
            case "2":
                GameSettings.getInstance().setAIDifficulty(AIDifficulty.MEDIUM);
                System.out.println("\n✅ AI Difficulty set to MEDIUM");
                System.out.println("💡 Balanced challenge with strategic AI!");
                break;
            case "3":
                GameSettings.getInstance().setAIDifficulty(AIDifficulty.HARD);
                System.out.println("\n✅ AI Difficulty set to HARD");
                System.out.println("💡 Prepare for tactical combat!");
                break;
            case "0":
                System.out.println("Cancelled.");
                break;
            default:
                System.out.println("❌ Invalid choice!");
        }

        if (!choice.equals("0")) {
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    /**
     * Toggle detailed battle log
     */
    private void toggleDetailedLog() {
        GameSettings settings = GameSettings.getInstance();
        settings.setShowDetailedLog(!settings.isShowDetailedLog());

        String status = settings.isShowDetailedLog() ? "ON" : "OFF";
        System.out.println("\n✅ Detailed Battle Log: " + status);

        if (settings.isShowDetailedLog()) {
            System.out.println("💡 You will see detailed AI decisions and calculations");
        } else {
            System.out.println("💡 Only basic battle messages will be shown");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Toggle auto progress
     */
    private void toggleAutoProgress() {
        GameSettings settings = GameSettings.getInstance();
        settings.setAutoProgress(!settings.isAutoProgress());

        String status = settings.isAutoProgress() ? "ON" : "OFF";
        System.out.println("\n✅ Auto Progress: " + status);

        if (settings.isAutoProgress()) {
            System.out.println("💡 Battle will automatically continue after each action");
        } else {
            System.out.println("💡 You need to press Enter to continue after each action");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Reset all settings to defaults
     */
    private void resetSettings() {
        System.out.print("\n⚠️  Are you sure you want to reset all settings to defaults? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            GameSettings.getInstance().resetToDefaults();
            System.out.println("\n✅ All settings reset to defaults!");
            System.out.println("  • AI Difficulty: MEDIUM");
            System.out.println("  • Detailed Log: ON");
            System.out.println("  • Auto Progress: OFF");
        } else {
            System.out.println("Cancelled.");
        }

        System.out.println("\nPress Enter to continue...");
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

    /**
     * Display all save slots with metadata
     */
    private void displaySaveSlots() {
        var slots = saveLoadService.getAllSaveMetadata();
        System.out.println();
        for (int i = 0; i < slots.size(); i++) {
            System.out.println(slots.get(i).getDisplayString());
            if (i < slots.size() - 1)
                System.out.println();
        }
    }

    /**
     * FR-SAVE-003: Delete save with confirmation
     */
    private void handleDeleteSave() {
        System.out.print("\nSlot to delete (1-3, 0=cancel): ");
        try {
            int slot = Integer.parseInt(scanner.nextLine().trim());
            if (slot == 0)
                return;

            if (slot < 1 || slot > 3) {
                System.out.println("❌ Invalid slot!");
                return;
            }

            if (!saveLoadService.saveSlotExists(slot)) {
                System.out.println("❌ Slot empty!");
                return;
            }

            System.out.print("⚠️  Delete slot " + slot + "? Cannot undo! (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                saveLoadService.deleteSave(slot);
                System.out.println("\nPress Enter...");
                scanner.nextLine();
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
