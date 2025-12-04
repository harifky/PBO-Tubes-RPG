package com.elemental.ui.fx;

import com.elemental.model.CharacterClass;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import java.io.InputStream;

public class SpriteFactory {
    private static Image playerSheet;
    private static Image enemySheet;

    static {
        playerSheet = loadImage("players.png");
        enemySheet = loadImage("enemies.png");
    }

    private static Image loadImage(String filename) {
        String path = "/assets/" + filename;
        InputStream stream = SpriteFactory.class.getResourceAsStream(path);
        if (stream == null) return null;
        return new Image(stream);
    }

    public static ImageView getPlayerSprite(CharacterClass charClass) {
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

        // Row 1 = Tampak Belakang (Back View)
        int row = 1;

        Rectangle2D viewport = new Rectangle2D(col * width, row * height, width, height);
        view.setViewport(viewport);

        // --- PERUBAHAN UKURAN ---
        // Sebelumnya 220, dikecilkan jadi 140 agar proporsional
        view.setFitHeight(140);
        view.setPreserveRatio(true);

        return view;
    }

    public static ImageView getEnemySprite(String enemyName) {
        if (enemySheet == null) return createPlaceholder(Color.RED);

        ImageView view = new ImageView(enemySheet);
        String name = enemyName.toLowerCase();

        double totalW = enemySheet.getWidth();
        double totalH = enemySheet.getHeight();
        double spriteW = totalW / 5;
        double spriteH = totalH / 3;

        int col = 0; int row = 0;

        // Logic Mapping
        if (name.contains("goblin"))       { col = 0; row = 0; }
        else if (name.contains("orc"))     { col = 1; row = 0; }
        else if (name.contains("troll"))   { col = 2; row = 0; }
        else if (name.contains("skeleton")){ col = 3; row = 0; }
        else if (name.contains("zombie"))  { col = 4; row = 0; }
        else if (name.contains("bandit"))    { col = 0; row = 1; }
        else if (name.contains("assassin"))  { col = 1; row = 1; }
        else if (name.contains("witch"))     { col = 2; row = 1; }
        else if (name.contains("warlock"))   { col = 3; row = 1; }
        else if (name.contains("knight"))    { col = 4; row = 1; }
        else if (name.contains("dragon") || name.contains("phoenix")) { col = 0; row = 2; }
        else if (name.contains("golem"))     { col = 1; row = 2; }
        else if (name.contains("wraith"))    { col = 2; row = 2; }
        else if (name.contains("ghost"))     { col = 3; row = 2; }
        else if (name.contains("demon"))     { col = 4; row = 2; }

        Rectangle2D viewport = new Rectangle2D(col * spriteW, row * spriteH, spriteW, spriteH);
        view.setViewport(viewport);

        // --- PERUBAHAN UKURAN ---
        // Dikecilkan agar pas dengan background
        if (row == 2) {
            view.setFitHeight(150); // Boss sedikit lebih besar
        } else {
            view.setFitHeight(110); // Musuh biasa lebih kecil
        }

        view.setPreserveRatio(true);
        return view;
    }

    private static ImageView createPlaceholder(Color color) {
        return new ImageView();
    }
}