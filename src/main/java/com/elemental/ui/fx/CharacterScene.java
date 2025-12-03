package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.factory.CharacterFactory;
import com.elemental.model.Character;
import com.elemental.model.CharacterClass;
import com.elemental.model.Element;
import com.elemental.model.GameSettings;
import com.elemental.model.Inventory;
import com.elemental.service.SaveLoadService; // <--- TAMBAHKAN INI
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class CharacterScene {
    private StackPane rootStack;
    private BorderPane mainLayout;
    private ListView<String> charListView;

    public CharacterScene() {
        // Init Root Stack untuk Overlay
        rootStack = new StackPane();
        rootStack.getStyleClass().add("root");

        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        // --- LEFT: CREATE FORM ---
        VBox createPanel = new VBox(10);
        createPanel.getStyleClass().add("panel-background");
        createPanel.setPadding(new Insets(15));
        createPanel.setPrefWidth(300);

        Label lblCreate = new Label("Create New Hero");
        lblCreate.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Hero Name");

        ComboBox<CharacterClass> classBox = new ComboBox<>();
        classBox.getItems().addAll(CharacterClass.values());
        classBox.setPromptText("Select Class");

        ComboBox<Element> elementBox = new ComboBox<>();
        elementBox.getItems().addAll(Element.values());
        elementBox.setPromptText("Select Element");

        Button btnCreate = new Button("Summon Hero");
        btnCreate.getStyleClass().add("button-medieval");
        btnCreate.setOnAction(e -> {
            try {
                Character newChar = MainFX.characterService.createCharacter(
                        nameInput.getText(),
                        classBox.getValue(),
                        elementBox.getValue()
                );

                // Auto-save logic
                String msg = "Character " + newChar.getName() + " created!";
                if (GameSettings.getInstance().isAutoSave()) {
                    MainFX.saveLoadService.autoSave();
                    msg += "\nGame auto-saved.";
                }

                MedievalPopup.show(rootStack, "SUCCESS", msg, MedievalPopup.Type.SUCCESS);
                refreshList();
                nameInput.clear();
            } catch (Exception ex) {
                MedievalPopup.show(rootStack, "ERROR", ex.getMessage(), MedievalPopup.Type.ERROR);
            }
        });

        Button btnRevive = new Button("Revive Dead Character");
        btnRevive.getStyleClass().add("button-medieval");
        btnRevive.setOnAction(e -> showReviveUI());

        Button btnBack = new Button("Back to Menu");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setOnAction(e -> MainFX.primaryStage.setScene(new MainMenuScene().getScene()));

        createPanel.getChildren().addAll(lblCreate, new Label("Name:"), nameInput,
                new Label("Class:"), classBox,
                new Label("Element:"), elementBox,
                new Separator(), btnCreate, btnRevive, btnBack);

        // --- RIGHT: LIST VIEW ---
        VBox listPanel = new VBox(10);
        listPanel.getStyleClass().add("panel-background");
        listPanel.setPadding(new Insets(15));

        charListView = new ListView<>();
        refreshList();

        listPanel.getChildren().addAll(new Label("Your Roster"), charListView);

        mainLayout.setLeft(createPanel);
        mainLayout.setCenter(listPanel);
        BorderPane.setMargin(listPanel, new Insets(0, 0, 0, 20));

        rootStack.getChildren().add(mainLayout);
    }

    private void refreshList() {
        charListView.getItems().clear();
        for (Character c : MainFX.characterService.getAllCharacters()) {
            charListView.getItems().add(c.toString() + " - " + c.getStatus());
        }
    }

    // Custom Revive UI menggunakan MedievalPopup Logic
    private void showReviveUI() {
        // Cari karakter mati
        List<Character> deadChars = new ArrayList<>();
        for(Character c : MainFX.characterService.getAllCharacters()) {
            if(!c.isAlive()) deadChars.add(c);
        }

        if(deadChars.isEmpty()) {
            MedievalPopup.show(rootStack, "INFO", "No dead characters to revive.", MedievalPopup.Type.INFO);
            return;
        }

        // Versi Simpel: Revive karakter mati pertama yang ditemukan
        Character target = deadChars.get(0);

        // Check Global Inventory
        if (!Inventory.getInstance().hasItem("Revive")) {
            MedievalPopup.show(rootStack, "MISSING ITEM",
                    "You need a 'Revive' item in Global Inventory.",
                    MedievalPopup.Type.WARNING);
            return;
        }

        MedievalPopup.showConfirm(rootStack, "REVIVE CHARACTER",
                "Revive " + target.getName() + " using 1 Revive item?",
                () -> {
                    int reviveHP = (int) (target.getMaxHP() * 0.30);
                    target.setCurrentHP(reviveHP);
                    target.setStatus(com.elemental.model.Status.NORMAL);

                    // Remove from Global Inventory
                    Inventory.getInstance().removeItem("Revive", 1);

                    MedievalPopup.show(rootStack, "REVIVED!",
                            target.getName() + " returned to life!", MedievalPopup.Type.SUCCESS);
                    refreshList();
                }
        );
    }

    public StackPane getLayout() {
        return rootStack;
    }
}