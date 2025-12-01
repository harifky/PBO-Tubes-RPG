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
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2b2b2b;"); // Dark BG

        Label title = new Label("ELEMENTAL BATTLE ARENA");
        title.getStyleClass().add("game-title");

        Button btnCharMgmt = new Button("Character Management");
        btnCharMgmt.getStyleClass().add("button-medieval");
        btnCharMgmt.setPrefWidth(200);
        btnCharMgmt.setOnAction(e -> {
            MainFX.primaryStage.getScene().setRoot(new CharacterScene().getLayout());
        });

        Button btnBattle = new Button("Start Battle");
        btnBattle.getStyleClass().add("button-medieval");
        btnBattle.setPrefWidth(200);
        btnBattle.setOnAction(e -> {
            MainFX.primaryStage.getScene().setRoot(new BattleSetupScene().getLayout());
        });

        Button btnExit = new Button("Exit Game");
        btnExit.getStyleClass().add("button-medieval");
        btnExit.setPrefWidth(200);
        btnExit.setOnAction(e -> MainFX.primaryStage.close());

        layout.getChildren().addAll(title, btnCharMgmt, btnBattle, btnExit);

        this.scene = new Scene(layout, 800, 600);
        this.scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }
}