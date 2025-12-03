package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.model.SaveData;
import com.elemental.model.SaveMetadata;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SaveLoadScene {
    private StackPane rootStack;
    private BorderPane layout;
    private VBox slotsContainer;
    private boolean isSaveMode;

    public SaveLoadScene(boolean isSaveMode) {
        this.isSaveMode = isSaveMode;
        rootStack = new StackPane();
        rootStack.getStyleClass().add("root");

        layout = new BorderPane();
        layout.setPadding(new Insets(20));

        Label title = new Label(isSaveMode ? "SAVE GAME" : "LOAD GAME");
        title.getStyleClass().add("game-title");

        VBox topBox = new VBox(title);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(0,0,20,0));
        layout.setTop(topBox);

        // Container untuk slots
        slotsContainer = new VBox(20);
        slotsContainer.setAlignment(Pos.CENTER);
        refreshSlots();

        layout.setCenter(slotsContainer);

        Button btnBack = new Button("Back");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setPrefWidth(150);
        btnBack.setOnAction(e -> MainFX.showMainMenu());

        VBox bottomBox = new VBox(btnBack);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));
        layout.setBottom(bottomBox);

        rootStack.getChildren().add(layout);
    }

    private void refreshSlots() {
        slotsContainer.getChildren().clear();
        List<SaveMetadata> allMeta = MainFX.saveLoadService.getAllSaveMetadata();
        for (int i = 0; i < 3; i++) {
            int slotNum = i + 1;
            SaveMetadata meta = (i < allMeta.size()) ? allMeta.get(i) : null;
            slotsContainer.getChildren().add(createSlotCard(slotNum, meta));
        }
    }

    private HBox createSlotCard(int slotNum, SaveMetadata meta) {
        HBox card = new HBox(20);
        // Menggunakan style dark panel untuk setiap kartu slot
        card.getStyleClass().add("dark-fantasy-panel");
        card.setPadding(new Insets(20)); // Padding dalam kartu
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(600);
        // Reset min-width bawaan class css agar tidak terlalu lebar untuk slot
        card.setStyle("-fx-min-width: 500px; -fx-padding: 20px;");

        VBox infoBox = new VBox(5);
        Label lblSlotName = new Label("Slot " + slotNum);
        lblSlotName.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #ffd700;");

        Label lblDetail = new Label();
        boolean isEmpty = meta == null || meta.getSavedAt() == null || meta.getSlotName().contains("Empty");

        if (isEmpty) {
            lblDetail.setText("-- Empty Slot --");
            lblDetail.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
        } else {
            String dateStr = meta.getSavedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            lblDetail.setText(String.format("Lv.%d %s | Battles: %d\n%s",
                    meta.getHighestLevel(), meta.getHighestLevelCharName(), meta.getTotalBattles(), dateStr));
            lblDetail.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        }
        infoBox.getChildren().addAll(lblSlotName, lblDetail);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        if (isSaveMode) {
            Button btnSave = new Button("SAVE");
            btnSave.getStyleClass().add("button-medieval");
            btnSave.setPrefWidth(80);
            btnSave.setOnAction(e -> handleSave(slotNum));
            btnBox.getChildren().add(btnSave);
        } else {
            Button btnLoad = new Button("LOAD");
            btnLoad.getStyleClass().add("button-medieval");
            btnLoad.setPrefWidth(80);
            btnLoad.setDisable(isEmpty);
            btnLoad.setOnAction(e -> handleLoad(slotNum));
            btnBox.getChildren().add(btnLoad);
        }

        if (!isEmpty) {
            Button btnDelete = new Button("X");
            btnDelete.getStyleClass().add("button-medieval");
            btnDelete.setStyle("-fx-background-color: #8b0000; -fx-text-fill: white;");
            btnDelete.setOnAction(e -> handleDelete(slotNum));
            btnBox.getChildren().add(btnDelete);
        }

        card.getChildren().addAll(infoBox, btnBox);
        return card;
    }

    private void handleSave(int slotNum) {
        if (MainFX.saveLoadService.saveSlotExists(slotNum)) {
            MedievalPopup.showConfirm(rootStack, "OVERWRITE SAVE?",
                    "Slot " + slotNum + " already has data.\nOverwrite it?",
                    () -> performSave(slotNum));
        } else {
            performSave(slotNum);
        }
    }

    private void performSave(int slotNum) {
        try {
            MainFX.saveLoadService.saveGame(slotNum);
            refreshSlots();
            MedievalPopup.show(rootStack, "GAME SAVED", "Your progress has been saved to Slot " + slotNum + ".", MedievalPopup.Type.SUCCESS);
        } catch (Exception e) {
            MedievalPopup.show(rootStack, "SAVE FAILED", e.getMessage(), MedievalPopup.Type.ERROR);
        }
    }

    private void handleLoad(int slotNum) {
        try {
            SaveData data = MainFX.saveLoadService.loadGame(slotNum);
            MainFX.saveLoadService.applySaveData(data);

            MedievalPopup.show(rootStack, "GAME LOADED",
                    "Welcome back, Hero!\nGame loaded successfully.",
                    MedievalPopup.Type.SUCCESS,
                    () -> MainFX.showMainMenu()
            );

        } catch (Exception e) {
            MedievalPopup.show(rootStack, "LOAD FAILED", e.getMessage(), MedievalPopup.Type.ERROR);
        }
    }

    private void handleDelete(int slotNum) {
        MedievalPopup.showConfirm(rootStack, "DELETE SAVE?",
                "Are you sure you want to delete Slot " + slotNum + "?\nThis action cannot be undone.",
                () -> {
                    try {
                        MainFX.saveLoadService.deleteSave(slotNum);
                        refreshSlots();
                        MedievalPopup.show(rootStack, "DELETED", "Save file deleted.", MedievalPopup.Type.INFO);
                    } catch (Exception e) {
                        MedievalPopup.show(rootStack, "ERROR", e.getMessage(), MedievalPopup.Type.ERROR);
                    }
                });
    }

    public StackPane getLayout() { return rootStack; }
}