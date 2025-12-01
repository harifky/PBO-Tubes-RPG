package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.model.SaveData;
import com.elemental.model.SaveMetadata;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SaveLoadScene {
    private BorderPane layout;
    private VBox slotsContainer;
    private boolean isSaveMode; // true = Menu Save, false = Menu Load

    public SaveLoadScene(boolean isSaveMode) {
        this.isSaveMode = isSaveMode;

        layout = new BorderPane();
        layout.getStyleClass().add("root");
        layout.setPadding(new Insets(20));

        // --- HEADER ---
        Label title = new Label(isSaveMode ? "SAVE GAME" : "LOAD GAME");
        title.getStyleClass().add("game-title");

        VBox topBox = new VBox(title);
        topBox.setAlignment(Pos.CENTER);
        layout.setTop(topBox);

        // --- SLOTS AREA ---
        slotsContainer = new VBox(15);
        slotsContainer.setAlignment(Pos.CENTER);
        refreshSlots(); // Load data slot dari file

        layout.setCenter(slotsContainer);

        // --- FOOTER (BACK BUTTON) ---
        Button btnBack = new Button("Back");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setPrefWidth(150);
        btnBack.setOnAction(e -> MainFX.primaryStage.setScene(new MainMenuScene().getScene()));

        VBox bottomBox = new VBox(btnBack);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));
        layout.setBottom(bottomBox);
    }

    private void refreshSlots() {
        slotsContainer.getChildren().clear();
        // Ambil data metadata dari service
        List<SaveMetadata> allMeta = MainFX.saveLoadService.getAllSaveMetadata();

        // Loop membuat 3 Slot Card
        for (int i = 0; i < 3; i++) {
            int slotNum = i + 1;
            SaveMetadata meta = (i < allMeta.size()) ? allMeta.get(i) : null;
            slotsContainer.getChildren().add(createSlotCard(slotNum, meta));
        }
    }

    private HBox createSlotCard(int slotNum, SaveMetadata meta) {
        HBox card = new HBox(20);
        card.getStyleClass().add("panel-background");
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(600);

        // --- INFO SLOT (Kiri) ---
        VBox infoBox = new VBox(5);
        Label lblSlotName = new Label("Slot " + slotNum);
        lblSlotName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label lblDetail = new Label();
        // Cek apakah slot kosong atau corrupt
        boolean isEmpty = meta == null || meta.getSavedAt() == null || meta.getSlotName().contains("Empty");

        if (isEmpty) {
            lblDetail.setText("-- Empty Slot --");
            lblDetail.setStyle("-fx-text-fill: #777; -fx-font-style: italic;");
        } else {
            String dateStr = meta.getSavedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            lblDetail.setText(String.format("Lv.%d %s | Battles: %d\n%s",
                    meta.getHighestLevel(),
                    meta.getHighestLevelCharName(),
                    meta.getTotalBattles(),
                    dateStr
            ));
        }
        infoBox.getChildren().addAll(lblSlotName, lblDetail);

        // Spacer agar tombol terdorong ke kanan
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // --- TOMBOL AKSI (Kanan) ---
        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        if (isSaveMode) {
            Button btnSave = new Button("SAVE");
            btnSave.getStyleClass().add("button-medieval");
            btnSave.setOnAction(e -> handleSave(slotNum));
            btnBox.getChildren().add(btnSave);
        } else {
            // Load Mode
            Button btnLoad = new Button("LOAD");
            btnLoad.getStyleClass().add("button-medieval");
            btnLoad.setDisable(isEmpty); // Matikan tombol jika slot kosong
            btnLoad.setOnAction(e -> handleLoad(slotNum));
            btnBox.getChildren().add(btnLoad);
        }

        // Tombol Delete (Hanya jika ada isi)
        if (!isEmpty) {
            Button btnDelete = new Button("X");
            btnDelete.setStyle("-fx-background-color: #a00; -fx-text-fill: white; -fx-font-weight: bold;");
            btnDelete.setOnAction(e -> handleDelete(slotNum));
            btnBox.getChildren().add(btnDelete);
        }

        card.getChildren().addAll(infoBox, btnBox);
        return card;
    }

    // --- LOGIKA TOMBOL ---

    private void handleSave(int slotNum) {
        try {
            // Konfirmasi jika menimpa file
            if (MainFX.saveLoadService.saveSlotExists(slotNum)) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Overwrite Slot " + slotNum + "?");
                if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
            }

            MainFX.saveLoadService.saveGame(slotNum);
            refreshSlots();
            new Alert(Alert.AlertType.INFORMATION, "Game Saved Successfully!").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Save Failed: " + e.getMessage()).show();
        }
    }

    private void handleLoad(int slotNum) {
        try {
            SaveData data = MainFX.saveLoadService.loadGame(slotNum);
            MainFX.saveLoadService.applySaveData(data); // Terapkan data ke game

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Game Loaded!");
            alert.showAndWait();

            // Kembali ke menu utama (state karakter sudah berubah sesuai save file)
            MainFX.primaryStage.setScene(new MainMenuScene().getScene());

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Load Failed: " + e.getMessage()).show();
        }
    }

    private void handleDelete(int slotNum) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete Slot " + slotNum + "? Cannot be undone.");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                MainFX.saveLoadService.deleteSave(slotNum);
                refreshSlots();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Delete Failed: " + e.getMessage()).show();
            }
        }
    }

    public BorderPane getLayout() { return layout; }
}