package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.factory.EnemyFactory;
import com.elemental.model.Character;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Collections;
import java.util.List;

public class BattleSetupScene {
    private VBox layout;

    public BattleSetupScene() {
        layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("root");

        Label title = new Label("Prepare for Battle");
        title.getStyleClass().add("game-title");

        ComboBox<Character> charSelect = new ComboBox<>();
        charSelect.getItems().addAll(MainFX.characterService.getAllCharacters());

        Button btnStart = new Button("FIGHT!");
        btnStart.getStyleClass().add("button-medieval");
        btnStart.setOnAction(e -> {
            Character selected = charSelect.getValue();
            if (selected != null && selected.isAlive()) {
                // Generate Enemy (with Boss system)
                List<Character> enemies = generateEnemyTeam(selected.getLevel());
                MainFX.battleService.startBattle(Collections.singletonList(selected), enemies);

                // Pindah ke Scene Pertarungan
                MainFX.primaryStage.getScene().setRoot(new BattleScene().getLayout());
            } else {
                new Alert(Alert.AlertType.WARNING, "Select an alive character!").show();
            }
        });

        Button btnBack = new Button("Retreat");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setOnAction(e -> MainFX.primaryStage.setScene(new MainMenuScene().getScene()));

        layout.getChildren().addAll(title, charSelect, btnStart, btnBack);
    }

    public VBox getLayout() { return layout; }

    /**
     * Generate enemy team with boss system and smart difficulty
     * Boss appears at level 5, 10, 15, 20, 25, ...
     */
    private List<Character> generateEnemyTeam(int playerLevel) {
        // Get player element for smart generation
        com.elemental.model.Element playerElement = null;
        for (Character c : MainFX.characterService.getAllCharacters()) {
            if (c.getLevel() == playerLevel) {
                playerElement = c.getElement();
                break;
            }
        }

        // Check if player level is 5 or multiple of 5
        if (playerLevel >= 5 && playerLevel % 5 == 0) {
            // BOSS BATTLE!
            String[] bossNames = {"Dragon", "Phoenix", "Golem", "Wraith", "Demon Lord"};
            int bossIndex = ((playerLevel / 5) - 1) % bossNames.length;
            String bossName = bossNames[bossIndex];

            // Show boss warning
            Alert bossAlert = new Alert(Alert.AlertType.INFORMATION);
            bossAlert.setTitle("⚠️ BOSS BATTLE WARNING!");
            bossAlert.setHeaderText("🔥 " + bossName + " (Level " + playerLevel + ") has appeared!");
            bossAlert.setContentText("💀 Prepare for an epic battle!");
            bossAlert.showAndWait();

            // Create boss
            Character boss = EnemyFactory.createBoss(bossName, playerLevel);
            return List.of(boss);
        } else {
            // Normal enemy - smart generation based on AI difficulty for level 1-10
            if (playerLevel <= 10) {
                com.elemental.model.AIDifficulty difficulty = com.elemental.model.GameSettings.getInstance().getAIDifficulty();

                switch (difficulty) {
                    case EASY:
                        // Easy: Weak enemy (disadvantage element)
                        return List.of(createWeakEnemy(playerLevel, playerElement));

                    case MEDIUM:
                        // Medium: Balanced enemy (neutral element)
                        return List.of(createBalancedEnemy(playerLevel, playerElement));

                    case HARD:
                        // Hard: Strong enemy (advantage element)
                        return List.of(createStrongEnemy(playerLevel, playerElement));

                    default:
                        return List.of(EnemyFactory.createEnemy(playerLevel));
                }
            } else {
                // Level 11+: Always random enemy
                return List.of(EnemyFactory.createEnemy(playerLevel));
            }
        }
    }

    /**
     * Create weak enemy (element disadvantage) for beginner friendly gameplay
     */
    private Character createWeakEnemy(int level, com.elemental.model.Element playerElement) {
        if (playerElement == null) {
            return EnemyFactory.createEnemy(level);
        }

        // Get weak element (player has advantage)
        com.elemental.model.Element weakElement;
        switch (playerElement) {
            case FIRE:
                weakElement = com.elemental.model.Element.EARTH; // Fire beats Earth
                break;
            case WATER:
                weakElement = com.elemental.model.Element.FIRE; // Water beats Fire
                break;
            case EARTH:
                weakElement = com.elemental.model.Element.WATER; // Earth beats Water
                break;
            default:
                weakElement = com.elemental.model.Element.FIRE;
        }

        // Create enemy with weak element
        String[] weakEnemyNames = {"Goblin", "Skeleton", "Bandit"};
        String name = weakEnemyNames[(int)(Math.random() * weakEnemyNames.length)];
        com.elemental.model.CharacterClass randomClass = com.elemental.model.CharacterClass.values()[(int)(Math.random() * 3)];

        return EnemyFactory.createEnemy(name, randomClass, weakElement, level);
    }

    /**
     * Create balanced enemy (neutral element) for fair gameplay
     */
    private Character createBalancedEnemy(int level, com.elemental.model.Element playerElement) {
        if (playerElement == null) {
            return EnemyFactory.createEnemy(level);
        }

        // Use same element as player (neutral matchup: 1.0x damage both ways)
        String[] balancedEnemyNames = {"Orc", "Troll", "Warrior"};
        String name = balancedEnemyNames[(int)(Math.random() * balancedEnemyNames.length)];
        com.elemental.model.CharacterClass randomClass = com.elemental.model.CharacterClass.values()[(int)(Math.random() * 3)];

        return EnemyFactory.createEnemy(name, randomClass, playerElement, level);
    }

    /**
     * Create strong enemy (element advantage) for challenging gameplay
     */
    private Character createStrongEnemy(int level, com.elemental.model.Element playerElement) {
        if (playerElement == null) {
            return EnemyFactory.createEnemy(level);
        }

        // Get strong element (enemy has advantage)
        com.elemental.model.Element strongElement;
        switch (playerElement) {
            case FIRE:
                strongElement = com.elemental.model.Element.WATER; // Water beats Fire
                break;
            case WATER:
                strongElement = com.elemental.model.Element.EARTH; // Earth beats Water
                break;
            case EARTH:
                strongElement = com.elemental.model.Element.FIRE; // Fire beats Earth
                break;
            default:
                strongElement = com.elemental.model.Element.WATER;
        }

        // Create enemy with strong element
        String[] strongEnemyNames = {"Elite Guard", "Warlock", "Champion"};
        String name = strongEnemyNames[(int)(Math.random() * strongEnemyNames.length)];
        com.elemental.model.CharacterClass randomClass = com.elemental.model.CharacterClass.values()[(int)(Math.random() * 3)];

        return EnemyFactory.createEnemy(name, randomClass, strongElement, level);
    }
}