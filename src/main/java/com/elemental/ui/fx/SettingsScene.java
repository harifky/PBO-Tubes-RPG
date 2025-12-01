package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.model.AIDifficulty;
import com.elemental.model.GameSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SettingsScene {
    private VBox layout;

    public SettingsScene() {
        layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("root"); // Background gelap
        layout.setPadding(new Insets(20));

        // --- TITLE ---
        Label title = new Label("Game Settings");
        title.getStyleClass().add("game-title");

        // --- PANEL UTAMA ---
        VBox settingsPanel = new VBox(15);
        settingsPanel.getStyleClass().add("panel-background");
        settingsPanel.setMaxWidth(500);
        settingsPanel.setPadding(new Insets(30));
        settingsPanel.setAlignment(Pos.CENTER);

        // 1. AI DIFFICULTY
        Label lblDiff = new Label("AI Difficulty");
        lblDiff.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        HBox diffBox = new HBox(10);
        diffBox.setAlignment(Pos.CENTER);

        Button btnEasy = createToggleBtn("EASY", AIDifficulty.EASY);
        Button btnMed = createToggleBtn("MEDIUM", AIDifficulty.MEDIUM);
        Button btnHard = createToggleBtn("HARD", AIDifficulty.HARD);

        // Update visual state tombol saat diklik
        updateButtonStyles(btnEasy, btnMed, btnHard);

        btnEasy.setOnAction(e -> {
            GameSettings.getInstance().setAIDifficulty(AIDifficulty.EASY);
            updateButtonStyles(btnEasy, btnMed, btnHard);
        });
        btnMed.setOnAction(e -> {
            GameSettings.getInstance().setAIDifficulty(AIDifficulty.MEDIUM);
            updateButtonStyles(btnEasy, btnMed, btnHard);
        });
        btnHard.setOnAction(e -> {
            GameSettings.getInstance().setAIDifficulty(AIDifficulty.HARD);
            updateButtonStyles(btnEasy, btnMed, btnHard);
        });

        diffBox.getChildren().addAll(btnEasy, btnMed, btnHard);

        // 2. OTHER SETTINGS
        CheckBox cbLog = new CheckBox("Show Detailed Battle Log");
        cbLog.setSelected(GameSettings.getInstance().isShowDetailedLog());
        cbLog.setStyle("-fx-font-size: 14px;");
        cbLog.setOnAction(e -> GameSettings.getInstance().setShowDetailedLog(cbLog.isSelected()));

        CheckBox cbAuto = new CheckBox("Auto Progress Battle");
        cbAuto.setSelected(GameSettings.getInstance().isAutoProgress());
        cbAuto.setStyle("-fx-font-size: 14px;");
        cbAuto.setOnAction(e -> GameSettings.getInstance().setAutoProgress(cbAuto.isSelected()));

        // --- BACK BUTTON ---
        Button btnBack = new Button("Save & Back");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setPrefWidth(200);
        btnBack.setOnAction(e -> MainFX.primaryStage.setScene(new MainMenuScene().getScene()));

        // Susun Layout
        settingsPanel.getChildren().addAll(
                lblDiff, diffBox,
                new Separator(),
                cbLog, cbAuto,
                new Separator(),
                btnBack
        );

        layout.getChildren().addAll(title, settingsPanel);
    }

    private Button createToggleBtn(String text, AIDifficulty difficulty) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-medieval");
        btn.setPrefWidth(100);
        return btn;
    }

    // Ubah warna tombol berdasarkan mana yang aktif
    private void updateButtonStyles(Button bEasy, Button bMed, Button bHard) {
        AIDifficulty current = GameSettings.getInstance().getAIDifficulty();

        // Reset style
        String inactive = "";
        String active = "-fx-background-color: #ffd700; -fx-text-fill: #3e1903;"; // Emas

        bEasy.setStyle(current == AIDifficulty.EASY ? active : inactive);
        bMed.setStyle(current == AIDifficulty.MEDIUM ? active : inactive);
        bHard.setStyle(current == AIDifficulty.HARD ? active : inactive);
    }

    public VBox getLayout() { return layout; }
}