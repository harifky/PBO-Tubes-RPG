package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.model.*;
import com.elemental.model.Character;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class BattleScene {
    // Layout Utama
    private BorderPane layout;
    private Pane overlayPane; // Untuk floating text damage
    private TextArea battleLog;

    // Area Sprite (Tengah)
    private HBox arenaContainer; // Wadah utama pertarungan (Kiri vs Kanan)
    private VBox enemySpriteBox; // Wadah gambar musuh (Kiri)
    private VBox playerSpriteBox; // Wadah gambar player (Kanan)

    // HUD Atas (Musuh)
    private VBox topHud;
    private Label enemyNameLbl;
    private ProgressBar enemyHP;

    // HUD Bawah (Player)
    private VBox bottomHud;
    private Label playerNameLbl;
    private ProgressBar playerHP, playerMP;
    private HBox actionMenu; // Tombol-tombol

    public BattleScene() {
        // Root Stack untuk menumpuk UI Game dan Efek Visual
        StackPane rootStack = new StackPane();
        rootStack.getStyleClass().add("root");

        layout = new BorderPane();

        // Layer efek (Floating Text) agar tidak mengganggu layout
        overlayPane = new Pane();
        overlayPane.setPickOnBounds(false);

        // =========================================
        // 1. TOP SECTION (ENEMY HUD)
        // =========================================
        topHud = new VBox(5);
        topHud.setPadding(new Insets(10, 20, 10, 20));
        topHud.setAlignment(Pos.CENTER);
        topHud.getStyleClass().add("panel-background"); // Style kertas krem
        // Sedikit margin biar tidak nempel ujung layar
        BorderPane.setMargin(topHud, new Insets(10, 10, 0, 10));

        enemyNameLbl = new Label("Enemy Name");
        enemyNameLbl.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-size: 18px; -fx-font-weight: bold;");

        enemyHP = new ProgressBar(1.0);
        enemyHP.getStyleClass().add("hp-bar");
        enemyHP.setPrefWidth(400); // Bar lebar
        enemyHP.setStyle("-fx-accent: #ff3333;"); // Warna merah

        topHud.getChildren().addAll(enemyNameLbl, enemyHP);
        layout.setTop(topHud);

        // =========================================
        // 2. CENTER SECTION (BATTLE ARENA)
        // =========================================
        arenaContainer = new HBox();
        arenaContainer.setAlignment(Pos.CENTER);
        arenaContainer.setSpacing(100); // Jarak antara Player dan Musuh
        // Background gelap untuk area tengah
        arenaContainer.setStyle("-fx-background-color: #2b2b2b;");

        // --- KIRI: Musuh Sprite ---
        enemySpriteBox = new VBox();
        enemySpriteBox.setAlignment(Pos.CENTER);
        // Kita beri ID agar efek animasi tahu lokasi ini
        enemySpriteBox.setId("enemyBox");

        // --- KANAN: Player Sprite ---
        playerSpriteBox = new VBox();
        playerSpriteBox.setAlignment(Pos.CENTER);
        playerSpriteBox.setId("playerBox");

        // VS Text di tengah (Optional, bisa dihapus kalau ganggu)
        Label vsLabel = new Label("VS");
        vsLabel.setStyle("-fx-font-size: 60px; -fx-text-fill: rgba(255,255,255,0.1); -fx-font-weight: bold;");

        // Masukkan ke Arena: Musuh - VS - Player
        arenaContainer.getChildren().addAll(enemySpriteBox, vsLabel, playerSpriteBox);
        layout.setCenter(arenaContainer);

        // =========================================
        // 3. RIGHT SECTION (LOG)
        // =========================================
        VBox logContainer = new VBox();
        logContainer.setPadding(new Insets(10));
        logContainer.getStyleClass().add("panel-background");
        BorderPane.setMargin(logContainer, new Insets(10, 10, 10, 0));

        Label logTitle = new Label("Battle Log");
        logTitle.setStyle("-fx-font-weight: bold;");

        battleLog = new TextArea();
        battleLog.setEditable(false);
        battleLog.setPrefWidth(200);
        battleLog.setPrefHeight(400);
        battleLog.setWrapText(true);
        battleLog.setStyle("-fx-control-inner-background: #f4e4bc; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        logContainer.getChildren().addAll(logTitle, battleLog);
        layout.setRight(logContainer);

        // =========================================
        // 4. BOTTOM SECTION (PLAYER HUD)
        // =========================================
        bottomHud = new VBox(10);
        bottomHud.setPadding(new Insets(15));
        bottomHud.getStyleClass().add("panel-background");
        BorderPane.setMargin(bottomHud, new Insets(0, 10, 10, 10));

        // Info Nama & Level
        playerNameLbl = new Label("Player Name");
        playerNameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Bar Container
        VBox barsBox = new VBox(5);
        playerHP = new ProgressBar(1.0);
        playerHP.getStyleClass().add("hp-bar");
        playerHP.setPrefWidth(300);

        playerMP = new ProgressBar(1.0);
        playerMP.getStyleClass().add("mp-bar");
        playerMP.setPrefWidth(300);

        barsBox.getChildren().addAll(new Label("HP"), playerHP, new Label("MP"), playerMP);

        // Action Buttons
        actionMenu = new HBox(20);
        actionMenu.setAlignment(Pos.CENTER);

        Button btnAttack = new Button("ATTACK");
        btnAttack.getStyleClass().add("button-medieval");
        btnAttack.setPrefWidth(120);
        btnAttack.setPrefHeight(40);
        btnAttack.setOnAction(e -> executePlayerAction(ActionType.ATTACK, null));

        Button btnDefend = new Button("DEFEND");
        btnDefend.getStyleClass().add("button-medieval");
        btnDefend.setPrefWidth(120);
        btnDefend.setPrefHeight(40);
        btnDefend.setOnAction(e -> executePlayerAction(ActionType.DEFEND, null));

        actionMenu.getChildren().addAll(btnAttack, btnDefend);

        // Tata letak panel bawah: Info di kiri, Bar di kiri, Tombol di tengah/kanan
        HBox bottomLayout = new HBox(20);
        bottomLayout.setAlignment(Pos.CENTER_LEFT);

        VBox infoContainer = new VBox(5);
        infoContainer.getChildren().addAll(playerNameLbl, barsBox);

        // Spacer agar tombol terdorong ke tengah/kanan
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottomLayout.getChildren().addAll(infoContainer, spacer, actionMenu);
        bottomHud.getChildren().add(bottomLayout);

        layout.setBottom(bottomHud);

        // =========================================
        // FINAL SETUP
        // =========================================
        rootStack.getChildren().addAll(layout, overlayPane);

        // Mulai Battle
        initialUpdate();
    }

    public StackPane getLayout() { return (StackPane) overlayPane.getParent(); }

    private void initialUpdate() {
        Battle battle = MainFX.battleService.getCurrentBattle();
        Character player = battle.getPlayerTeam().get(0);
        Character enemy = battle.getEnemyTeam().get(0);

        // 1. Render Sprites
        enemySpriteBox.getChildren().clear();
        playerSpriteBox.getChildren().clear();

        ImageView enemyImg = SpriteFactory.getEnemySprite(enemy.getName());
        ImageView playerImg = SpriteFactory.getPlayerSprite(player.getCharacterClass());

        // Agar musuh menghadap kanan (jika perlu dicerminkan)
        // enemyImg.setScaleX(-1); // Uncomment jika gambar asli menghadap kiri

        // Efek Idle
        addBreathingEffect(enemyImg);
        addBreathingEffect(playerImg);

        enemySpriteBox.getChildren().add(enemyImg);
        playerSpriteBox.getChildren().add(playerImg);

        // 2. Update Info Awal
        updateBars(player, enemy);
        battleLog.setText("Battle Start!\n" + player.getName() + " vs " + enemy.getName() + "\n");
    }

    private void addBreathingEffect(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1.5), node);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.02); st.setToY(1.02);
        st.setCycleCount(ScaleTransition.INDEFINITE);
        st.setAutoReverse(true);
        st.play();
    }

    private void updateUI(Character attacker, Character target, int damageDealt, boolean isCritical) {
        Battle battle = MainFX.battleService.getCurrentBattle();
        Character player = battle.getPlayerTeam().get(0);
        Character enemy = battle.getEnemyTeam().get(0);

        // Update Log
        if (!battle.getBattleLog().getRecentEntries().isEmpty()) {
            String lastEntry = battle.getBattleLog().getRecentEntries().get(battle.getBattleLog().getRecentEntries().size()-1);
            battleLog.appendText(lastEntry + "\n");
        }

        // Animasi Bar
        UIEffects.animateBarChange(playerHP, (double) player.getCurrentHP() / player.getMaxHP());
        UIEffects.animateBarChange(playerMP, (double) player.getCurrentMP() / player.getMaxMP());
        UIEffects.animateBarChange(enemyHP, (double) enemy.getCurrentHP() / enemy.getMaxHP());

        // Update Label
        updateBars(player, enemy);

        // Efek Damage
        if (damageDealt > 0) {
            boolean targetIsEnemy = (target == enemy);
            Node targetNode = targetIsEnemy ? enemySpriteBox : playerSpriteBox;
            UIEffects.shakeNode(targetNode);

            // Teks Damage muncul di atas kepala sprite
            double xPos = targetIsEnemy ?
                    enemySpriteBox.localToScene(enemySpriteBox.getBoundsInLocal()).getMinX() + 50 :
                    playerSpriteBox.localToScene(playerSpriteBox.getBoundsInLocal()).getMinX() + 50;

            double yPos = layout.getCenter().getBoundsInParent().getMinY() + 100;

            String text = (isCritical ? "CRIT " : "") + damageDealt;
            String style = isCritical ? "crit-text" : "damage-text";
            UIEffects.showFloatingText(overlayPane, text, style, xPos, yPos);
        }

        if (battle.getBattleStatus() != BattleStatus.ONGOING) {
            handleBattleEnd(battle.getBattleStatus());
        }
    }

    private void updateBars(Character p, Character e) {
        // Update Nama & Text
        enemyNameLbl.setText(String.format("%s (Lv.%d)", e.getName(), e.getLevel()));
        playerNameLbl.setText(String.format("%s (Lv.%d) - %s", p.getName(), p.getLevel(), p.getStatus()));

        // Progress bar value diupdate via animasi di method updateUI,
        // tapi kita set text/tooltip jika perlu di sini.
    }

    private void executePlayerAction(ActionType type, Skill skill) {
        Battle battle = MainFX.battleService.getCurrentBattle();
        Character player = battle.getPlayerTeam().get(0);
        Character enemy = battle.getEnemyTeam().get(0);

        int hpBefore = enemy.getCurrentHP();

        BattleAction action = new BattleAction(player, type);
        action.setTarget(enemy);
        if (skill != null) action.setSkill(skill);

        battle.executeAction(action);

        int damageDealt = Math.max(0, hpBefore - enemy.getCurrentHP());
        boolean isCrit = false;
        // Cek log terakhir untuk crit (simplifikasi)
        if(!battle.getBattleLog().getRecentEntries().isEmpty()){
            isCrit = battle.getBattleLog().getRecentEntries().get(battle.getBattleLog().getRecentEntries().size()-1).contains("CRITICAL");
        }

        updateUI(player, enemy, damageDealt, isCrit);

        if (battle.getBattleStatus() != BattleStatus.ONGOING) return;

        // ENEMY TURN
        actionMenu.setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
        pause.setOnFinished(e -> {
            if (battle.getBattleStatus() != BattleStatus.ONGOING) return;
            int pHpBefore = player.getCurrentHP();

            BattleAction enemyAction = new BattleAction(enemy, ActionType.ATTACK);
            enemyAction.setTarget(player);
            battle.executeAction(enemyAction);

            int pDamage = Math.max(0, pHpBefore - player.getCurrentHP());
            boolean pCrit = false;
            // logic crit musuh sama

            updateUI(enemy, player, pDamage, pCrit);
            actionMenu.setDisable(false);
        });
        pause.play();
    }

    private void handleBattleEnd(BattleStatus status) {
        actionMenu.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Battle Result");
        alert.setHeaderText(status == BattleStatus.VICTORY ? "VICTORY!" : "DEFEAT...");
        alert.show();
        alert.setOnHidden(e -> MainFX.primaryStage.setScene(new MainMenuScene().getScene()));
    }
}