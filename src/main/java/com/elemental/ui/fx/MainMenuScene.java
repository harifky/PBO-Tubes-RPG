package com.elemental.ui.fx;

import com.elemental.MainFX;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainMenuScene {
    private VBox layout; // Simpan layout sebagai variabel class

    public MainMenuScene() {
        layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("root");

        Label title = new Label("ELEMENTAL BATTLE ARENA");
        title.getStyleClass().add("game-title");

        // Tombol-tombol Menu
        Button btnCharMgmt = createMenuButton("Character Management");
        btnCharMgmt.setOnAction(e -> MainFX.primaryStage.getScene().setRoot(new CharacterScene().getLayout()));

        Button btnBattle = createMenuButton("Start Battle");
        btnBattle.setOnAction(e -> MainFX.primaryStage.getScene().setRoot(new BattleSetupScene().getLayout()));

        Button btnInventory = createMenuButton("Inventory");
        btnInventory.setOnAction(e -> MainFX.primaryStage.getScene().setRoot(new InventoryScene().getLayout()));

        Button btnSave = createMenuButton("Save Game");
        btnSave.setOnAction(e -> MainFX.primaryStage.getScene().setRoot(new SaveLoadScene(true).getLayout()));

        Button btnLoad = createMenuButton("Load Game");
        btnLoad.setOnAction(e -> MainFX.primaryStage.getScene().setRoot(new SaveLoadScene(false).getLayout()));

        Button btnSettings = createMenuButton("Settings");
        btnSettings.setOnAction(e -> MainFX.primaryStage.getScene().setRoot(new SettingsScene().getLayout()));

        Button btnExit = createMenuButton("Exit Game");
        btnExit.setOnAction(e -> MainFX.primaryStage.close());

        layout.getChildren().addAll(title, btnCharMgmt, btnBattle, btnInventory, btnSave, btnLoad, btnSettings, btnExit);
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-medieval");
        btn.setPrefWidth(250);
        return btn;
    }

    // Method ini mengembalikan Layout, bukan Scene
    public VBox getLayout() {
        return layout;
    }
}