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
        layout.getStyleClass().add("root");
        layout.setPadding(new Insets(20));

        Label title = new Label("Game Settings");
        title.getStyleClass().add("game-title");

        // Gunakan style dark-fantasy-panel
        VBox settingsPanel = new VBox(20);
        settingsPanel.getStyleClass().add("dark-fantasy-panel");
        settingsPanel.setMaxWidth(500);
        settingsPanel.setPadding(new Insets(40));
        settingsPanel.setAlignment(Pos.CENTER);

        // AI DIFFICULTY
        Label lblDiff = new Label("AI Difficulty");
        lblDiff.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #ffd700;");

        HBox diffBox = new HBox(15);
        diffBox.setAlignment(Pos.CENTER);

        Button btnEasy = createToggleBtn("EASY", AIDifficulty.EASY);
        Button btnMed = createToggleBtn("MEDIUM", AIDifficulty.MEDIUM);
        Button btnHard = createToggleBtn("HARD", AIDifficulty.HARD);

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

        // OTHER SETTINGS
        // Pastikan warna teks putih/terang agar terbaca di dark panel
        String cbStyle = "-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold;";

        CheckBox cbLog = new CheckBox("Show Detailed Battle Log");
        cbLog.setSelected(GameSettings.getInstance().isShowDetailedLog());
        cbLog.setStyle(cbStyle);
        cbLog.setOnAction(e -> GameSettings.getInstance().setShowDetailedLog(cbLog.isSelected()));

        CheckBox cbAuto = new CheckBox("Auto Progress Battle");
        cbAuto.setSelected(GameSettings.getInstance().isAutoProgress());
        cbAuto.setStyle(cbStyle);
        cbAuto.setOnAction(e -> GameSettings.getInstance().setAutoProgress(cbAuto.isSelected()));

        CheckBox cbAutoSave = new CheckBox("💾 Auto Save");
        cbAutoSave.setSelected(GameSettings.getInstance().isAutoSave());
        cbAutoSave.setStyle(cbStyle);
        cbAutoSave.setOnAction(e -> GameSettings.getInstance().setAutoSave(cbAutoSave.isSelected()));

        Button btnBack = new Button("Save & Back");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setPrefWidth(200);
        btnBack.setOnAction(e -> MainFX.showMainMenu());

        settingsPanel.getChildren().addAll(
                lblDiff, diffBox,
                new Separator(),
                cbLog, cbAuto, cbAutoSave,
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

    private void updateButtonStyles(Button bEasy, Button bMed, Button bHard) {
        AIDifficulty current = GameSettings.getInstance().getAIDifficulty();
        String inactive = "-fx-opacity: 0.6;";
        String active = "-fx-background-color: #ffd700; -fx-text-fill: #3e1903; -fx-opacity: 1.0; -fx-effect: dropshadow(gaussian, #ff4500, 10, 0.6, 0, 0);";

        bEasy.setStyle(current == AIDifficulty.EASY ? active : inactive);
        bMed.setStyle(current == AIDifficulty.MEDIUM ? active : inactive);
        bHard.setStyle(current == AIDifficulty.HARD ? active : inactive);
    }

    public VBox getLayout() { return layout; }
}