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
                // Generate Enemy & Start
                List<Character> enemies = List.of(EnemyFactory.createEnemy(selected.getLevel()));
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
}