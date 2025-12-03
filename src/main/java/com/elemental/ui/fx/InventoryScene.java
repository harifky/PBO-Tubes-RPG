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
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

public class InventoryScene {
    private BorderPane layout;
    private VBox itemsContainer;

    public InventoryScene() {
        layout = new BorderPane();
        layout.getStyleClass().add("root");
        layout.setPadding(new Insets(20));

        // HEADER
        Label title = new Label("GLOBAL INVENTORY");
        title.getStyleClass().add("game-title");
        VBox topBox = new VBox(title);
        topBox.setAlignment(Pos.CENTER);
        layout.setTop(topBox);

        // ITEMS AREA
        itemsContainer = new VBox(10);
        itemsContainer.setPadding(new Insets(10));
        itemsContainer.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(itemsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.getStyleClass().add("panel-background");

        layout.setCenter(scrollPane);
        BorderPane.setMargin(scrollPane, new Insets(20));

        refreshInventory();

        // FOOTER
        Button btnBack = new Button("Back to Menu");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setPrefWidth(200);
        btnBack.setOnAction(e -> MainFX.primaryStage.setScene(new MainMenuScene().getScene()));

        VBox bottomBox = new VBox(btnBack);
        bottomBox.setAlignment(Pos.CENTER);
        layout.setBottom(bottomBox);
    }

    private void refreshInventory() {
        itemsContainer.getChildren().clear();
        Map<String, Integer> items = Inventory.getInstance().getAllItems();

        if (items.isEmpty()) {
            Label empty = new Label("-- Inventory is Empty --");
            empty.setStyle("-fx-font-size: 18px; -fx-text-fill: #555;");
            itemsContainer.getChildren().add(empty);
            return;
        }

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String name = entry.getKey();
            int qty = entry.getValue();
            Item template = ItemFactory.getItem(name);

            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));
            row.setStyle("-fx-border-color: #5c4033; -fx-border-width: 0 0 1 0;"); // Garis bawah

            // Icon Placeholder (Optional, pakai kotak warna)
            Label icon = new Label("📦");
            icon.setStyle("-fx-font-size: 24px;");

            // Info
            VBox info = new VBox(2);
            Label lblName = new Label(name);
            lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            Label lblDesc = new Label(template.getDescription());
            lblDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #444;");

            info.getChildren().addAll(lblName, lblDesc);

            // Qty
            Label lblQty = new Label("x" + qty);
            lblQty.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

            // Spacer
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            row.getChildren().addAll(icon, info, spacer, lblQty);
            itemsContainer.getChildren().add(row);
        }
    }

    public BorderPane getLayout() { return layout; }
}