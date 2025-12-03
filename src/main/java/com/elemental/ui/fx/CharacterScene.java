package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.model.Character;
import com.elemental.model.CharacterClass;
import com.elemental.model.Element;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CharacterScene {
    private BorderPane layout;
    private ListView<String> charListView;

    public CharacterScene() {
        layout = new BorderPane();
        layout.getStyleClass().add("root");
        layout.setPadding(new Insets(20));

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

                // Auto-save if enabled
                if (com.elemental.model.GameSettings.getInstance().isAutoSave()) {
                    MainFX.saveLoadService.autoSave();
                    showInfoAlert("Success", "Character created!\n💾 Game auto-saved!");
                } else {
                    showInfoAlert("Success", "Character created!");
                }

                refreshList();
                nameInput.clear();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        Button btnRevive = new Button("⚕️ Revive Dead Character");
        btnRevive.getStyleClass().add("button-medieval");
        btnRevive.setOnAction(e -> showReviveDialog());

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

        layout.setLeft(createPanel);
        layout.setCenter(listPanel);
        BorderPane.setMargin(listPanel, new Insets(0, 0, 0, 20));
    }

    private void refreshList() {
        charListView.getItems().clear();
        for (Character c : MainFX.characterService.getAllCharacters()) {
            charListView.getItems().add(c.toString() + " - " + c.getStatus());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showReviveDialog() {
        // Find dead characters
        java.util.List<Character> deadCharacters = new java.util.ArrayList<>();
        for (Character c : MainFX.characterService.getAllCharacters()) {
            if (!c.isAlive()) {
                deadCharacters.add(c);
            }
        }

        if (deadCharacters.isEmpty()) {
            showInfoAlert("No Dead Characters", "✓ All characters are alive! No one needs reviving.");
            return;
        }

        // Create choice dialog
        ChoiceDialog<Character> dialog = new ChoiceDialog<>(deadCharacters.get(0), deadCharacters);
        dialog.setTitle("⚕️ Revive Dead Character");
        dialog.setHeaderText("Select character to revive:");
        dialog.setContentText("Dead character:");

        dialog.showAndWait().ifPresent(toRevive -> {
            // Check if has Revive item
            if (!toRevive.getInventory().hasItem("Revive")) {
                showAlert("No Revive Item",
                    toRevive.getName() + " doesn't have a Revive item!\n\n" +
                    "💡 Tip: Revive items can be obtained during battles.");
                return;
            }

            // Confirm revive
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Revive");
            confirmAlert.setHeaderText("Use 1 Revive item to restore " + toRevive.getName() + " with 30% HP?");
            confirmAlert.setContentText("This action will consume 1 Revive item.");

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Apply revive effect (30% HP)
                    int reviveHP = (int) (toRevive.getMaxHP() * 0.30);
                    toRevive.setCurrentHP(reviveHP);
                    toRevive.setStatus(com.elemental.model.Status.NORMAL);

                    // Remove Revive item
                    toRevive.getInventory().removeItem("Revive", 1);

                    // Show success
                    showInfoAlert("✨ Revival Successful!",
                        "💚 " + toRevive.getName() + " has been revived!\n" +
                        "HP Restored: " + reviveHP + "/" + toRevive.getMaxHP() + " (30%)\n" +
                        "Status: NORMAL");

                    refreshList();
                }
            });
        });
    }

    public BorderPane getLayout() {
        return layout;
    }
}