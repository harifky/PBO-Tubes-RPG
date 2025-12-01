package com.elemental;

import com.elemental.service.BattleService;
import com.elemental.service.CharacterService;
import com.elemental.ui.fx.MainMenuScene;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainFX extends Application {

    // Global Services agar bisa diakses antar scene
    public static CharacterService characterService = new CharacterService();
    public static BattleService battleService = new BattleService();
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Elemental Battle Arena - RPG");

        // Load Main Menu
        MainMenuScene mainMenu = new MainMenuScene();
        primaryStage.setScene(mainMenu.getScene());

        // Apply CSS global
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}