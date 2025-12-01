package com.elemental.ui.fx;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class UIEffects {

    /**
     * Membuat efek getar pada node (misal: saat kena damage)
     */
    public static void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10); // Geser 10px
        tt.setCycleCount(6); // Bolak balik 6 kali
        tt.setAutoReverse(true);
        tt.play();
    }

    /**
     * Memunculkan angka damage melayang (Floating Text)
     */
    public static void showFloatingText(Pane root, String text, String styleClass, double x, double y) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add(styleClass);
        // Posisikan label (sedikit acak biar natural)
        lbl.setLayoutX(x + (Math.random() * 20 - 10));
        lbl.setLayoutY(y);

        // Penting: Agar label tidak mengganggu klik mouse
        lbl.setMouseTransparent(true);

        root.getChildren().add(lbl);

        // Animasi: Gerak ke atas + Memudar
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), lbl);
        tt.setByY(-50); // Gerak ke atas

        FadeTransition ft = new FadeTransition(Duration.millis(1000), lbl);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> root.getChildren().remove(lbl)); // Hapus dari layar setelah selesai
        pt.play();
    }

    /**
     * Animasi HP Bar berkurang perlahan (Smooth)
     */
    public static void animateBarChange(ProgressBar bar, double targetProgress) {
        // Timeline untuk mengubah value progress bar secara bertahap
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(bar.progressProperty(), targetProgress, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.millis(400), kv); // Durasi 0.4 detik
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }
}