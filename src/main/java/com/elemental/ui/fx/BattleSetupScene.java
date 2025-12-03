package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.factory.EnemyFactory;
import com.elemental.model.Character;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import java.util.Collections;
import java.util.List;

public class BattleSetupScene {
    private StackPane rootStack;
    private VBox content;

    public BattleSetupScene() {
        rootStack = new StackPane();
        rootStack.getStyleClass().add("root");

        content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        Label title = new Label("Prepare for Battle");
        title.getStyleClass().add("game-title");

        ComboBox<Character> charSelect = new ComboBox<>();
        List<Character> allChars = MainFX.characterService.getAllCharacters();
        allChars.sort((c1, c2) -> {
            boolean alive1 = c1.isAlive();
            boolean alive2 = c2.isAlive();
            if (alive1 != alive2) return alive1 ? -1 : 1;
            return Integer.compare(c2.getLevel(), c1.getLevel());
        });
        charSelect.getItems().addAll(allChars);
        charSelect.setPromptText("Select Your Hero");

        charSelect.setConverter(new StringConverter<Character>() {
            @Override
            public String toString(Character c) {
                if (c == null) return null;
                String statusSuffix = c.isAlive() ? "" : " (DEAD 💀)";
                return c.toString() + statusSuffix;
            }
            @Override
            public Character fromString(String string) { return null; }
        });

        if (!charSelect.getItems().isEmpty()) {
            charSelect.getSelectionModel().selectFirst();
        }

        Button btnStart = new Button("FIGHT!");
        btnStart.getStyleClass().add("button-medieval");
        btnStart.setOnAction(e -> {
            Character selected = charSelect.getValue();
            if (selected == null) {
                MedievalPopup.show(rootStack, "NO HERO SELECTED", "Please select a character first!", MedievalPopup.Type.WARNING);
                return;
            }
            if (!selected.isAlive()) {
                MedievalPopup.show(rootStack, "HERO IS DEAD", selected.getName() + " cannot fight!", MedievalPopup.Type.ERROR);
                return;
            }
            checkAndStartBattle(selected);
        });

        // --- BACK BUTTON (UPDATED) ---
        Button btnBack = new Button("Retreat");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setOnAction(e -> MainFX.showMainMenu());

        content.getChildren().addAll(title, charSelect, btnStart, btnBack);
        rootStack.getChildren().add(content);
    }

    private void checkAndStartBattle(Character player) {
        int level = player.getLevel();
        if (level >= 5 && level % 5 == 0) {
            String[] bossNames = {"Dragon", "Phoenix", "Golem", "Wraith", "Demon Lord"};
            String bossName = bossNames[(level / 5 - 1) % bossNames.length];
            MedievalPopup.showConfirm(rootStack, "⚠️ BOSS WARNING ⚠️",
                    "A powerful " + bossName + " (Lv." + level + ") has appeared!\nAre you prepared?",
                    () -> startBattleLogic(player));
        } else {
            startBattleLogic(player);
        }
    }

    private void startBattleLogic(Character player) {
        List<Character> enemies = generateEnemyTeam(player.getLevel());
        MainFX.battleService.startBattle(Collections.singletonList(player), enemies);
        MainFX.primaryStage.getScene().setRoot(new BattleScene().getLayout());
    }

    public StackPane getLayout() { return rootStack; }

    private List<Character> generateEnemyTeam(int playerLevel) {
        if (playerLevel >= 5 && playerLevel % 5 == 0) {
            String[] bossNames = {"Dragon", "Phoenix", "Golem", "Wraith", "Demon Lord"};
            String bossName = bossNames[(playerLevel / 5 - 1) % bossNames.length];
            return List.of(EnemyFactory.createBoss(bossName, playerLevel));
        } else {
            return List.of(EnemyFactory.createEnemy(playerLevel));
        }
    }
}