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

        // Tombol-tombol Menu
        Button btnCharMgmt = createMenuButton("Character Management");
        btnCharMgmt.setOnAction(e -> {
            MainFX.primaryStage.getScene().setRoot(new CharacterScene().getLayout());
        });

        Button btnBattle = createMenuButton("Start Battle");
        btnBattle.setOnAction(e -> {
            MainFX.primaryStage.getScene().setRoot(new BattleSetupScene().getLayout());
        });

        // BARU: Tombol Save
        Button btnSave = createMenuButton("Save Game");
        btnSave.setOnAction(e -> {
            // Buka SaveLoadScene dengan mode Save (true)
            MainFX.primaryStage.getScene().setRoot(new SaveLoadScene(true).getLayout());
        });

        // BARU: Tombol Load
        Button btnLoad = createMenuButton("Load Game");
        btnLoad.setOnAction(e -> {
            // Buka SaveLoadScene dengan mode Load (false)
            MainFX.primaryStage.getScene().setRoot(new SaveLoadScene(false).getLayout());
        });

        // BARU: Tombol Settings
        Button btnSettings = createMenuButton("Settings");
        btnSettings.setOnAction(e -> {
            MainFX.primaryStage.getScene().setRoot(new SettingsScene().getLayout());
        });

        Button btnExit = createMenuButton("Exit Game");
        btnExit.setOnAction(e -> MainFX.primaryStage.close());

        layout.getChildren().addAll(title, btnCharMgmt, btnBattle, btnSave, btnLoad, btnSettings, btnExit);

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