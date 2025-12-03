package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.factory.ItemFactory;
import com.elemental.model.Inventory;
import com.elemental.model.Item;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane; // Gunakan StackPane sebagai root
import javafx.scene.layout.VBox;
import java.util.Map;

public class InventoryScene {
    private StackPane rootStack; // Ganti ke StackPane untuk background root
    private BorderPane mainLayout;
    private VBox itemsContainer;

    public InventoryScene() {
        rootStack = new StackPane();
        rootStack.getStyleClass().add("root");

        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        Label title = new Label("GLOBAL INVENTORY");
        title.getStyleClass().add("game-title");
        VBox topBox = new VBox(title);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(0, 0, 20, 0));
        mainLayout.setTop(topBox);

        // --- Container Inventory (Dark Panel) ---
        VBox invPanel = new VBox(10);
        invPanel.getStyleClass().add("dark-fantasy-panel"); // Pakai dark panel
        invPanel.setPadding(new Insets(20));
        invPanel.setMaxWidth(800);

        itemsContainer = new VBox(10);
        itemsContainer.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(itemsContainer);
        scrollPane.setFitToWidth(true);
        // Buat scrollpane transparan agar menyatu dengan dark panel
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.getStyleClass().add("edge-to-edge"); // Menghapus border default scrollpane jika ada

        invPanel.getChildren().add(scrollPane);

        mainLayout.setCenter(invPanel);
        BorderPane.setMargin(invPanel, new Insets(0, 20, 20, 20));

        refreshInventory();

        Button btnBack = new Button("Back to Menu");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setPrefWidth(200);
        btnBack.setOnAction(e -> MainFX.showMainMenu());

        VBox bottomBox = new VBox(btnBack);
        bottomBox.setAlignment(Pos.CENTER);
        mainLayout.setBottom(bottomBox);

        rootStack.getChildren().add(mainLayout);
    }

    private void refreshInventory() {
        itemsContainer.getChildren().clear();
        Map<String, Integer> items = Inventory.getInstance().getAllItems();

        if (items.isEmpty()) {
            Label empty = new Label("-- Inventory is Empty --");
            empty.setStyle("-fx-font-size: 18px; -fx-text-fill: #aaa; -fx-font-style: italic;");
            itemsContainer.getChildren().add(empty);
            return;
        }

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String name = entry.getKey();
            int qty = entry.getValue();
            Item template = ItemFactory.getItem(name);

            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(15));
            row.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 10; -fx-border-color: #5c4033; -fx-border-radius: 10;");

            Label icon = new Label("📦");
            icon.setStyle("-fx-font-size: 24px;");

            VBox info = new VBox(2);
            Label lblName = new Label(name);
            lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #ffd700;");
            Label lblDesc = new Label(template.getDescription());
            lblDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #ccc;");

            info.getChildren().addAll(lblName, lblDesc);

            Label lblQty = new Label("x" + qty);
            lblQty.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");

            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            row.getChildren().addAll(icon, info, spacer, lblQty);
            itemsContainer.getChildren().add(row);
        }
    }

    public StackPane getLayout() { return rootStack; } // Return rootStack, bukan layout langsung
}