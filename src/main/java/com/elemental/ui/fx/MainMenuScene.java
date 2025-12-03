package com.elemental.ui.fx;

import com.elemental.MainFX;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.scene.layout.HBox; // Tambahkan ini

import java.util.Random;

public class MainMenuScene {
    private StackPane rootLayout;
    private Random random = new Random();

    public MainMenuScene() {
        rootLayout = new StackPane();
        rootLayout.getStyleClass().add("root");

        // 1. Layer Partikel (Lebih subtle/halus untuk tema gelap)
        Pane particleLayer = createElementalParticles();

        // 2. Menu Container (Papan Kayu Dark Fantasy)
        VBox menuBox = new VBox(20); // Jarak antar elemen vertikal
        menuBox.setAlignment(Pos.CENTER);
        // HAPUS setMaxWidth agar tidak terpotong. Biarkan dia menyesuaikan kontennya.
        // menuBox.setMaxWidth(350);

        // Gunakan class CSS baru
        menuBox.getStyleClass().add("dark-fantasy-panel");

        // --- TITLE ---
        Label title = new Label("ELEMENTAL\nBATTLE ARENA");
        title.getStyleClass().add("game-title");
        title.setTextAlignment(TextAlignment.CENTER);

        Glow glow = new Glow(0.6); // Glow sedikit dikurangi agar tidak terlalu silau
        title.setEffect(glow);
        animateTitle(title);

        // --- BUTTONS ---
        VBox buttonContainer = new VBox(15); // Jarak antar tombol
        buttonContainer.setAlignment(Pos.CENTER);
        // Padding agar tombol tidak mepet ke pinggir panel
        buttonContainer.setPadding(new Insets(20, 10, 10, 10));

        Button btnCharMgmt = createMenuButton("⚔ CHARACTER ROSTER");
        btnCharMgmt.setOnAction(e -> MainFX.primaryStage.getScene().setRoot(new CharacterScene().getLayout()));

        Button btnBattle = createMenuButton("🔥 ENTER THE ARENA");
        btnBattle.setOnAction(e -> MainFX.primaryStage.getScene().setRoot(new BattleSetupScene().getLayout()));

        Button btnInventory = createMenuButton("🎒 WAR CHEST (INVENTORY)");
        btnInventory.setOnAction(e -> MainFX.primaryStage.getScene().setRoot(new InventoryScene().getLayout()));

        // Grouping Save/Load/Settings agar lebih rapi
        HBox smallBtns = new HBox(10);
        smallBtns.setAlignment(Pos.CENTER);
        Button btnSave = createSmallMenuButton("💾 SAVE");
        btnSave.setOnAction(e -> MainFX.primaryStage.getScene().setRoot(new SaveLoadScene(true).getLayout()));
        Button btnLoad = createSmallMenuButton("📂 LOAD");
        btnLoad.setOnAction(e -> MainFX.primaryStage.getScene().setRoot(new SaveLoadScene(false).getLayout()));
        Button btnSettings = createSmallMenuButton("⚙ SETTINGS");
        btnSettings.setOnAction(e -> MainFX.primaryStage.getScene().setRoot(new SettingsScene().getLayout()));
        smallBtns.getChildren().addAll(btnSave, btnLoad, btnSettings);

        Button btnExit = createMenuButton("❌ EXIT KINGDOM");
        btnExit.getStyleClass().add("button-exit"); // Tambah class khusus merah
        btnExit.setOnAction(e -> MainFX.primaryStage.close());

        buttonContainer.getChildren().addAll(btnCharMgmt, btnBattle, btnInventory, smallBtns, btnExit);

        menuBox.getChildren().addAll(title, buttonContainer);

        animateButtonsEntrance(buttonContainer);

        rootLayout.getChildren().addAll(particleLayer, menuBox);
        StackPane.setAlignment(menuBox, Pos.CENTER); // Pastikan panel selalu di tengah
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-medieval");
        // Ukuran preferensi, tapi biarkan melebar jika perlu
        btn.setPrefWidth(320);
        btn.setPrefHeight(55); // Lebih tebal (chunky)
        return btn;
    }

    // Helper untuk tombol kecil (Save/Load/Settings)
    private Button createSmallMenuButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-medieval");
        btn.setPrefWidth(100);
        btn.setPrefHeight(45);
        btn.setStyle("-fx-font-size: 14px; -fx-padding: 5 10 5 10;"); // Font sedikit lebih kecil
        return btn;
    }

    private Pane createElementalParticles() {
        Pane pane = new Pane();
        pane.setMouseTransparent(true);
        int particleCount = 40;
        for (int i = 0; i < particleCount; i++) {
            Circle particle = new Circle();
            double radius = 3 + random.nextDouble() * 10; // Partikel sedikit lebih kecil
            particle.setRadius(radius);

            int type = random.nextInt(4); // Tambah variasi warna gelap
            Color color;
            if (type == 0) color = Color.rgb(255, 69, 0, 0.3); // Merah Api Gelap
            else if (type == 1) color = Color.rgb(30, 144, 255, 0.2); // Biru Air Gelap
            else if (type == 2) color = Color.rgb(34, 139, 34, 0.2); // Hijau Tanah Gelap
            else color = Color.rgb(100, 100, 100, 0.2); // Abu-abu/Debu

            particle.setFill(color);
            particle.setEffect(new GaussianBlur(15)); // Blur lebih kuat untuk atmosfer

            particle.setTranslateX(random.nextDouble() * 800);
            particle.setTranslateY(random.nextDouble() * 600);

            pane.getChildren().add(particle);
            animateParticle(particle);
        }
        return pane;
    }

    private void animateParticle(Circle particle) {
        // Gerakan lebih lambat dan "berat"
        TranslateTransition tt = new TranslateTransition(
                Duration.seconds(15 + random.nextDouble() * 25), particle
        );
        tt.setByX(random.nextDouble() * 150 - 75);
        tt.setByY(random.nextDouble() * 150 - 75);
        tt.setAutoReverse(true);
        tt.setCycleCount(TranslateTransition.INDEFINITE);

        FadeTransition ft = new FadeTransition(
                Duration.seconds(4 + random.nextDouble() * 4), particle
        );
        ft.setFromValue(0.1);
        ft.setToValue(0.5);
        ft.setAutoReverse(true);
        ft.setCycleCount(FadeTransition.INDEFINITE);

        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.play();
    }

    private void animateTitle(Label title) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(2.5), title);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.05); st.setToY(1.05); // Gerakan lebih halus
        st.setCycleCount(ScaleTransition.INDEFINITE);
        st.setAutoReverse(true);
        st.play();
    }

    private void animateButtonsEntrance(VBox container) {
        int i = 0;
        for (Node node : container.getChildren()) {
            node.setOpacity(0);
            node.setTranslateY(30); // Jarak muncul lebih jauh

            FadeTransition ft = new FadeTransition(Duration.millis(600), node);
            ft.setToValue(1);

            // Efek overshoot (sedikit membal) saat muncul
            TranslateTransition tt = new TranslateTransition(Duration.millis(600), node);
            tt.setToY(0);
            tt.setInterpolator(Interpolator.EASE_OUT);

            ParallelTransition pt = new ParallelTransition(ft, tt);
            pt.setDelay(Duration.millis(i * 120));
            pt.play();
            i++;
        }
    }

    public StackPane getLayout() {
        return rootLayout;
    }
}