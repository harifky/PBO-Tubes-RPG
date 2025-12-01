package com.elemental.ui.fx;

import com.elemental.model.CharacterClass;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;

public class SpriteFactory {
    private static Image playerSheet;
    private static Image enemySheet;

    static {
        // UPDATE: Sekarang memuat 'enemies.png' (bukan .jpg lagi)
        playerSheet = loadImage("players.png");
        enemySheet = loadImage("enemies.png");
    }

    // Helper method untuk load gambar dengan pengecekan error
    private static Image loadImage(String filename) {
        String path = "/assets/" + filename;
        InputStream stream = SpriteFactory.class.getResourceAsStream(path);

        if (stream == null) {
            System.err.println("❌ CRITICAL ERROR: File gambar tidak ditemukan: " + path);
            System.err.println("   Pastikan file ada di folder: src/main/resources/assets/" + filename);
            System.err.println("   Dan jangan lupa REBUILD PROJECT setelah menambah file baru!");
            return null;
        }
        return new Image(stream);
    }

    public static ImageView getPlayerSprite(CharacterClass charClass) {
        // Fallback jika gambar gagal dimuat
        if (playerSheet == null) return createPlaceholder(Color.BLUE);

        ImageView view = new ImageView(playerSheet);

        double width = playerSheet.getWidth() / 3;
        double height = playerSheet.getHeight() / 2;

        int col = 0;
        switch (charClass) {
            case MAGE: col = 0; break;
            case WARRIOR: col = 1; break;
            case RANGER: col = 2; break;
        }

        int row = 1; // Back View

        Rectangle2D viewport = new Rectangle2D(col * width, row * height, width, height);
        view.setViewport(viewport);
        view.setFitHeight(180);
        view.setPreserveRatio(true);

        return view;
    }

    public static ImageView getEnemySprite(String enemyName) {
        // Fallback jika gambar gagal dimuat
        if (enemySheet == null) return createPlaceholder(Color.RED);

        ImageView view = new ImageView(enemySheet);
        String name = enemyName.toLowerCase();

        double totalW = enemySheet.getWidth();
        double totalH = enemySheet.getHeight();

        double normalWidth = totalW / 5;
        double normalHeight = totalH / 3;
        double bossWidth = totalW / 4;

        double x = 0;
        double y = 0;
        double w = normalWidth;
        double h = normalHeight;

        // --- MAPPING COORDINATES ---
        if (name.contains("goblin")) { x = 0; y = 0; }
        else if (name.contains("orc")) { x = normalWidth; y = 0; }
        else if (name.contains("troll")) { x = 2 * normalWidth; y = 0; }
        else if (name.contains("skeleton")) { x = 3 * normalWidth; y = 0; }
        else if (name.contains("zombie")) { x = 4 * normalWidth; y = 0; }

        else if (name.contains("bandit")) { x = 0; y = normalHeight; }
        else if (name.contains("assassin") || name.contains("rogue")) { x = normalWidth; y = normalHeight; }
        else if (name.contains("witch")) { x = 2 * normalWidth; y = normalHeight; }
        else if (name.contains("warlock") || name.contains("demon")) { x = 3 * normalWidth; y = normalHeight; }
        else if (name.contains("knight")) { x = 4 * normalWidth; y = normalHeight; }

        else if (name.contains("dragon") || name.contains("phoenix")) { x = 0; y = 2 * normalHeight; w = bossWidth; }
        else if (name.contains("golem")) { x = bossWidth; y = 2 * normalHeight; w = bossWidth; }
        else if (name.contains("wraith") || name.contains("ghost")) { x = 2 * bossWidth; y = 2 * normalHeight; w = bossWidth; }
        else if (name.contains("demon") || name.contains("satan")) { x = 3 * bossWidth; y = 2 * normalHeight; w = bossWidth; }

        Rectangle2D viewport = new Rectangle2D(x, y, w, h);
        view.setViewport(viewport);

        if (y >= 2 * normalHeight) {
            view.setFitHeight(180);
        } else {
            view.setFitHeight(140);
        }

        view.setPreserveRatio(true);
        return view;
    }

    private static ImageView createPlaceholder(Color color) {
        return new ImageView(); // Return kosong agar tidak crash
    }
}