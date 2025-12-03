package com.elemental;

import com.elemental.service.BattleService;
import com.elemental.service.CharacterService;
import com.elemental.service.SaveLoadService;
import com.elemental.ui.fx.MainMenuScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyCombination;

public class MainFX extends Application {

    public static CharacterService characterService = new CharacterService();
    public static BattleService battleService = new BattleService();
    public static SaveLoadService saveLoadService = new SaveLoadService(characterService);

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Elemental Battle Arena - RPG");

        // 1. Inisialisasi Scene HANYA SEKALI di sini
        MainMenuScene mainMenu = new MainMenuScene();
        // Ukuran 800x600 hanya fallback, nanti akan ditimpa oleh fullscreen
        Scene scene = new Scene(mainMenu.getLayout(), 800, 600);

        // Load CSS
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setScene(scene);

        // 2. Aktifkan Fullscreen
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint(""); // Hapus pesan "Press ESC"
        // primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Opsional: Cegah ESC

        primaryStage.show();
    }

    // --- HELPER UNTUK KEMBALI KE MENU (PENTING) ---
    public static void showMainMenu() {
        // Kita hanya mengganti Root (isinya), bukan Scene (jendelanya)
        // Ini kuncinya agar Fullscreen tidak lepas/reset
        primaryStage.getScene().setRoot(new MainMenuScene().getLayout());
    }

    public static void main(String[] args) {
        launch(args);
    }
}