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
                MainFX.characterService.createCharacter(
                        nameInput.getText(),
                        classBox.getValue(),
                        elementBox.getValue()
                );
                refreshList();
                nameInput.clear();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        Button btnBack = new Button("Back to Menu");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setOnAction(e -> MainFX.primaryStage.setScene(new MainMenuScene().getScene()));

        createPanel.getChildren().addAll(lblCreate, new Label("Name:"), nameInput,
                new Label("Class:"), classBox,
                new Label("Element:"), elementBox,
                new Separator(), btnCreate, btnBack);

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

    public BorderPane getLayout() {
        return layout;
    }
}