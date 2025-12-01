package com.elemental;

import com.elemental.service.BattleService;
import com.elemental.service.CharacterService;
import com.elemental.service.SaveLoadService;
import com.elemental.ui.fx.MainMenuScene;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainFX extends Application {

    public static CharacterService characterService = new CharacterService();
    public static BattleService battleService = new BattleService();
    // BARU: Tambahkan Service SaveLoad global
    public static SaveLoadService saveLoadService = new SaveLoadService(characterService);

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Elemental Battle Arena - RPG");

        MainMenuScene mainMenu = new MainMenuScene();
        primaryStage.setScene(mainMenu.getScene());

        // Load CSS
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}