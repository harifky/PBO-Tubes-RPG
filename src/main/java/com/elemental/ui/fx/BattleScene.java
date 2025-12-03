package com.elemental.ui.fx;

import com.elemental.MainFX;
import com.elemental.factory.EnemyFactory;
import com.elemental.factory.ItemFactory;
import com.elemental.model.*;
import com.elemental.model.Character;
import com.elemental.strategy.AIStrategy;
import com.elemental.strategy.AIStrategyFactory;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BattleScene {
    // Layout Components
    private StackPane rootStack;
    private BorderPane layout;
    private Pane overlayPane;
    private TextArea battleLog;

    // Sprite Containers
    private HBox arenaContainer;
    private VBox enemySpriteBox;
    private VBox playerSpriteBox;

    // HUD Components
    private BorderPane topBar;
    private VBox topHudCenter;
    private Label enemyNameLbl;
    private ProgressBar enemyHP;

    private VBox bottomHud;
    private Label playerNameLbl;
    private ProgressBar playerHP, playerMP;
    private HBox actionMenu;

    // State
    private boolean isPaused = false;

    public BattleScene() {
        // 1. Root Stack
        rootStack = new StackPane();
        rootStack.getStyleClass().add("root");

        layout = new BorderPane();

        // Layer untuk floating text
        overlayPane = new Pane();
        overlayPane.setPickOnBounds(false);

        // =========================================
        // 2. TOP SECTION (ENEMY HUD + PAUSE)
        // =========================================
        topBar = new BorderPane();
        topBar.setPadding(new Insets(15));
        BorderPane.setMargin(topBar, new Insets(10));

        // -- Enemy HUD (Tengah) --
        topHudCenter = new VBox(5);
        topHudCenter.setAlignment(Pos.CENTER);
        topHudCenter.getStyleClass().add("panel-background");
        topHudCenter.setPadding(new Insets(10, 40, 10, 40));
        topHudCenter.setMaxWidth(500);
        topHudCenter.setEffect(new DropShadow(10, Color.BLACK));

        enemyNameLbl = new Label("Enemy Name");
        enemyNameLbl.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-size: 20px; -fx-font-weight: bold;");

        enemyHP = new ProgressBar(1.0);
        enemyHP.getStyleClass().add("hp-bar");
        enemyHP.setPrefWidth(400);
        enemyHP.setStyle("-fx-accent: #ff3333;");

        topHudCenter.getChildren().addAll(enemyNameLbl, enemyHP);

        // -- Tombol Pause (Kanan) --
        Button btnPause = new Button("||");
        btnPause.getStyleClass().add("button-medieval");
        btnPause.setPrefSize(45, 45);
        btnPause.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 10;");
        btnPause.setOnAction(e -> showPauseMenu());

        topBar.setCenter(topHudCenter);
        topBar.setRight(btnPause);

        layout.setTop(topBar);

        // =========================================
        // 3. CENTER SECTION (ARENA)
        // =========================================
        arenaContainer = new HBox();
        arenaContainer.setAlignment(Pos.CENTER);
        arenaContainer.setSpacing(150);

        // Background Image + Border Kayu
        arenaContainer.setStyle(
                "-fx-background-image: url('/assets/battle_bg.jpg');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-border-color: #3e2723;" +
                        "-fx-border-width: 8px;" +
                        "-fx-border-radius: 0;" +
                        "-fx-effect: innerShadow(gaussian, rgba(0,0,0,0.9), 40, 0.4, 0, 0);"
        );

        enemySpriteBox = new VBox();
        enemySpriteBox.setAlignment(Pos.CENTER);
        enemySpriteBox.setId("enemyBox");

        playerSpriteBox = new VBox();
        playerSpriteBox.setAlignment(Pos.CENTER);
        playerSpriteBox.setId("playerBox");

        arenaContainer.getChildren().addAll(enemySpriteBox, playerSpriteBox);
        layout.setCenter(arenaContainer);

        // =========================================
        // 4. RIGHT SECTION (LOG)
        // =========================================
        VBox logContainer = new VBox();
        logContainer.setPadding(new Insets(10));
        logContainer.getStyleClass().add("panel-background");
        BorderPane.setMargin(logContainer, new Insets(10, 10, 10, 0));

        Label logTitle = new Label("Battle Log");
        logTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        battleLog = new TextArea();
        battleLog.setEditable(false);
        battleLog.setPrefWidth(220);
        battleLog.setPrefHeight(400);
        battleLog.setWrapText(true);
        battleLog.setStyle("-fx-control-inner-background: #f4e4bc; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        logContainer.getChildren().addAll(logTitle, battleLog);
        layout.setRight(logContainer);

        // =========================================
        // 5. BOTTOM SECTION (PLAYER HUD)
        // =========================================
        bottomHud = new VBox(10);
        bottomHud.setPadding(new Insets(20));
        bottomHud.getStyleClass().add("panel-background");
        bottomHud.setEffect(new DropShadow(10, Color.BLACK));
        BorderPane.setMargin(bottomHud, new Insets(0, 10, 10, 10));

        playerNameLbl = new Label("Player Name");
        playerNameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        VBox barsBox = new VBox(8);
        playerHP = new ProgressBar(1.0);
        playerHP.getStyleClass().add("hp-bar");
        playerHP.setPrefWidth(350);

        playerMP = new ProgressBar(1.0);
        playerMP.getStyleClass().add("mp-bar");
        playerMP.setPrefWidth(350);

        barsBox.getChildren().addAll(new Label("HP"), playerHP, new Label("MP"), playerMP);

        // Action Buttons
        actionMenu = new HBox(20);
        actionMenu.setAlignment(Pos.CENTER_RIGHT);

        double btnW = 110;
        double btnH = 45;

        Button btnAttack = createActionButton("ATTACK", btnW, btnH);
        btnAttack.setStyle("-fx-base: #8b0000;");
        btnAttack.setOnAction(e -> executePlayerAction(ActionType.ATTACK, null, null));

        Button btnSkill = createActionButton("SKILL", btnW, btnH);
        btnSkill.setOnAction(e -> showSkillSelection());

        Button btnItem = createActionButton("ITEM", btnW, btnH);
        btnItem.setOnAction(e -> showItemSelection());

        Button btnDefend = createActionButton("DEFEND", btnW, btnH);
        btnDefend.setStyle("-fx-base: #2f4f4f;");
        btnDefend.setOnAction(e -> executePlayerAction(ActionType.DEFEND, null, null));

        actionMenu.getChildren().addAll(btnAttack, btnSkill, btnItem, btnDefend);

        HBox bottomLayout = new HBox(30);
        bottomLayout.setAlignment(Pos.CENTER_LEFT);

        VBox infoContainer = new VBox(5);
        infoContainer.getChildren().addAll(playerNameLbl, barsBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottomLayout.getChildren().addAll(infoContainer, spacer, actionMenu);
        bottomHud.getChildren().add(bottomLayout);

        layout.setBottom(bottomHud);

        rootStack.getChildren().addAll(layout, overlayPane);

        initialUpdate();
    }

    private Button createActionButton(String text, double w, double h) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-medieval");
        btn.setPrefSize(w, h);
        return btn;
    }

    public StackPane getLayout() { return rootStack; }

    // =========================================
    // UI LOGIC: MENU OVERLAYS
    // =========================================

    private void showPauseMenu() {
        isPaused = true;

        VBox pauseContent = new VBox(15);
        pauseContent.setAlignment(Pos.CENTER);

        Button btnResume = createActionButton("RESUME", 220, 50);
        btnResume.setOnAction(e -> closeOverlay());

        Button btnSettings = createActionButton("SETTINGS", 220, 50);
        btnSettings.setOnAction(e -> showInGameSettingsMenu());

        Button btnExit = createActionButton("EXIT TO MENU", 220, 50);
        btnExit.setStyle("-fx-background-color: #8b0000; -fx-text-fill: white; -fx-border-color: #ffd700; -fx-border-width: 2;");
        btnExit.setOnAction(e -> {
            // MENGGUNAKAN MEDIEVAL POPUP UNTUK KONFIRMASI
            MedievalPopup.showConfirm(rootStack, "LEAVE BATTLE?",
                    "Are you sure you want to surrender?\nAll unsaved progress will be lost.",
                    () -> MainFX.primaryStage.setScene(new MainMenuScene().getScene())
            );
        });

        pauseContent.getChildren().addAll(btnResume, btnSettings, btnExit);
        showFancyOverlay("GAME PAUSED", pauseContent);
    }

    private void showInGameSettingsMenu() {
        VBox settingsBox = new VBox(20);
        settingsBox.setAlignment(Pos.CENTER);

        CheckBox cbLog = new CheckBox("Detailed Battle Log");
        cbLog.setStyle("-fx-font-size: 16px; -fx-text-fill: #3e1903; -fx-font-weight: bold;");
        cbLog.setSelected(GameSettings.getInstance().isShowDetailedLog());
        cbLog.setOnAction(e -> GameSettings.getInstance().setShowDetailedLog(cbLog.isSelected()));

        CheckBox cbAuto = new CheckBox("Auto Progress");
        cbAuto.setStyle("-fx-font-size: 16px; -fx-text-fill: #3e1903; -fx-font-weight: bold;");
        cbAuto.setSelected(GameSettings.getInstance().isAutoProgress());
        cbAuto.setOnAction(e -> GameSettings.getInstance().setAutoProgress(cbAuto.isSelected()));

        settingsBox.getChildren().addAll(cbLog, cbAuto);

        showFancyOverlay("SETTINGS", settingsBox);
    }

    private void showFancyOverlay(String title, Node content) {
        StackPane overlayBg = new StackPane();
        overlayBg.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxSize(400, 400);
        menuBox.setPadding(new Insets(30));

        menuBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #2b1b17, #1a1a1a);" +
                        "-fx-border-color: #ffd700;" +
                        "-fx-border-width: 4;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, black, 20, 0.5, 0, 0);"
        );

        Label lblTitle = new Label(title);
        lblTitle.setStyle(
                "-fx-font-family: 'Times New Roman';" +
                        "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #ffd700;" +
                        "-fx-effect: dropshadow(one-pass-box, black, 2, 2, 0, 0);"
        );

        Button btnBack = new Button("Back");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setOnAction(e -> closeOverlay());

        if (!title.equals("GAME PAUSED")) {
            menuBox.getChildren().addAll(lblTitle, new Separator(), content, new Separator(), btnBack);
        } else {
            menuBox.getChildren().addAll(lblTitle, new Separator(), content);
        }

        overlayBg.getChildren().add(menuBox);
        rootStack.getChildren().add(overlayBg);

        ScaleTransition st = new ScaleTransition(Duration.millis(200), menuBox);
        st.setFromX(0.5); st.setFromY(0.5);
        st.setToX(1.0); st.setToY(1.0);
        st.play();
    }

    private void closeOverlay() {
        if (rootStack.getChildren().size() > 2) {
            rootStack.getChildren().remove(rootStack.getChildren().size() - 1);
        }
        if (rootStack.getChildren().size() <= 2) {
            isPaused = false;
        }
    }

    // --- Overlay Sederhana (Skill/Item) ---
    private void showOverlayMenu(String title, Node content) {
        StackPane overlayBg = new StackPane();
        overlayBg.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox menuBox = new VBox(15);
        menuBox.getStyleClass().add("panel-background");
        menuBox.setPadding(new Insets(20));
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setMaxSize(400, 350);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-underline: true;");

        Button btnBack = new Button("Back");
        btnBack.getStyleClass().add("button-medieval");
        btnBack.setOnAction(e -> closeOverlay());

        menuBox.getChildren().addAll(lblTitle, content, new Separator(), btnBack);
        overlayBg.getChildren().add(menuBox);
        rootStack.getChildren().add(overlayBg);
    }

    private void showSkillSelection() {
        if (isPaused) return;

        Character player = MainFX.battleService.getCurrentBattle().getPlayerTeam().get(0);
        VBox skillList = new VBox(10);
        skillList.setAlignment(Pos.CENTER);

        for (Skill skill : player.getSkills()) {
            Button btn = new Button(String.format("%s (%d MP)", skill.getName(), skill.getMpCost()));
            btn.getStyleClass().add("button-medieval");
            btn.setPrefWidth(250);

            if (!player.canUseSkill(skill)) {
                btn.setDisable(true);
                btn.setStyle("-fx-opacity: 0.6; -fx-text-fill: #555;");
            } else {
                btn.setOnAction(e -> {
                    closeOverlay();
                    executePlayerAction(ActionType.SKILL, skill, null);
                });
            }
            skillList.getChildren().add(btn);
        }
        showOverlayMenu("SELECT SKILL", skillList);
    }

    private void showItemSelection() {
        if (isPaused) return;

        Character player = MainFX.battleService.getCurrentBattle().getPlayerTeam().get(0);
        Inventory inv = player.getInventory();
        VBox itemList = new VBox(10);
        itemList.setAlignment(Pos.CENTER);

        Map<String, Integer> items = inv.getAllItems();

        if (items.isEmpty()) {
            itemList.getChildren().add(new Label("Inventory Empty!"));
        } else {
            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                String itemName = entry.getKey();
                int qty = entry.getValue();
                Item itemTemplate = ItemFactory.getItem(itemName);

                Button btn = new Button(String.format("%s (x%d)", itemName, qty));
                btn.getStyleClass().add("button-medieval");
                btn.setPrefWidth(250);

                Tooltip tooltip = new Tooltip(itemTemplate.getDescription());
                btn.setTooltip(tooltip);

                btn.setOnAction(e -> {
                    closeOverlay();
                    executePlayerAction(ActionType.ITEM, null, itemTemplate);
                });
                itemList.getChildren().add(btn);
            }
        }
        showOverlayMenu("SELECT ITEM", itemList);
    }

    // =========================================
    // GAME LOGIC & ANIMATION
    // =========================================

    private void initialUpdate() {
        Battle battle = MainFX.battleService.getCurrentBattle();
        Character player = battle.getPlayerTeam().get(0);
        Character enemy = battle.getEnemyTeam().get(0);

        enemySpriteBox.getChildren().clear();
        playerSpriteBox.getChildren().clear();

        ImageView enemyImg = SpriteFactory.getEnemySprite(enemy.getName());
        ImageView playerImg = SpriteFactory.getPlayerSprite(player.getCharacterClass());

        addBreathingEffect(enemyImg);
        addBreathingEffect(playerImg);

        enemySpriteBox.getChildren().add(enemyImg);
        playerSpriteBox.getChildren().add(playerImg);

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

        if (!battle.getBattleLog().getRecentEntries().isEmpty()) {
            String lastEntry = battle.getBattleLog().getRecentEntries().get(battle.getBattleLog().getRecentEntries().size()-1);
            battleLog.appendText(lastEntry + "\n");
        }

        UIEffects.animateBarChange(playerHP, (double) player.getCurrentHP() / player.getMaxHP());
        UIEffects.animateBarChange(playerMP, (double) player.getCurrentMP() / player.getMaxMP());
        UIEffects.animateBarChange(enemyHP, (double) enemy.getCurrentHP() / enemy.getMaxHP());

        updateBars(player, enemy);

        if (damageDealt > 0) {
            boolean targetIsEnemy = (target == enemy);
            Node targetNode = targetIsEnemy ? enemySpriteBox : playerSpriteBox;
            UIEffects.shakeNode(targetNode);

            double xPos = targetIsEnemy ?
                    enemySpriteBox.localToScene(enemySpriteBox.getBoundsInLocal()).getMinX() + 50 :
                    playerSpriteBox.localToScene(playerSpriteBox.getBoundsInLocal()).getMinX() + 50;

            double yPos = layout.getCenter().getBoundsInParent().getMinY() + 100;

            String text = (isCritical ? "CRIT " : "") + damageDealt;
            String style = isCritical ? "crit-text" : "damage-text";
            UIEffects.showFloatingText(overlayPane, text, style, xPos, yPos);
        }

        if (battle.getBattleStatus() != BattleStatus.ONGOING) {
            javafx.application.Platform.runLater(() -> handleBattleEnd(battle.getBattleStatus()));
        }
    }

    private void updateBars(Character p, Character e) {
        // Logika sederhana untuk indikator Boss
        boolean isBoss = e.getLevel() >= 5 && e.getLevel() % 5 == 0;
        String bossIndicator = isBoss ? " 👑 BOSS" : "";

        enemyNameLbl.setText(String.format("%s (Lv.%d)%s", e.getName(), e.getLevel(), bossIndicator));
        playerNameLbl.setText(String.format("%s (Lv.%d) - %s", p.getName(), p.getLevel(), p.getStatus()));
    }

    private void executePlayerAction(ActionType type, Skill skill, Item item) {
        if (isPaused) return;

        Battle battle = MainFX.battleService.getCurrentBattle();
        Character player = battle.getPlayerTeam().get(0);
        Character enemy = battle.getEnemyTeam().get(0);

        int hpBefore = enemy.getCurrentHP();

        BattleAction action = new BattleAction(player, type);

        if (type == ActionType.ITEM && item != null) {
            action.setItem(item);
            action.setTarget(player);
        } else {
            action.setTarget(enemy);
        }

        if (skill != null) action.setSkill(skill);

        battle.executeAction(action);

        int damageDealt = Math.max(0, hpBefore - enemy.getCurrentHP());
        boolean isCrit = false;
        if(!battle.getBattleLog().getRecentEntries().isEmpty()){
            isCrit = battle.getBattleLog().getRecentEntries().get(battle.getBattleLog().getRecentEntries().size()-1).contains("CRITICAL");
        }

        updateUI(player, enemy, damageDealt, isCrit);

        if (battle.getBattleStatus() != BattleStatus.ONGOING) return;

        actionMenu.setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
        pause.setOnFinished(e -> {
            if (battle.getBattleStatus() != BattleStatus.ONGOING) return;
            int pHpBefore = player.getCurrentHP();

            AIStrategy ai = AIStrategyFactory.create(GameSettings.getInstance().getAIDifficulty());
            BattleAction enemyAction = ai.decideAction(enemy, battle.getEnemyTeam(), battle.getPlayerTeam());
            battle.executeAction(enemyAction);

            int pDamage = Math.max(0, pHpBefore - player.getCurrentHP());
            boolean pCrit = false;

            updateUI(enemy, player, pDamage, pCrit);
            actionMenu.setDisable(false);
        });
        pause.play();
    }

    // MENGGUNAKAN MEDIEVAL POPUP UNTUK HASIL BATTLE
    private void handleBattleEnd(BattleStatus status) {
        actionMenu.setDisable(true); // Kunci tombol

        // Auto Save
        if (MainFX.saveLoadService != null) {
            MainFX.saveLoadService.autoSave();
        }

        if (status == BattleStatus.VICTORY) {
            // Menang: Tanya mau lanjut atau keluar?
            MedievalPopup.showConfirm(rootStack, "VICTORY!",
                    "Enemy defeated!\nYou gained experience and loot.\n\nContinue to next battle?",
                    () -> { // YES -> Lanjut Battle
                        try {
                            Character player = MainFX.battleService.getCurrentBattle().getPlayerTeam().get(0);
                            List<Character> enemies = generateEnemyTeam(player.getLevel());
                            MainFX.battleService.startBattle(Collections.singletonList(player), enemies);

                            // Refresh Scene
                            MainFX.primaryStage.getScene().setRoot(new BattleScene().getLayout());
                        } catch (Exception e) {
                            MainFX.primaryStage.setScene(new MainMenuScene().getScene());
                        }
                    },
                    () -> { // NO -> Balik ke Menu
                        MainFX.primaryStage.setScene(new MainMenuScene().getScene());
                    }
            );
        } else {
            // Kalah: Hanya bisa keluar
            MedievalPopup.show(rootStack, "DEFEAT...",
                    "You have fallen in battle...\nBetter luck next time.",
                    MedievalPopup.Type.DEFEAT,
                    () -> MainFX.primaryStage.setScene(new MainMenuScene().getScene())
            );
        }
    }

    private List<Character> generateEnemyTeam(int playerLevel) {
        // Logic generate enemy (Boss setiap kelipatan 5)
        if (playerLevel >= 5 && playerLevel % 5 == 0) {
            String[] bossNames = {"Dragon", "Phoenix", "Golem", "Wraith", "Demon Lord"};
            String bossName = bossNames[(playerLevel / 5 - 1) % bossNames.length];
            return List.of(EnemyFactory.createBoss(bossName, playerLevel));
        } else {
            return List.of(EnemyFactory.createEnemy(playerLevel));
        }
    }
}