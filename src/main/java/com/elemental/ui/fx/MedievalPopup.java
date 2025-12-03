package com.elemental.ui.fx;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class MedievalPopup {

    public enum Type {
        INFO, SUCCESS, WARNING, ERROR, VICTORY, DEFEAT
    }

    /**
     * Menampilkan pesan standar (OK only)
     */
    public static void show(StackPane root, String title, String message, Type type) {
        show(root, title, message, type, null);
    }

    public static void show(StackPane root, String title, String message, Type type, Runnable onOk) {
        StackPane overlay = createOverlay(root);
        VBox dialog = createDialogBox(type);

        Label lblTitle = createTitle(title, type);
        Label lblMsg = createMessage(message);

        Button btnOk = new Button("OK");
        btnOk.getStyleClass().add("button-medieval");
        btnOk.setPrefWidth(100);
        btnOk.setOnAction(e -> {
            close(root, overlay);
            if (onOk != null) onOk.run();
        });

        dialog.getChildren().addAll(lblTitle, new Separator(), lblMsg, new Separator(), btnOk);

        overlay.getChildren().add(dialog);
        animateIn(dialog);
        root.getChildren().add(overlay);
    }

    /**
     * Menampilkan konfirmasi (YES / NO)
     */
    public static void showConfirm(StackPane root, String title, String message, Runnable onYes) {
        showConfirm(root, title, message, onYes, null);
    }

    // UPDATE: Menambahkan parameter onNo
    public static void showConfirm(StackPane root, String title, String message, Runnable onYes, Runnable onNo) {
        StackPane overlay = createOverlay(root);
        VBox dialog = createDialogBox(Type.WARNING);

        Label lblTitle = createTitle(title, Type.WARNING);
        Label lblMsg = createMessage(message);

        HBox btnBox = new HBox(15);
        btnBox.setAlignment(Pos.CENTER);

        Button btnYes = new Button("YES");
        btnYes.getStyleClass().add("button-medieval");
        btnYes.setPrefWidth(80);
        btnYes.setOnAction(e -> {
            close(root, overlay);
            if (onYes != null) onYes.run();
        });

        Button btnNo = new Button("NO");
        btnNo.getStyleClass().add("button-medieval");
        btnNo.setPrefWidth(80);
        btnNo.setOnAction(e -> {
            close(root, overlay);
            if (onNo != null) onNo.run();
        });

        btnBox.getChildren().addAll(btnYes, btnNo);
        dialog.getChildren().addAll(lblTitle, new Separator(), lblMsg, new Separator(), btnBox);

        overlay.getChildren().add(dialog);
        animateIn(dialog);
        root.getChildren().add(overlay);
    }

    // --- HELPER METHODS (Sama seperti sebelumnya) ---

    private static StackPane createOverlay(StackPane root) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        return overlay;
    }

    private static VBox createDialogBox(Type type) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setMaxSize(400, 250);
        box.setPadding(new javafx.geometry.Insets(20));

        String borderColor = "#ffd700"; // Default Gold
        if (type == Type.ERROR || type == Type.DEFEAT) borderColor = "#ff3333"; // Merah
        if (type == Type.SUCCESS || type == Type.VICTORY) borderColor = "#33ff33"; // Hijau

        box.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #2b1b17, #1a1a1a);" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(three-pass-box, black, 20, 0.5, 0, 0);"
        );
        return box;
    }

    private static Label createTitle(String text, Type type) {
        Label lbl = new Label(text);
        String color = "#ffd700";
        if (type == Type.ERROR || type == Type.DEFEAT) color = "#ff3333";
        if (type == Type.SUCCESS || type == Type.VICTORY) color = "#33ff33";

        lbl.setStyle(
                "-fx-font-family: 'Times New Roman';" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-effect: dropshadow(one-pass-box, black, 2, 2, 0, 0);"
        );
        return lbl;
    }

    private static Label createMessage(String text) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setTextAlignment(TextAlignment.CENTER);
        lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        return lbl;
    }

    private static void animateIn(VBox dialog) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), dialog);
        st.setFromX(0.5); st.setFromY(0.5);
        st.setToX(1.0); st.setToY(1.0);
        st.play();
    }

    private static void close(StackPane root, StackPane overlay) {
        root.getChildren().remove(overlay);
    }
}