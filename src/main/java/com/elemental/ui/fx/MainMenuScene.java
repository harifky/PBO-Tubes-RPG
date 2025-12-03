package com.elemental.ui.fx;

import com.elemental.MainFX;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainMenuScene {
    private Scene scene;

    public MainMenuScene() {
        VBox layout = new VBox(15); // Jarak antar tombol 15px
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("root");

        Label title = new Label("ELEMENTAL BATTLE ARENA");
        title.getStyleClass().add("game-title");

        // 1. Character Management
        Button btnCharMgmt = createMenuButton("Character Management");
        btnCharMgmt.setOnAction(e -> {
            MainFX.primaryStage.getScene().setRoot(new CharacterScene().getLayout());
        });

        // 2. Start Battle
        Button btnBattle = createMenuButton("Start Battle");
        btnBattle.setOnAction(e -> {
            MainFX.primaryStage.getScene().setRoot(new BattleSetupScene().getLayout());
        });

        // 3. Inventory (BARU)
        Button btnInventory = createMenuButton("Inventory");
        btnInventory.setOnAction(e -> {
            MainFX.primaryStage.getScene().setRoot(new InventoryScene().getLayout());
        });

        // 4. Save Game
        Button btnSave = createMenuButton("Save Game");
        btnSave.setOnAction(e -> {
            MainFX.primaryStage.getScene().setRoot(new SaveLoadScene(true).getLayout());
        });

        // 5. Load Game
        Button btnLoad = createMenuButton("Load Game");
        btnLoad.setOnAction(e -> {
            MainFX.primaryStage.getScene().setRoot(new SaveLoadScene(false).getLayout());
        });

        // 6. Settings
        Button btnSettings = createMenuButton("Settings");
        btnSettings.setOnAction(e -> {
            MainFX.primaryStage.getScene().setRoot(new SettingsScene().getLayout());
        });

        // 0. Exit
        Button btnExit = createMenuButton("Exit Game");
        btnExit.setOnAction(e -> MainFX.primaryStage.close());

        layout.getChildren().addAll(title, btnCharMgmt, btnBattle, btnInventory, btnSave, btnLoad, btnSettings, btnExit);

        this.scene = new Scene(layout, 800, 600);
        this.scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-medieval");
        btn.setPrefWidth(250);
        return btn;
    }

    public Scene getScene() {
        return scene;
    }
}