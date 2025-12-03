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
        layout.setTop(topBox);

        slotsContainer = new VBox(15);
        slotsContainer.setAlignment(Pos.CENTER);
        refreshSlots();

        layout.setCenter(slotsContainer);

        // --- BACK BUTTON (UPDATED) ---
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
        card.getStyleClass().add("panel-background");
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(600);

        VBox infoBox = new VBox(5);
        Label lblSlotName = new Label("Slot " + slotNum);
        lblSlotName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label lblDetail = new Label();
        boolean isEmpty = meta == null || meta.getSavedAt() == null || meta.getSlotName().contains("Empty");

        if (isEmpty) {
            lblDetail.setText("-- Empty Slot --");
            lblDetail.setStyle("-fx-text-fill: #777; -fx-font-style: italic;");
        } else {
            String dateStr = meta.getSavedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            lblDetail.setText(String.format("Lv.%d %s | Battles: %d\n%s",
                    meta.getHighestLevel(), meta.getHighestLevelCharName(), meta.getTotalBattles(), dateStr));
        }
        infoBox.getChildren().addAll(lblSlotName, lblDetail);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        if (isSaveMode) {
            Button btnSave = new Button("SAVE");
            btnSave.getStyleClass().add("button-medieval");
            btnSave.setOnAction(e -> handleSave(slotNum));
            btnBox.getChildren().add(btnSave);
        } else {
            Button btnLoad = new Button("LOAD");
            btnLoad.getStyleClass().add("button-medieval");
            btnLoad.setDisable(isEmpty);
            btnLoad.setOnAction(e -> handleLoad(slotNum));
            btnBox.getChildren().add(btnLoad);
        }

        if (!isEmpty) {
            Button btnDelete = new Button("X");
            btnDelete.setStyle("-fx-background-color: #a00; -fx-text-fill: white; -fx-font-weight: bold;");
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
                    // PENTING: Gunakan MainFX.showMainMenu() setelah load sukses
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