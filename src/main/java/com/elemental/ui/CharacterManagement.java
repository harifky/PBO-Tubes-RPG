package com.elemental.ui;

import com.elemental.model.Character;
import com.elemental.model.CharacterClass;
import com.elemental.model.Element;
import com.elemental.model.GameSettings;
import com.elemental.model.Inventory; // Import Inventory
import com.elemental.service.CharacterService;
import com.elemental.service.SaveLoadService;
import com.elemental.factory.CharacterFactory;

import java.util.Scanner;

public class CharacterManagement {
    private CharacterService characterService;
    private SaveLoadService saveLoadService;
    private Scanner scanner;

    public CharacterManagement(CharacterService characterService, SaveLoadService saveLoadService, Scanner scanner) {
        this.characterService = characterService;
        this.saveLoadService = saveLoadService;
        this.scanner = scanner;
    }

    public void show() {
        boolean running = true;
        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createCharacter();
                    break;
                case "2":
                    viewAllCharacters();
                    break;
                case "3":
                    viewCharacterDetails();
                    break;
                case "4":
                    selectCharacter();
                    break;
                case "5":
                    deleteCharacter();
                    break;
                case "6":
                    testLevelUp();
                    break;
                case "7":
                    reviveCharacter();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     CHARACTER MANAGEMENT SYSTEM        ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("1. Create New Character");
        System.out.println("2. View All Characters");
        System.out.println("3. View Character Details");
        System.out.println("4. Select Character");
        System.out.println("5. Delete Character");
        System.out.println("6. Test Level Up System");
        System.out.println("7. Revive Dead Character");
        System.out.println("0. Back to Main Menu");
        System.out.println("─────────────────────────────────────────");

        if (characterService.getSelectedCharacter() != null) {
            System.out.println("Selected: " + characterService.getSelectedCharacter().toString());
        }

        System.out.print("Enter choice: ");
    }

    /**
     * FR-CHAR-001: Character Creation with validation
     */
    private void createCharacter() {
        System.out.println("\n=== CREATE NEW CHARACTER ===");

        // Get character name with validation
        String name = null;
        while (name == null) {
            System.out.print("Enter character name (max 20 chars, alphanumeric): ");
            String input = scanner.nextLine().trim();

            String error = CharacterFactory.getNameValidationError(input);
            if (error != null) {
                System.out.println("❌ " + error);
            } else {
                name = input;
            }
        }

        // Select character class
        CharacterClass characterClass = selectCharacterClass();
        if (characterClass == null) return;

        // Select element
        Element element = selectElement();
        if (element == null) return;

        // Create character
        try {
            Character newChar = characterService.createCharacter(name, characterClass, element);
            System.out.println("\n✓ Character created successfully!");
            System.out.println(newChar.getStatsPreview());

            // Auto-save if enabled
            if (saveLoadService != null && GameSettings.getInstance().isAutoSave()) {
                saveLoadService.autoSave();
                System.out.println("\n💾 Game auto-saved!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error creating character: " + e.getMessage());
        }
    }

    private CharacterClass selectCharacterClass() {
        System.out.println("\nSelect Character Class:");
        System.out.println("1. MAGE    (HP:80  MP:100 ATK:35 DEF:10 SPD:25)");
        System.out.println("2. WARRIOR (HP:120 MP:50  ATK:25 DEF:20 SPD:15)");
        System.out.println("3. RANGER  (HP:100 MP:70  ATK:30 DEF:15 SPD:30)");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": return CharacterClass.MAGE;
            case "2": return CharacterClass.WARRIOR;
            case "3": return CharacterClass.RANGER;
            default:
                System.out.println("Invalid choice!");
                return null;
        }
    }

    private Element selectElement() {
        System.out.println("\nSelect Element:");
        System.out.println("1. FIRE");
        System.out.println("2. WATER");
        System.out.println("3. EARTH");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": return Element.FIRE;
            case "2": return Element.WATER;
            case "3": return Element.EARTH;
            default:
                System.out.println("Invalid choice!");
                return null;
        }
    }

    /**
     * FR-CHAR-005: View all characters
     */
    private void viewAllCharacters() {
        System.out.println("\n" + characterService.displayAllCharacters());

        if (!characterService.isRosterEmpty()) {
            System.out.println("Total characters: " + characterService.getRosterSize());
        }
    }

    /**
     * FR-CHAR-005: View character details with stats preview
     */
    private void viewCharacterDetails() {
        if (characterService.isRosterEmpty()) {
            System.out.println("\n❌ No characters in roster!");
            return;
        }

        System.out.println("\n" + characterService.displayAllCharacters());
        System.out.print("Enter character number to view details (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;

            String stats = characterService.displayCharacterStats(choice - 1);
            System.out.println("\n" + stats);

            // Display skills
            Character character = characterService.getCharacter(choice - 1);
            if (character != null) {
                System.out.println("\n=== SKILLS ===");
                for (int i = 0; i < character.getSkills().size(); i++) {
                    System.out.println((i + 1) + ". " + character.getSkills().get(i).toString());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
        }
    }

    /**
     * FR-CHAR-005: Character Selection
     */
    private void selectCharacter() {
        if (characterService.isRosterEmpty()) {
            System.out.println("\n❌ No characters in roster!");
            return;
        }

        System.out.println("\n" + characterService.displayAllCharacters());
        System.out.print("Enter character number to select (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;

            Character selected = characterService.selectCharacter(choice - 1);
            System.out.println("\n✓ Selected: " + selected.toString());
            System.out.println(selected.getStatsPreview());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void deleteCharacter() {
        if (characterService.isRosterEmpty()) {
            System.out.println("\n❌ No characters in roster!");
            return;
        }

        System.out.println("\n" + characterService.displayAllCharacters());
        System.out.print("Enter character number to delete (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;

            Character toDelete = characterService.getCharacter(choice - 1);
            if (toDelete != null) {
                System.out.print("Are you sure you want to delete " + toDelete.getName() + "? (y/n): ");
                String confirm = scanner.nextLine().trim().toLowerCase();

                if (confirm.equals("y") || confirm.equals("yes")) {
                    characterService.removeCharacter(choice - 1);
                    System.out.println("✓ Character deleted successfully!");
                } else {
                    System.out.println("Deletion cancelled.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
        }
    }

    /**
     * FR-CHAR-004: Test Level Up System
     */
    private void testLevelUp() {
        if (characterService.isRosterEmpty()) {
            System.out.println("\n❌ No characters in roster!");
            return;
        }

        System.out.println("\n" + characterService.displayAllCharacters());
        System.out.print("Enter character number to level up (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) return;

            Character character = characterService.getCharacter(choice - 1);
            if (character == null) {
                System.out.println("❌ Invalid character!");
                return;
            }

            System.out.print("Enter experience points to gain: ");
            int exp = Integer.parseInt(scanner.nextLine().trim());

            int oldLevel = character.getLevel();
            character.gainExperience(exp);
            int newLevel = character.getLevel();

            System.out.println("\n✓ Gained " + exp + " experience!");
            if (newLevel > oldLevel) {
                System.out.println("🎉 LEVEL UP! " + oldLevel + " → " + newLevel);
            }
            System.out.println("\n" + character.getStatsPreview());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
        }
    }

    /**
     * Revive a dead character using Revive item
     */
    private void reviveCharacter() {
        if (characterService.isRosterEmpty()) {
            System.out.println("\n❌ No characters in roster!");
            return;
        }

        // Find dead characters
        java.util.List<Character> allCharacters = characterService.getAllCharacters();
        java.util.List<Character> deadCharacters = new java.util.ArrayList<>();

        for (int i = 0; i < allCharacters.size(); i++) {
            Character c = allCharacters.get(i);
            if (!c.isAlive()) {
                deadCharacters.add(c);
            }
        }

        if (deadCharacters.isEmpty()) {
            System.out.println("\n✓ All characters are alive! No one needs reviving.");
            return;
        }

        // Display dead characters
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         REVIVE DEAD CHARACTER          ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Dead Characters:");
        for (int i = 0; i < deadCharacters.size(); i++) {
            Character c = deadCharacters.get(i);
            System.out.println((i + 1) + ". " + c.getName() +
                    " (Lv." + c.getLevel() + " " + c.getCharacterClass() +
                    " " + c.getElement() + ") [DEAD]");
        }

        System.out.print("\nEnter character number to revive (0 to cancel): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) {
                System.out.println("Revive cancelled.");
                return;
            }

            if (choice < 1 || choice > deadCharacters.size()) {
                System.out.println("❌ Invalid choice!");
                return;
            }

            Character toRevive = deadCharacters.get(choice - 1);

            // GANTI: Cek inventory GLOBAL (Singleton), bukan inventory karakter
            if (!Inventory.getInstance().hasItem("Revive")) {
                System.out.println("\n❌ You don't have a Revive item in global inventory!");
                System.out.println("💡 Tip: Revive items can be obtained during battles.");
                return;
            }

            // Confirm revive
            System.out.print("\nUse 1 Revive item to restore " + toRevive.getName() + " with 30% HP? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (confirm.equals("y") || confirm.equals("yes")) {
                // Apply revive effect (30% HP restore)
                int reviveHP = (int) (toRevive.getMaxHP() * 0.30);
                toRevive.setCurrentHP(reviveHP);
                toRevive.setStatus(com.elemental.model.Status.NORMAL);

                // GANTI: Remove item from GLOBAL inventory
                Inventory.getInstance().removeItem("Revive", 1);

                System.out.println("\n════════════════════════════════════════");
                System.out.println("✨ REVIVAL SUCCESSFUL! ✨");
                System.out.println("════════════════════════════════════════");
                System.out.println("💚 " + toRevive.getName() + " has been revived!");
                System.out.println("HP Restored: " + reviveHP + "/" + toRevive.getMaxHP() + " (30%)");
                System.out.println("Status: " + toRevive.getStatus());
                System.out.println("\n" + toRevive.getStatsPreview());

                // Auto-save
                if (saveLoadService != null && GameSettings.getInstance().isAutoSave()) {
                    saveLoadService.autoSave();
                    System.out.println("💾 Game auto-saved!");
                }
            } else {
                System.out.println("Revive cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
        }
    }
}