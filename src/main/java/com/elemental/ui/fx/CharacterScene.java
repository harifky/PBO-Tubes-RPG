package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.model.Character;
import com.elemental.model.CharacterClass;
import com.elemental.model.Element;
import com.elemental.model.GameSettings;
import com.elemental.model.Inventory;
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
        rootStack = new StackPane();
        rootStack.getStyleClass().add("root");

        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(30));

        // Judul Halaman
        Label title = new Label("CHARACTER ROSTER");
        title.getStyleClass().add("game-title");

        VBox topBox = new VBox(title);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(0, 0, 20, 0));
        mainLayout.setTop(topBox);

        // --- Panel Kiri: Create Hero ---
        VBox createPanel = new VBox(15);
        createPanel.getStyleClass().add("dark-fantasy-panel");
        createPanel.setPadding(new Insets(25));
        createPanel.setPrefWidth(350);
        createPanel.setAlignment(Pos.TOP_CENTER);

        Label lblCreate = new Label("Create New Hero");
        lblCreate.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #ffd700;");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Hero Name");
        nameInput.setStyle("-fx-font-size: 14px;");

        ComboBox<CharacterClass> classBox = new ComboBox<>();
        classBox.getItems().addAll(CharacterClass.values());
        classBox.setPromptText("Select Class");
        classBox.setPrefWidth(300);

        ComboBox<Element> elementBox = new ComboBox<>();
        elementBox.getItems().addAll(Element.values());
        elementBox.setPromptText("Select Element");
        elementBox.setPrefWidth(300);

        Button btnCreate = new Button("Summon Hero");
        btnCreate.getStyleClass().add("button-medieval");
        btnCreate.setPrefWidth(300);
        btnCreate.setOnAction(e -> {
            try {
                Character newChar = MainFX.characterService.createCharacter(
                        nameInput.getText(),
                        classBox.getValue(),
                        elementBox.getValue()
                );
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

        createPanel.getChildren().addAll(lblCreate,
                new Label("Name:"), nameInput,
                new Label("Class:"), classBox,
                new Label("Element:"), elementBox,
                new Separator(), btnCreate);

        // --- Panel Kanan: Roster List ---
        VBox listPanel = new VBox(10);
        listPanel.getStyleClass().add("dark-fantasy-panel");
        listPanel.setPadding(new Insets(20));

        Label lblRoster = new Label("Your Roster");
        lblRoster.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #ffd700;");

        charListView = new ListView<>();
        // Styling agar list menyatu dengan panel gelap
        charListView.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-control-inner-background: transparent;");
        VBox.setVgrow(charListView, Priority.ALWAYS);
        refreshList();

        // Button Group
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        // TOMBOL VIEW STATS (BARU)
        Button btnStats = new Button("View Stats");
        btnStats.getStyleClass().add("button-medieval");
        btnStats.setPrefWidth(100);
        btnStats.setOnAction(e -> showCharacterStats());

        Button btnRevive = new Button("Revive");
        btnRevive.getStyleClass().add("button-medieval");
        btnRevive.setPrefWidth(80);
        btnRevive.setOnAction(e -> showReviveUI());

        Button btnDelete = new Button("Delete");
        btnDelete.getStyleClass().add("button-medieval");
        // Warna merah gelap untuk indikasi aksi berbahaya
        btnDelete.setStyle("-fx-base: #5e1b1b;");
        btnDelete.setPrefWidth(80);
        btnDelete.setOnAction(e -> deleteCharacter());

        Button btnBack = new Button("Back");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setPrefWidth(80);
        btnBack.setOnAction(e -> MainFX.showMainMenu());

        buttonBox.getChildren().addAll(btnStats, btnRevive, btnDelete, btnBack);

        listPanel.getChildren().addAll(lblRoster, charListView, new Separator(), buttonBox);

        mainLayout.setLeft(createPanel);
        mainLayout.setCenter(listPanel);
        BorderPane.setMargin(listPanel, new Insets(0, 0, 0, 20));

        rootStack.getChildren().add(mainLayout);
    }

    private void refreshList() {
        charListView.getItems().clear();
        for (Character c : MainFX.characterService.getAllCharacters()) {
            String statusIcon = c.isAlive() ? "❤️" : "💀";
            charListView.getItems().add(String.format("%s %s (Lv.%d %s) - %s",
                    statusIcon, c.getName(), c.getLevel(), c.getCharacterClass(), c.getStatus()));
        }
    }

    private void showCharacterStats() {
        int selectedIndex = charListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0) {
            MedievalPopup.show(rootStack, "NO SELECTION", "Please select a character to view stats.", MedievalPopup.Type.INFO);
            return;
        }

        Character character = MainFX.characterService.getCharacter(selectedIndex);
        // Menggunakan method getStatsPreview() yang sudah ada di model Character
        String statsInfo = character.getStatsPreview();

        MedievalPopup.show(rootStack, "CHARACTER STATS", statsInfo, MedievalPopup.Type.INFO);
    }

    private void deleteCharacter() {
        // Ambil index yang dipilih
        int selectedIndex = charListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0) {
            MedievalPopup.show(rootStack, "NO SELECTION", "Please select a character to delete.", MedievalPopup.Type.WARNING);
            return;
        }

        Character character = MainFX.characterService.getCharacter(selectedIndex);

        // Konfirmasi Hapus
        MedievalPopup.showConfirm(rootStack, "DELETE CHARACTER",
                "Are you sure you want to delete " + character.getName() + "?\nThis action cannot be undone.",
                () -> {
                    // Hapus dari service
                    MainFX.characterService.removeCharacter(selectedIndex);

                    // Auto-save jika aktif
                    if (GameSettings.getInstance().isAutoSave()) {
                        MainFX.saveLoadService.autoSave();
                    }

                    MedievalPopup.show(rootStack, "DELETED", "Character has been deleted.", MedievalPopup.Type.INFO);
                    refreshList();
                }
        );
    }

    private void showReviveUI() {
        List<Character> deadChars = new ArrayList<>();
        for(Character c : MainFX.characterService.getAllCharacters()) {
            if(!c.isAlive()) deadChars.add(c);
        }

        if(deadChars.isEmpty()) {
            MedievalPopup.show(rootStack, "INFO", "No dead characters to revive.", MedievalPopup.Type.INFO);
            return;
        }

        Character target = deadChars.get(0);

        if (!Inventory.getInstance().hasItem("Revive")) {
            MedievalPopup.show(rootStack, "MISSING ITEM",
                    "You need a 'Revive' item in Global Inventory.", MedievalPopup.Type.WARNING);
            return;
        }

        MedievalPopup.showConfirm(rootStack, "REVIVE CHARACTER",
                "Revive " + target.getName() + " using 1 Revive item?",
                () -> {
                    int reviveHP = (int) (target.getMaxHP() * 0.30);
                    target.setCurrentHP(reviveHP);
                    target.setStatus(com.elemental.model.Status.NORMAL);
                    Inventory.getInstance().removeItem("Revive", 1);
                    MedievalPopup.show(rootStack, "REVIVED!", target.getName() + " returned to life!", MedievalPopup.Type.SUCCESS);
                    refreshList();

                    if (GameSettings.getInstance().isAutoSave()) {
                        MainFX.saveLoadService.autoSave();
                    }
                }
        );
    }

    public StackPane getLayout() { return rootStack; }
}